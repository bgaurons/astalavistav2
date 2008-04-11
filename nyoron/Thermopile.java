import java.util.concurrent.locks.*;

/**
<p>Thermopile.java - A driver for the thermopile array using the i2c/usb
	converter. This sensor has 8 sensors going along the long dimensions of
	the chip, and 32 pre-set positions if a servo is attached to it. It has
	a field of vision representing a hemisphere with 128 different points.
	of measurement.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>March 23, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Added logic that might help find hottest spots.</li>
		</ul>
		<li>March 21, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Reversed yesterday's change. Broke concurrency.</li>
		</ul>
		<li>March 20, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Sped up the retrieve by a factor of 8.</li>
		</ul>
		<li>March 14, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Made thread safety improvements.</li>
			<li>Added some debug output.</li>
			<li>Fixed bounding errors found with a test driver.</li>
		</ul>
		<li>March 13, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>First edition.</li>
			<li>Created comments.</li>
		</ul>
	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.2
 */

public class Thermopile extends I2CDevice
{

	//Constants

	//The default address for the Thermopile array
	//private static final byte THERMO_ADDRESS = (byte) 0x68;
	private static final byte THERMO_ADDRESS = (byte) 0xD0;

	//The addresses of the registers
	public static final byte CMD_REGISTER = 0x00;
	public static final byte TEMP_AMBIENT = 0x01;
	private static final byte TEMP_1 = 0x02;
	private static final byte TEMP_2 = 0x03;
	private static final byte TEMP_3 = 0x04;
	private static final byte TEMP_4 = 0x05;
	private static final byte TEMP_5 = 0x06;
	private static final byte TEMP_6 = 0x07;
	private static final byte TEMP_7 = 0x08;
	private static final byte TEMP_8 = 0x09;


	//Command register commands
	private static final byte SERVO_POS_MIN = 0x00;
	private static final byte SERVO_POS_MAX = 0x1F;
	private static final byte I2C_ADD_CHG_1 = (byte) 0xA0;
	private static final byte I2C_ADD_CHG_2 = (byte) 0xAA;
	private static final byte I2C_ADD_CHG_3 = (byte) 0xA5;

	//Some aliases for true and false for direction
	private static final boolean DOWN = true;
	private static final boolean UP = false;
	private static final boolean RIGHT = true;
	private static final boolean LEFT = false;

	//The amount of time that the thread needs to sleep to allow the servo
	//to adjust itself in milliseconds.
	private static final int SERVO_SLEEP = 5;

	//The size of the array storing all the temperatures.
	public static final int HOR_WIDTH = 32;
	public static final int VERT_WIDTH = 8;

	public static final int HOR_MIDDLE = HOR_WIDTH/2;

	//The offset in register number to make the correct array position
	private static final byte VERT_OFFSET = 0x02;

	//The offset in register number to make the correct array position
	private static final byte DEFAULT_THRESHHOLD = 40;


	/**
	The vertical position last read.
	It is a number between 2 and 9.
	*/
	private byte vertPos;

	/**
	Whether we are scanning up or down.
	True is down, false is up.
	*/
	private boolean vertDirection;

	/**
	The horizontal position last read.
	It is a number between 0 and 31. It is the last position the servo has
	been set to.
	*/
	private byte horPos;

	/**
	The temperature that needs to be crossed to trigger the threshhold.
	*/
	public byte threshhold;

	/**
	The column that the threshhold has been crossed at.
	In degrees, column 31 is 90 degrees left of the front, and column 0 is 90
	degrees to the right.
	*/
	private byte threshholdColumn;


	/**
	Tells whether the threshhold has been crossed.
	*/
	private boolean threshholdCrossed;


	/**
	Whether we are scanning up or down.
	True is right, false is left.
	*/
	private boolean horDirection;

	/**
	Stores all temperatures
	*/
	private byte[][] temperatures;


	/**
	Keeps the sweep operation mutually exclusive.
	This is so that no one modifies sweep variables at the same time.
	*/
	private ReentrantLock sweepLock;

	/**
	Ensures proper waits between commands sent to the servo.
	The Servo automatically stops if it receives any commands.
	This will keep any commands from being sendable until an appropriate
	wait occurs.
	*/
	private ReentrantLock servoLock;

	/**
	Ensures proper waits between commands sent to the servo.
	The Servo automatically stops if it receives any commands.
	This will keep any commands from being sendable until an appropriate
	wait occurs.
	*/
	private ReentrantLock arrayLock;

	/**
	Concurrency threshhold locks. Used for variables that modify the
	threshhold. It allows access to said variables.
	*/
	private ReentrantLock threshholdLock;

	/**
	An automatically started thread that starts the sweeping logic to read new
	temperatures all the time
	*/
	public SweepThread sweepThread;





        /**
	Constructor. Gives access to wheels attached to the I2CChannel passed
	as a paramer
	@param		channel		The channel the thermopile array is on.
	@author		Benjamin Gauronskas
         */
	public Thermopile(I2CChannel channel)
	{
		super(channel, THERMO_ADDRESS);

		vertPos = TEMP_1;
		vertDirection = DOWN;
		horPos = SERVO_POS_MIN;
		horDirection = RIGHT;

		sweepLock = new ReentrantLock();
		servoLock = new ReentrantLock();
		arrayLock = new ReentrantLock();
		threshholdLock = new ReentrantLock();

		temperatures = new byte[HOR_WIDTH][VERT_WIDTH];

		threshhold = DEFAULT_THRESHHOLD;
		threshholdCrossed = false;

		//Initialize it to start position
		moveServo();

		//And start sweeping
		sweepThread = new SweepThread(this);
	}


        /**
	Reads the temperature at the current position then readies for the next
	position.
	@return		A byte representing the temperature at the current
			location
	@author		Benjamin Gauronskas
         */
	public byte sweepTemp()
	{
		//hor toggle tells us that we have moved horizontally. Do not
		//move vertically.
		boolean horToggle = false;
		byte returnVal = 1;


		sweepLock.lock();
		returnVal = readTemp();
		arrayLock.lock();
		temperatures[(HOR_WIDTH-1)-horPos][vertPos-VERT_OFFSET] = returnVal;
		arrayLock.unlock();

		if(returnVal >= threshhold){
			setThreshhold(returnVal, horPos);
		}



		//Now get ready to read the next location. First point the
		//horizontal view correctly

		//If we are at the top and still going up or the bottom and
		//going down.
		if ((vertPos == TEMP_1 && vertDirection == UP) ||
		(vertPos == TEMP_8 && vertDirection == DOWN)){
			//Copy the pointer of the old column to the accessible array.
			//Then make a new array.

			if (horDirection == LEFT){
				horPos--;
				moveServo();
				//If we reach the left side, go the other way.
				if (horPos == SERVO_POS_MIN){
					horDirection = RIGHT;

					new UpdateThread(this);
				}
			}
			else{
				horPos++;
				moveServo();
				//If we reach the right side, go the other way.
				if (horPos == SERVO_POS_MAX){
					horDirection = LEFT;
					new UpdateThread(this);
				}
			}

			//reverse vertical direction
			vertDirection = !vertDirection;

			//prevent from moving vertical as well.
			horToggle = true;
		}


		//Now set the vertical view correctly.
		if (!horToggle){
			if (vertDirection == DOWN)
				vertPos++;
			else
				vertPos--;
		}

		sweepLock.unlock();

		return returnVal;


	}

        /**
	Reads the ambient temperature in the current direction.
	@return		A byte representing the temperature in the current
			direction.
	@author		Benjamin Gauronskas
         */
	public byte readAmbientTemp()
	{
		servoLock.lock();
		byte returnVal = readByte(TEMP_AMBIENT);
		servoLock.unlock();
		return returnVal;

	}

        /**
	Reads the temperature in the current direction and position.
	@return		A byte representing the temperature in the current
			direction and position.
	@author		Benjamin Gauronskas
         */
	public byte readTemp()
	{
		servoLock.lock();
		byte returnVal = readByte(vertPos);
		servoLock.unlock();
		return returnVal;

	}

        /**
	Changes the servo position and ensures adequate thread sleep.

	@author		Benjamin Gauronskas
         */
	public void moveServo()
	{
		try{
			servoLock.lock();
			command(CMD_REGISTER, horPos);
			Thread.sleep(SERVO_SLEEP);
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}
		finally{
			servoLock.unlock();
		}
	}

    /**
	Sets the crossed threshhold, and changes the threshholdCrossed
	boolean to true.

	@author			Benjamin Gauronskas
	@param	temp	The new threshhold
	@param	column	The column where the threshhold was crossed.
    */
	public void setThreshhold(byte temp, byte column)
	{
		threshholdLock.lock();
		threshhold = temp;
		threshholdCrossed = true;
		threshholdColumn = column;
		threshholdLock.unlock();

	}

    /**
	Tells if the threshhold was crossed.

	@return			Returns whether the threshhold was crossed.

	@author			Benjamin Gauronskas

    */
	public boolean thresholdCrossed()
	{
		boolean returnVal;

		threshholdLock.lock();
		returnVal = threshholdCrossed;
		threshholdCrossed = false;
		threshholdLock.unlock();

		return returnVal;

	}

    /**
	Tells the last column that the threshhold was crossed at

	@return			The last column the threshhold was crossed.

	@author			Benjamin Gauronskas

    */
	public byte getHotColumn()
	{
		byte returnVal;

		threshholdLock.lock();
		returnVal = threshholdColumn;
		threshholdLock.unlock();

		return returnVal;

	}


        /**
	Returns a 32x8 array of temperatures.
	@return		the temperature array
	@author		Benjamin Gauronskas
         */
	public byte[][] getTemperatures()
	{
		byte[][] returnValue = new byte[HOR_WIDTH][VERT_WIDTH];

		for(byte i = 0; i < HOR_WIDTH; i++)
			for(byte j = 0; j < VERT_WIDTH; j++){
				arrayLock.lock();
				returnValue[i][j] = temperatures[i][j];
				arrayLock.unlock();
			}



		return returnValue;

	}

        /**
	Gives a string containing all the information on positioning
	@return		A string that tells which position will be measured next.
	@author		Benjamin Gauronskas
         */
	public String posString()
	{
		sweepLock.lock();
		//Vertical
		String returnVal = "Vertical:\tDirection: ";
		if (vertDirection == UP)
			returnVal += "Up";
		else
			returnVal += "Down";
		returnVal += "\t\tPosition: " + vertPos + "\n";
		//Horizontal
		returnVal += "Horizontal:\tDirection: ";
		if (horDirection == LEFT)
			returnVal += "Left\t";
		else
			returnVal += "Right";
		returnVal += "\tPosition: " + horPos + "\n";

		sweepLock.unlock();

		return returnVal;

	}

	/**
	 * <p>Sweep thread keeps running the sweep temp method over and over in
	 * order to collect more temperatures.</p>
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1
	 */
	private static class SweepThread implements Runnable
	{

		public Thread t;
		public Thermopile thermopile;


		// Constructor initilized vars
		/**
		 * Constructor takes the thermopile object that makes it.
		 *
		 *
		 * @param	thermopile		The constructing object.
		 * @author			Benjamin Gauronskas
		 */
		public SweepThread(Thermopile thermopile)
		{

			this.thermopile = thermopile;
			t = new Thread(this,"Temp Get");
			t.start();
		}

		/**
		 * Just reads temperatures forever.
		 *
		 *
		 * @author			Benjamin Gauronskas
		 */
		public void run()
		{
			while(true){
					thermopile.sweepTemp();

			}
		}
	}


	/**
	 * <p>Update thread sends an update to the server.</p>
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1
	 */
	private static class UpdateThread implements Runnable
	{

		public Thread t;
		public Thermopile thermopile;


		// Constructor initilized vars
		/**
		 * Constructor takes the thermopile object that makes it.
		 *
		 *
		 * @param	thermopile		The constructing object.
		 * @author			Benjamin Gauronskas
		 */
		public UpdateThread(Thermopile thermopile)
		{

			this.thermopile = thermopile;
			t = new Thread(this,"Temp Send");
			t.start();
		}

		/**
		 * Sends the current readings to the server.
		 *
		 *
		 * @author			Benjamin Gauronskas
		 */
		public void run()
		{
			ThermMessage msg = new ThermMessage(thermopile.getTemperatures());
			Registers.connection.sendMessage((Message)msg);
		}
	}

} // end Thermopile class

