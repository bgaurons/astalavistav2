import java.util.Date;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.lang.InterruptedException;
import java.util.concurrent.locks.*;
import java.awt.Color;
/**

MovementLogic.java
This file keeps track of all movement and geographical information that the
robot is involved in. It takes most of the logic that was originally in
BotPanel and moves it from the server to the robot, as is part of the project
requirements for the robot.

	<h1>Revision History:</h1>
	<ul>
		<li>March 27, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Created file.</li>
		</ul>

	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.1
*/
public class MovementLogic
{

	/**
	The amount of inches traversed in a single millisecond
	*/
	public static final double RADIANS_PER_MILLISECOND = Math.PI/5000;


	/**
	A small turn used for avoidance logic
	*/
	public static final double INCREMENT_ANGLE = Math.PI/4;

	/**
	The amount of inches traversed in a single millisecond
	*/
	public static final double INCHES_PER_MILLISECOND = 0.001;

	/**
	The amount of inches traversed in a single millisecond
	*/
	public static final int INCHES_PER_SECOND =
											(int)(INCHES_PER_MILLISECOND*1000);

	/**
	Designates the robot turning left.
	*/
	public static final byte LEFT = 0x00;
	/**
	Designates the robot turning right.
	*/
	public static final byte RIGHT = 0x01;
	/**
	Designates the robot moving forward.
	*/
	public static final byte UP = 0x02;
	/**
	Designates the robot moving forward.
	*/
	public static final byte FORWARD = 0x02;
	/**
	Designates the robot moving backward.
	*/
	public static final byte DOWN = 0x03;
	/**
	Designates the robot moving backward.
	*/
	public static final byte BACKWARD = 0x03;
	/**
	Designates stopping the robot's motors..
	*/
	public static final byte STOP = 0x04;
	/**
	No direction was given.
	*/
	public static final byte NO_DIRECTION = (byte)0xFF;

	/**
	Default X coordinate value.
	*/
	public static final int X_DEFAULT = 400;

	/**
	Default Y coordinate value.
	*/
	public static final int Y_DEFAULT = 400;

	/**
	Default angle.
	*/
	public static final int ANGLE_DEFAULT = 0;

	/**
	Default speed.
	*/
	public static final int SPEED_DEFAULT = 50;

	/**
	The amount of inches that an object must be within for the robot to decide
	to take prevantitive actions to avoid it
	*/
	public static final int DISTANCE_THRESHOLD = 20;

	/**
	The smallest amount that the robot will turn. This is 6 in degrees, by the
	way.
	*/
	public static final double TURN_UNIT = Math.PI/30;

	/**
	The regular period for sending new coordinates, angle, and speed of the
	robot.
	*/
	public static final int SERVER_XY_UPDATE_TIME = 200;


	/**
	The robot's current moving speed. Hopefully in some intelligent unit one
	day. This is the speed that the motors think in, not the speed that the
	GUI uses. The GUI uses speed/BotPanel.SPEED_DIVISOR.
	*/
	protected static int speed = SPEED_DEFAULT;


	/**
	The direction the robot is turning.
	*/
	protected static int turnDirection = LEFT;;

	/**
	The robot's X coordinate in inches.
	*/
	protected static int x = X_DEFAULT;

	/**
	The robot's Y coordinate in inches.
	*/
	protected static int y = Y_DEFAULT;

	/**
	The last place the robot stood still.
	*/
	protected static int lastX = X_DEFAULT;

	/**
	The last place the robot stood still
	*/
	protected static int lastY = Y_DEFAULT;


	/**
	The robot's angle in Radians. Read comment in BotPanel for information
	*/
	protected static double angle = ANGLE_DEFAULT;

	/**
	The robot's angle in Radians. Read comment in BotPanel for information
	*/
	protected static double lastAngle = ANGLE_DEFAULT;

	/**
	The direction the robot is travelling
	*/
	protected static boolean forward = true;

	/**
	The floor plan of the maze.
	*/
	protected static BufferedImage map;

	/**
	This lock should be acquired before changing or reading xy coordinates, and
	released afterwards
	*/
	protected static ReentrantLock coordLock = new ReentrantLock();

	/**
	Makes sure not too much is being sent to the motor.
	*/
	protected static ReentrantLock manualLock = new ReentrantLock();

	/**
	This sends the robot's coordinates to the server after the interval
	SERVER_XY_UPDATE_TIME
	*/
	private static ServerUpdate serverUpdate = new ServerUpdate();

	/**
	This updates the robot's coordinates.
	*/
	private static XYUpdate xyUpdate;

	/**
	This updates the robot's coordinates.
	*/
	private static AngleUpdate angleUpdate;

	/**
	This updates the map.
	*/
	private static MapIR mapIR;


	/**
	Initializes things for movement logic. Ostensibly, it can be longer, but
	for now it only sets the map file.
	@param		mapFile	The filename where the map is stored.
	@author		Benjamin Gauronskas
	*/
	public static void initialize(String mapFile)
	{
		try {
			map = ImageIO.read(new File(mapFile));
		} catch (IOException e) {
			System.out.println("Reading map failed");

		}

		System.out.println("THINGS ARE STARTED RIGHT?!");

		xyUpdate = new XYUpdate();
		angleUpdate = new AngleUpdate();


		mapIR = new MapIR();
		System.out.println("THINGS ARE STARTED RIGHT?!");
		stopMotor();
		stopTurning();

	}


	/**
	Updates the robot's internal mapping coordinates.
	@param		direction	The direction that the robot is moving in
	@author		Benjamin Gauronskas
	*/
	public static void calculateMan(byte direction)
	{
		manualLock.lock();
		Message posMessage;
		//Two cases are commented out, because the compiler complains that they
		//are duplicates. I am leaving them in, because I think it helps
		//with readability.
		switch(direction){
			case RIGHT:
				stopMotor();
				//angle = (angle + (TURN_UNIT)) % (2*Math.PI);
				//Registers.motor.turn(TURN_UNIT);
				turnDirection = RIGHT;
				startTurning();
				Registers.motor.setMotors(speed, -1*speed);
				break;

			case LEFT:
				stopMotor();
				//angle = (angle - (TURN_UNIT)) % (2*Math.PI);
				//Registers.motor.turn(-1*TURN_UNIT);
				turnDirection = LEFT;
				startTurning();
				Registers.motor.setMotors(-1*speed, speed);
				break;
			//case UP:
			case FORWARD:
				if(!forward)
					stopMotor();

				forward = true;
				stopTurning();
				Registers.motor.setMotors(speed, speed);
				startMotor();
				break;

			//case DOWN:
			case BACKWARD:
				if(forward)
					stopMotor();
				forward = false;
				stopTurning();
				Registers.motor.setMotors(-1*speed, -1*speed);
				startMotor();

				break;
			case STOP:
				stopMotor();
				stopTurning();
			case NO_DIRECTION:
			default: break;
		}
		manualLock.unlock();

	}


	/**
	Makes the robot go forward.
	@author		Benjamin Gauronskas
	*/
	public static void forward(){

		if(!forward)
			stopMotor();

		forward = true;
		stopTurning();
		Registers.motor.setMotors(speed, speed);
		startMotor();
	}

	/**
	Turns the robot angle radians.
	@author		Benjamin Gauronskas
	*/
	public static void turn(double angle){
		if(angle < 0){
			turnDirection = LEFT;

		}
		else if(angle >= 0){
			turnDirection = RIGHT;
		}

		startTurning();
		Registers.motor.turn(angle);
		stopTurning();

	}

	/**
	starts or restarts the thread that sends server updates
	@author		Benjamin Gauronskas
	*/
	private static void startMotor(){
		coordLock.lock();
		if(xyUpdate != null)
			xyUpdate.stopped = false;
		coordLock.unlock();
	}

	/**
	Stops the motor
	@author		Benjamin Gauronskas
	*/
	public static void stopMotor(){


		Registers.motor.setMotors(0,0);
		coordLock.lock();
		if(xyUpdate != null)
			xyUpdate.stopped = true;
		lastX = x;
		lastY = y;
		coordLock.unlock();
	}


	/**
	Stop turning
	@author		Benjamin Gauronskas
	*/
	public static void stopTurning(){


		coordLock.lock();
		if(angleUpdate != null)
			angleUpdate.stopped = true;
		lastAngle = angle;
		coordLock.unlock();
	}
	/**
	Start turning
	@author		Benjamin Gauronskas
	*/
	public static void startTurning(){
		coordLock.lock();
		if(angleUpdate != null)
			angleUpdate.stopped = false;
		coordLock.unlock();
	}

	/**
	Tells the robot to go straight however many inches are passed into the
	method. A negative number will move the robot in reverse.
	@param	inches	inches to move.
	@author		Benjamin Gauronskas
	*/
	public static void Straight(int inches){
		boolean isNegative = inches < 0;
		int absInches = Math.abs(inches);
		if(isNegative){
			Registers.motor.setMotors(speed*-1,speed*-1);
			startMotor();
		}
		else{
			Registers.motor.setMotors(speed, speed);
			startMotor();
		}

		//Sleep until it has travelled far enough.
		try{
			Thread.sleep(absInches*INCHES_PER_SECOND);
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}
		stopMotor();
	}

	/**
	Makes sure that the robot does not go out of the designated boundaries.
	@author		Benjamin Gauronskas
	*/
	private static void boundaryCheck(){
		if (y+BotPanel.ROBOT_RADIUS_PIXELS < 0){
			coordLock.lock();
			y = BotPanel.ROBOT_RADIUS_PIXELS;
			coordLock.unlock();
			stopMotor();
		}
		if (x-BotPanel.ROBOT_RADIUS_PIXELS >
				BotPanel.WINDOW_FEET_X*BotPanel.PPF){
			coordLock.lock();
			x = (BotPanel.WINDOW_FEET_X*BotPanel.PPF) -
				BotPanel.ROBOT_RADIUS_PIXELS;
			coordLock.unlock();
			stopMotor();
		}
		if (y-BotPanel.ROBOT_RADIUS_PIXELS >
				BotPanel.WINDOW_FEET_X*BotPanel.PPF){

			coordLock.lock();
			y = (BotPanel.WINDOW_FEET_Y*BotPanel.PPF) -
				BotPanel.ROBOT_RADIUS_PIXELS;
			coordLock.unlock();
			stopMotor();
		}
		if (x+BotPanel.ROBOT_RADIUS_PIXELS < 0){
			coordLock.lock();
			x = BotPanel.ROBOT_RADIUS_PIXELS;
			coordLock.unlock();
			stopMotor();
		}

	}

    /**
	Checks if there has been a collision with a wall, and reverses robot
	movement.

	@author		Benjamin Gauronskas
    */
	public static void edgeDetect()
	{
		int red = Color.red.getRGB();
		coordLock.lock();
		int xMax = x + BotPanel.ROBOT_RADIUS_PIXELS;
		int xMin = x - BotPanel.ROBOT_RADIUS_PIXELS;
		int yMax = y + BotPanel.ROBOT_RADIUS_PIXELS;
		int yMin = y - BotPanel.ROBOT_RADIUS_PIXELS;
		coordLock.unlock();

		//To do edge detection use...
		//map.getRGB(x,y).equals(Color.red)
		//where x and y are the coordinates of the robot... if these are the
		//same then we need to reverse direction.
		if(	xMax < map.getWidth() && yMax < map.getHeight() &&
			xMin >= 0 && yMin >= 0){
			if(	map.getRGB(xMin, yMin) == red ||
				map.getRGB(xMin, yMax) == red ||
				map.getRGB(xMax, yMin) == red ||
				map.getRGB(xMax, yMax) == red){

				System.out.println("Crossed edge detected.");
			}


		}

	}

    /**
	Tries to determine what prevantitive measures need to be taken to avoid
	whatever may be in front of the robot.

	@author		Benjamin Gauronskas
    */
	public static void avoidance(int forwardDistance)
	{
		stopMotor();
		stopTurning();

		coordLock.lock();
		int originalX = x;
		int originalY = y;
		double originalAngle = angle;
		coordLock.unlock();


		//We need to turn some amount.
		turn(INCREMENT_ANGLE);
		//Scan the direction in front of us.

		int leftDistance = Registers.arduino.getIRSensor();

		turn(INCREMENT_ANGLE*-2);
		//if(facingDistance > DISTANCE_THRESHOLD)

		int rightDistance = Registers.arduino.getIRSensor();

		//If the two distances are both far.
		if(
			rightDistance > DISTANCE_THRESHOLD &&
			leftDistance > DISTANCE_THRESHOLD){
			plotPoint(	originalX,
						originalY,
						forwardDistance,
						originalAngle,
						Color.blue);

		}else if(rightDistance > DISTANCE_THRESHOLD){
			Registers.motor.setMotors(speed, speed);
			startMotor();
		}else{
			turn(INCREMENT_ANGLE * 2);

			Registers.motor.setMotors(speed, speed);
			startMotor();
		}





	}

    /**
	Makes the robot go forward.

	@author		Benjamin Gauronskas
    */
	public static void robotForward(){
		stopTurning();
		Registers.motor.setMotors(speed, speed);
		startMotor();

	}

    /**
	Makes the robot go backwards

	@author		Benjamin Gauronskas
    */
	public static void robotBackward(){
		stopTurning();
		Registers.motor.setMotors(-1*speed, -1*speed);
		startMotor();

	}


    /**
	Makes the robot stop doing things

	@author		Benjamin Gauronskas
    */
	public static void stopRoaming(){
		stopTurning();
		stopMotor();

	}


    /**
	Plots a point
	@param	xR			X coordinate of the robot
	@param	yR			Y coordinate of the robot
	@param	distanceP	Distance to the plot point.
	@param	angleR		Distance to the plot point.
	@param	color		The color to plot the point in.

	@author		Benjamin Gauronskas
    */
	public static void plotPoint(	int xR,
									int yR,
									int distanceP,
									double angleR,
									Color color ){

		int red = Color.red.getRGB();
		int blue = Color.blue.getRGB();

		int mapLength = BotPanel.PPI * distanceP;

		int plotX = (int)(mapLength * Math.cos(angleR));
		int plotY = (int)(mapLength * Math.sin(angleR));
		if(map.getRGB(plotX, plotY) != blue)
			map.setRGB(plotX, plotY, red);
	}


	/**
	Angle Update will update the xy coordinates internally to the robot.

	@author			Benjamin Gauronskas
	@version			0.1
	*/
	private static class AngleUpdate implements Runnable
	{

		public Thread t;

		public boolean stopped;


		/**
		Constructor takes no arguments.





		@author			Benjamin Gauronskas
		*/
		public AngleUpdate()
		{
			stopped = true;
			System.out.println("AngleUpdate STARTED RIGHT?!");
			t = new Thread(this,"Angle Update");
			t.start();
		}

		/**
		This is the infinite loop that waits the appropriate amount of time and
		then sends an update to the server.

		@author			Benjamin Gauronskas
		*/
		public void run()
		{
			//PosMessage posMessage;
			Date lastStop = new Date();
			Date currentDate;// = new Date();
			long timeDifference = 0;
			double displacement = 0;
			while(true){


				//If this is stopped temporarily because we are turning in a
				//stationary position, just keep getting the new date in a busy
				//loop.
				while(stopped){
					lastStop = new Date();
				}


				//Now we can make a calculation that takes the amount of time
				//given by time difference, and the speed that the device is
				//travelling to update it.

				//Equation... displacement over time multiplied by time
				//gives us our displacement.
				currentDate = new Date();
				timeDifference = (currentDate.getTime() - lastStop.getTime());
				displacement = RADIANS_PER_MILLISECOND*timeDifference;
				//This is if the robot is going forward.
				if(turnDirection==LEFT){
					coordLock.lock();
					angle = lastAngle - displacement;
					if (angle < 0){
						angle = (2* Math.PI)- angle;
					}
					//x = lastX + (int)Math.floor(displacement*
					//		Math.cos(angle - (Math.PI)/4));
					//y = lastY + (int)Math.floor(displacement*
					//		Math.sin(angle - (Math.PI)/4));
					coordLock.unlock();
				}
				//Going backwards
				else if(turnDirection == RIGHT){
					coordLock.lock();
					angle = (lastAngle + displacement)%(2*Math.PI);
					//x = lastX - (int)Math.floor(displacement*
					//		Math.cos(angle - (Math.PI)/4));
					//y = lastY - (int)Math.floor(displacement*
					//		Math.sin(angle - (Math.PI)/4));
					coordLock.unlock();
				}



			}
		}
	}



	/**
	XY Update will update the xy coordinates internally to the robot.

	@author			Benjamin Gauronskas
	@version			0.1
	*/
	private static class XYUpdate implements Runnable
	{

		public Thread t;

		public boolean stopped;


		/**
		Constructor takes no arguments.





		@author			Benjamin Gauronskas
		*/
		public XYUpdate()
		{
			stopped = false;
			System.out.println("XYUpdate STARTED RIGHT?!");
			t = new Thread(this,"XY Update");
			t.start();
		}

		/**
		This is the infinite loop that waits the appropriate amount of time and
		then sends an update to the server.

		@author			Benjamin Gauronskas
		*/
		public void run()
		{
			//PosMessage posMessage;
			Date lastStop = new Date();
			Date currentDate;// = new Date();
			long timeDifference = 0;
			double displacement = 0;
			while(true){


				//If this is stopped temporarily because we are turning in a
				//stationary position, just keep getting the new date in a busy
				//loop.
				while(stopped){
					lastStop = new Date();
				}


				//Now we can make a calculation that takes the amount of time
				//given by time difference, and the speed that the device is
				//travelling to update it.

				//Equation... displacement over time multiplied by time
				//gives us our displacement.
				currentDate = new Date();
				timeDifference = (currentDate.getTime() - lastStop.getTime());
				displacement = INCHES_PER_MILLISECOND*timeDifference;
				//This is if the robot is going forward.
				if(forward){
					coordLock.lock();
					x = lastX + (int)Math.floor(displacement*
							Math.cos(angle - (Math.PI)/4));
					y = lastY + (int)Math.floor(displacement*
							Math.sin(angle - (Math.PI)/4));
					coordLock.unlock();
				}
				//Going backwards
				else{
					coordLock.lock();
					x = lastX - (int)Math.floor(displacement*
							Math.cos(angle - (Math.PI)/4));
					y = lastY - (int)Math.floor(displacement*
							Math.sin(angle - (Math.PI)/4));
					coordLock.unlock();
				}

				boundaryCheck();

			}
		}
	}

	/**
	This thread will sleep for the amount of time indicated by
	SERVER_XY_UPDATE_TIME and then will send the current coordinate information
	to the server.

	@author			Benjamin Gauronskas
	@version			0.2
	*/
	private static class ServerUpdate implements Runnable
	{

		public Thread t;




		/**
		Constructor takes no arguments.



		@author			Benjamin Gauronskas
		*/
		public ServerUpdate()
		{


			t = new Thread(this,"map update");
			t.start();
		}

		/**
		This is the infinite loop that waits the appropriate amount of time and
		then sends an update to the server.



		@author			Benjamin Gauronskas
		*/
		public void run()
		{
			PosMessage posMessage;

			while(true){
				try{
					Thread.sleep(SERVER_XY_UPDATE_TIME);
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
				coordLock.lock();
				posMessage = new PosMessage(speed, x, y, angle);
				coordLock.unlock();
				Registers.connection.sendMessage(posMessage);
			}

		}
	}

	/**
	This thread receives information from the infrared sensors, and puts them into the map.

	@author			Benjamin Gauronskas
	@version			0.1
	*/
	private static class MapIR implements Runnable
	{

		public Thread t;




		/**
		Constructor takes no arguments.



		@author			Benjamin Gauronskas
		*/
		public MapIR()
		{


			t = new Thread(this,"IR mapping");
			t.start();
		}

		/**
		This loop will ask the IR for information, get the current coordinates,
		and then map them.

		@author			Benjamin Gauronskas
		*/
		public void run()
		{
			int infraredLength;
			int currentX;
			int currentY;
			double currentAngle;

			while(!Registers.connectionMade);
			while(true){
				infraredLength = Registers.arduino.getIRSensor();

				//interrupt all movement in case we have accidentally come too
				//close to a wall in our travels.
				if(infraredLength < DISTANCE_THRESHOLD){
					avoidance(infraredLength);
				}

				coordLock.lock();
				currentX = x;
				currentY = y;
				currentAngle = angle;
				coordLock.unlock();

				//Knowing the current x, y, and angle, and having the distance
				//given by the IR, we can now compute where to draw this point.

				//Sin = opp/hyp
				//cos = adj/hyp
				//tan = opp/adj
				//
				plotPoint(	currentX,
							currentY,
							infraredLength,
							currentAngle,
							Color.red);
			}

		}
	}
//setRGB(int x, int y, int rgb)

}
