import java.util.concurrent.locks.*;

/**
<p>Motor.java - A driver for the motorboard using the i2c/usb converter.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>April 1, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Modified concurrency for higher level logic.</li>
		</ul>
		<li>March 23, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Added constants and logic for turning.</li>
			<li><b>NOTE: it is not complete and should be fixed later.</b></li>
		</ul>
		<li>March 13, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Compiled.</li>
			<li>Fixed bugs. Overloaded some methods to reduce compiler
				warnings.</li>
		</ul>
		<li>March 12, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Wrote; did not even test.</li>
			<li>Created comments.</li>
		</ul>
		<li>???, ???</li>
		<ul>
			<li>Someone made this, right?</li>
		</ul>
	</ul>


  @author                      ???
  @version                     0.2
 */

public class Motor extends I2CDevice implements Runnable
{



	//Constants

	//The default address for the motor
	private static final byte MOTOR_ADDRESS = (byte) 0xB0;

	//The addresses of the registers
	private static final byte RIGHT_REGISTER = 0x00;
	private static final byte LEFT_REGISTER = 0x01;
	private static final byte VOLT_REGISTER = 0x0A;
	private static final byte ACCEL_REGISTER = 0x0D;
	private static final byte MODE_REGISTER = 0x0F;
	private static final byte CMD_REGISTER = 0x10;

	//Command register commands
	private static final byte ENCODE_ZERO = 32;
	private static final byte SPEED_DEREG = 48;
	private static final byte SPEED_REG = 49;
	private static final byte TWO_SEC_TO_DISABLE = 50;
	public static final byte TWO_SEC_TO_ENABLE = 51;
	private static final byte I2C_ADD_CHG_1 = (byte) 0xA0;
	private static final byte I2C_ADD_CHG_2 = (byte) 0xAA;
	private static final byte I2C_ADD_CHG_3 = (byte) 0xA5;

	//Mode register commands
	private static final byte INDEPENDANT_UNSIGNED = 0;
	public static final byte INDEPENDANT_SIGNED = 1;
	private static final byte DEPENDANT_UNSIGNED = 2;
	private static final byte DEPENDANT_SIGNED = 3;

	private static final int SLEEP_TIME = 100;

	private static final byte DEFAULT_ACCELERATION = 10;

	private static final byte DEFAULT_SPEED = 30;


	//This is how long it takes to turn unit radians with the robot.
	//if we multiply this by the amount we are turning, then we get the correct
	//sleep amount.
	private static final int SLEEP_UNIT = 900;



	//This is a guess, but I hope to get an estimate of the amount of time
	//it takes to turn a given amount using DEFAULT_SPEED.
	private static final int SLEEP_PI = (int)(900* Math.PI);
	private static final int SLEEP_2_PI = SLEEP_PI*2;

/*
WE RECEIVE AN ANGLE FROM THE SERVER IN RADIANS... THE SMALLEST ANGLE IS PI/30
(6 DEGREES)

WE NEED TO CONVERT THIS ANGLE INTO A TIME... BUT WHAT IS THE CONVERSION FACTOR...


IF WE KNOW HOW LONG IT TAKES TO TURN PI... HOW CAN WE FIND THE AMOUNT OF TIME
IT TAKES TO TURN X?

WE TAKE THE TIME IT TAKES TO TURN PI AND THEN WE DIVIDE IT BY PI.
NOW WE CAN MULTIPLY THIS BY X AND WE HAVE THE AMOUNT OF TIME IT TAKES TO ROTATE
X.
*/



	/**
	A constant to make the turning method easier to read.
	*/
	public static final boolean LEFT = false;

	/**
	A constant to make the turning method easier to read.
	*/
	public static final boolean RIGHT = true;


	/**
	How fast to travel forward.
	I do not know about the scale
	*/
	public byte forward;
	/**
	How fast to turn.
	I do not know about the scale
	*/
	public byte turn;

	/**
	Whether the thread is running or not.
	No method is given to turn this off... yet.
	*/
	public boolean go;

	/**
	Controls the concurrency of the motor controls.
	*/
	private ReentrantLock motorLock;

	/**
	Logic lock locks chunks of logic that are doing "intelligent" things to the
	motors. It should be never be held by the thread that holds motorLock. The
	thread that holds logicLock may hold the motorLock, however.
	*/
	private ReentrantLock logicLock;


        /**
	Constructor. Gives access to wheels attached to the I2CChannel passed
	as a paramer
	@param		channel		The channel the wheels are on.
	@author		Benjamin Gauronskas
         */
	public Motor(I2CChannel channel)
	{
		super(channel, MOTOR_ADDRESS);

		command(TWO_SEC_TO_ENABLE); // turn off automatic motor timeout

		//mode(DEPENDANT_SIGNED);
		mode(INDEPENDANT_SIGNED);
				// set mode to one -127: full reverse,
				// 0: stop, 127:
				//full forward motors controlled together with
				//left as turn constant
		motorLock = new ReentrantLock();
		logicLock = new ReentrantLock();

		rightMotor(0);	// set forward motor speed to stop
		leftMotor(0);	// set turn motor speed to stop
		accel(DEFAULT_ACCELERATION);

		forward = 0;
		turn = 0;
		go = true;

		Registers.connection.motorStarted = true;


	}

        /**
	Reads or writes to and from the motor controller.
	@author		???
         */
	public void run()
	{
		try
		{
			do
			{
				rightMotor(forward);
				Thread.sleep(SLEEP_TIME);
				leftMotor(turn);
				Thread.sleep(SLEEP_TIME);
			}
			while(go);
		}
		catch(Exception ex)
		{
			System.out.println("Exception in motors");
			// turn automatic timeout on
			command(TWO_SEC_TO_ENABLE);
		}

		//motors.close(); // close communication with motors
	} // end run method

        /**
	Sets forward speed.
	@param		num	The new speed
	@author		???
         */
	public void setForward(byte num)
	{
		forward = num;
		turn = 0;
	}


        /**
	Sets motor speeds
	@param		forward	Right motor
	@param		turn	Left motor
	@author		Benjamin Gauronskas
         */
	public void setMotors(byte forward, byte turn)
	{
		this.forward = forward;
		this.turn = turn;
	}


        /**
	Sets motor speeds
	@param		forward	Right motor
	@param		turn	Left motor
	@author		Benjamin Gauronskas
         */
	public void setMotors(int forward, int turn)
	{
		setMotors((byte)forward, (byte) turn);
	}

        /**
	Sets forward speed.
	@param		num	The new speed
	@author		???
         */
	public void setForward(int num)
	{
		setForward((byte)num);
	}


        /**
	Sets turn speed.
	@param		num	The new speed
	@author		???
         */
	public void setTurn(byte num)
	{
		forward = 0;
		turn = num;
	}

        /**
	Sets turn speed.
	@param		num	The new speed
	@author		???
         */
	public void setTurn(int num)
	{
		setTurn((byte)num);
	}

    /**
	I am almost certain that this method will not work as hoped without some
	calibration of the motors and the like, but it is here to help the people
	working on AI have some sort of tools to program with. Use constants
	Motor.LEFT or Motor.RIGHT as a parameter to choose direction.
	@param		direction	Direction to turn the Robot. and then stop it.
	@author		Benjamin Gauronskas
    */
	public void turn90(boolean direction)
	{
		//Stop forward motor.
		rightMotor(0);
		//Turn the turn motor DEFAULT_SPEED in the right direction.
		if(direction == RIGHT){
			leftMotor(DEFAULT_SPEED);
		}
		else{
			leftMotor(-1*DEFAULT_SPEED);
		}
		//Now sleep until we get to where we need
		try{
			Thread.sleep(SLEEP_PI);
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}

		//Stop the turn motor.
		leftMotor(0);
	}

    /**
	The motors will turn the device by the given angle.
	@param		turnAngle	Direction to turn the Robot. and then stop it.
	@author		Benjamin Gauronskas
    */
	public void turn(double turnAngle)
	{
		logicLock.lock();
		//Stop forward motor.
		setMotors(0, 0);
		//Turn the turn motor DEFAULT_SPEED in the right direction.
		//if(direction == RIGHT){
		if(turnAngle > 0)
			setMotors(DEFAULT_SPEED,-1*DEFAULT_SPEED);
		else
			setMotors(-1*DEFAULT_SPEED, DEFAULT_SPEED);
		//}
		//else{

		//}
		//Now sleep until we get to where we need
		try{
			Thread.sleep((int)Math.abs(turnAngle*SLEEP_UNIT));
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}

		//Stop the turn motor.
		leftMotor(0);
		logicLock.unlock();

	}





        /**
	Sends a command to the command register. Commands are as follows:
	<ul>
		<li>ENCODE_ZERO: Resets encoder registers to zero</li>
		<li>SPEED_DEREG: Disables Speed Regulation</li>
		<li>SPEED_REG: Enables speed regulation (default)</li>
		<li>TWO_SEC_TO_DISABLE: Disables 2 second timeout of
			motors</li>
		<li>TWO_SEC_TO_ENABLE: Enables 2 second timeout of motors when
			there is no I2C communication</li>
		<li>I2C_ADD_CHG_1: 1st in sequence to change i2c address</li>
		<li>I2C_ADD_CHG_2: 2nd in sequence to change i2c address</li>
		<li>I2C_ADD_CHG_3: 3rd in sequence to change i2c address</li>
	</ul>

	@param		cmd		The command to send
	@author		Benjamin Gauronskas
         */
	public void command(byte cmd)
	{
		super.command(CMD_REGISTER, cmd);
	}

        /**
	Sends a command to the command register. Commands are as follows:
	<ul>
		<li>ENCODE_ZERO: Resets encoder registers to zero</li>
		<li>SPEED_DEREG: Disables Speed Regulation</li>
		<li>SPEED_REG: Enables speed regulation (default)</li>
		<li>TWO_SEC_TO_DISABLE: Disables 2 second timeout of
			motors</li>
		<li>TWO_SEC_TO_ENABLE: Enables 2 second timeout of motors when
			there is no I2C communication</li>
		<li>I2C_ADD_CHG_1: 1st in sequence to change i2c address</li>
		<li>I2C_ADD_CHG_2: 2nd in sequence to change i2c address</li>
		<li>I2C_ADD_CHG_3: 3rd in sequence to change i2c address</li>
	</ul>

	@param		cmd		The command to send
	@author		Benjamin Gauronskas
         */
	public void command(int cmd)
	{
		command((byte)cmd);
	}



        /**
	Controls speed of right motor or speed of both motors when in mode
	2 or 3.

	@param		speed		The new speed
	@author		Benjamin Gauronskas
         */
	public boolean rightMotor(byte speed)
	{
		boolean returnVal;
		motorLock.lock();
		returnVal = super.command(RIGHT_REGISTER, speed);
		motorLock.unlock();
		return returnVal;
	}


        /**
	Controls speed of right motor or speed of both motors when in mode
	2 or 3.

	@param		speed		The new speed
	@author		Benjamin Gauronskas
         */
	public boolean rightMotor(int speed)
	{
		return rightMotor((byte) speed);
	}


        /**
	Controls speed of left motor or speed of both motors when in mode
	2 or 3.

	@param		speed		The new speed
	@author		Benjamin Gauronskas
         */
	public boolean leftMotor(byte speed)
	{
		boolean returnVal;
		motorLock.lock();
		returnVal = super.command(LEFT_REGISTER, speed);
		motorLock.unlock();
		return returnVal;
	}


        /**
	Controls speed of left motor or speed of both motors when in mode
	2 or 3.

	@param		speed		The new speed
	@author		Benjamin Gauronskas
         */
	public boolean leftMotor(int speed)
	{
		return leftMotor((byte) speed);
	}


        /**
	Changes the mode. Modes are as follows:
	<ul>
		<li>INDEPENDANT_UNSIGNED : Motors are controlled independantly
			with range of 0 (stop) to 255 (full forward)</li>
		<li>INDEPENDANT_SIGNED : Motors are controlled independently
			with range of -127 full reverse to 127 full
			forward</li>
		<li>DEPENDANT_UNSIGNED : Motors are controlled By the same
			register unsigned Second register is turn constant</li>
		<li>DEPENDANT_SIGNED : Motors are controlled by the same
			register signed Second register is turn constant</li>
	</ul>

	@param		mode		The mode to change to.
	@author		Benjamin Gauronskas
         */
	public void mode(byte mode)
	{
		super.command(MODE_REGISTER, mode);
	}

        /**
	Changes the mode. Modes are as follows:
	<ul>
		<li>INDEPENDANT_UNSIGNED : Motors are controlled independantly
			with range of 0 (stop) to 255 (full forward)</li>
		<li>INDEPENDANT_SIGNED : Motors are controlled independently
			with range of -127 full reverse to 127 full
			forward</li>
		<li>DEPENDANT_UNSIGNED : Motors are controlled By the same
			register unsigned Second register is turn constant</li>
		<li>DEPENDANT_SIGNED : Motors are controlled by the same
			register signed Second register is turn constant</li>
	</ul>

	@param		mode		The mode to change to.
	@author		Benjamin Gauronskas
         */
	public void mode(int mode)
	{
		mode((byte)mode);
	}


        /**
	controls acceleration of motors:
	   steps to new speed = (new speed - old speed) / acceleration value
	   time to new speed = steps * 25 ms

	@param		accel		The new acceleration
	@author		Benjamin Gauronskas
         */
	public void accel(byte accel)
	{
		super.command(ACCEL_REGISTER, accel);
	}

        /**
	controls acceleration of motors:
	   steps to new speed = (new speed - old speed) / acceleration value
	   time to new speed = steps * 25 ms

	@param		accel		The new acceleration
	@author		Benjamin Gauronskas
         */
	public void accel(int accel)
	{
		super.command(ACCEL_REGISTER, (byte) accel);
	}


        /**
	Returns battery voltage. If negative one is returned, there was an
	   error when communicating with motor driver.
	@return		It will return voltage unless their is a problem, then
			it will return -1.
	@author		Benjamin Gauronskas
         */
	public long getVoltage()
	{
		return readLong(VOLT_REGISTER);
	}


} // end motor class

