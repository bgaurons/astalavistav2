import java.util.Date;

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
	The smallest amount that the robot will turn. This is 6 in degrees, by the
	way.
	*/
	public static final double TURN_UNIT = Math.PI/30;

	/**
	The regular period for sending new coordinates, angle, and speed of the
	robot.
	*/
	public static final int SERVER_XY_UPDATE_TIME = 2000;


	/**
	The robot's current moving speed. Hopefully in some intelligent unit one
	day. This is the speed that the motors think in, not the speed that the
	GUI uses. The GUI uses speed/BotPanel.SPEED_DIVISOR.
	*/
	protected static int speed = SPEED_DEFAULT;

	/**
	The robot's X coordinate in inches.
	*/
	protected static int x = X_DEFAULT;

	/**
	The robot's Y coordinate in inches.
	*/
	protected static int y = Y_DEFAULT;


	/**
	The robot's angle in Radians. Read comment in BotPanel for information
	*/
	protected static double angle = ANGLE_DEFAULT;

	/**
	The direction the robot is travelling
	*/
	protected static boolean forward = true;

	/**
	The robot's angle in Radians. Read comment in BotPanel for information
	*/
	private static ServerUpdate serverUpdate = new ServerUpdate();


	/**
	Updates the robot's internal mapping coordinates.
	@param		direction	The direction that the robot is moving in
	@author		Benjamin Gauronskas
	*/
	public static void calculateMan(byte direction)
	{
		Message posMessage;
		//Two cases are commented out, because the compiler complains that they
		//are duplicates. I am leaving them in, because I think it helps
		//with readability.
		switch(direction){
			case RIGHT:
				stopStraightDaemon();
				angle = (angle + (TURN_UNIT)) % (2*Math.PI);
				Registers.motor.turn(TURN_UNIT);
				break;

			case LEFT:
				stopStraightDaemon();
				angle = (angle - (TURN_UNIT)) % (2*Math.PI);
				Registers.motor.turn(-1*TURN_UNIT);
				break;
			//case UP:
			case FORWARD:

				forward = true;
				startStraightDaemon();
				Registers.motor.rightMotor(speed);
				//x = x + (int)Math.floor(
				//	speed/BotPanel.SPEED_DIVISOR*Math.cos(angle - (Math.PI)/4)
				//	);
				//y = y + (int)Math.floor(
				//	speed/BotPanel.SPEED_DIVISOR*Math.sin(angle - (Math.PI)/4)
				//	);
				//boundaryCheck();
				break; //Done with UP

			//case DOWN:
			case BACKWARD:
				forward = false;
				startStraightDaemon();
				Registers.motor.rightMotor(-1*speed);
				//x = x - (int)Math.floor(
				//	speed/BotPanel.SPEED_DIVISOR*Math.cos(angle - (Math.PI)/4)
				//	);
				//y = y - (int)Math.floor(
				//	speed/BotPanel.SPEED_DIVISOR*Math.sin(angle - (Math.PI)/4)
				//	);
				//boundaryCheck();
				break;
			case STOP:
				stopStraightDaemon();
				Registers.motor.rightMotor(0);
				Registers.motor.leftMotor(0);
			case NO_DIRECTION:
			default: break;
		}
		posMessage = new PosMessage(speed, x, y, angle);

		Registers.connection.sendMessage(posMessage);

	}


	/**
	Stops the thread that sends server updates
	@author		Benjamin Gauronskas
	*/
	private static void stopStraightDaemon(){
		serverUpdate.sending = false;
	}

	/**
	starts or restarts the thread that sends server updates
	@author		Benjamin Gauronskas
	*/
	private static void startStraightDaemon(){
		serverUpdate.sending = true;
	}

	/**
	Makes sure that the robot does not go out of the designated boundaries.
	@author		Benjamin Gauronskas
	*/
	private static void boundaryCheck(){
		if (y+BotPanel.ROBOT_RADIUS_PIXELS < 0)
			y = BotPanel.ROBOT_RADIUS_PIXELS;
		if (x-BotPanel.ROBOT_RADIUS_PIXELS >
				BotPanel.WINDOW_FEET_X*BotPanel.PPF)
			x = (BotPanel.WINDOW_FEET_X*BotPanel.PPF) -
				BotPanel.ROBOT_RADIUS_PIXELS;
		if (y-BotPanel.ROBOT_RADIUS_PIXELS >
				BotPanel.WINDOW_FEET_X*BotPanel.PPF)
			y = (BotPanel.WINDOW_FEET_Y*BotPanel.PPF) -
				BotPanel.ROBOT_RADIUS_PIXELS;
		if (x+BotPanel.ROBOT_RADIUS_PIXELS < 0)
			x = BotPanel.ROBOT_RADIUS_PIXELS;
	}


	/**
	This thread will sleep for the amount of time indicated by
	SERVER_XY_UPDATE_TIME and then will send the current coordinate information
	to the server. This process should be usurpable in case of an unforseen
	early stop.

	@author			Benjamin Gauronskas
	@version			0.1
	*/
	private static class ServerUpdate implements Runnable
	{

		public Thread t;
		public boolean sending;



		/**
		Constructor takes no arguments.



		@author			Benjamin Gauronskas
		*/
		public ServerUpdate()
		{

			sending = true;
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
			Date lastUpdate = new Date();
			Date testDate;// = new Date();
			long timeDifference = 0;
			double displacement = 0;
			while(true){
				testDate = new Date();
				//If this is stopped temporarily because we are turning in a
				//stationary position, just keep getting the new date in a busy
				//loop.
				while(!sending){
					lastUpdate = new Date();
				}
				//Busy loop until appropriate time has passed or loop needs to
				//be stopped early for an unusual occurence.
				//System.out.println("Infinite loop?");
				while((timeDifference < SERVER_XY_UPDATE_TIME) && sending){
					testDate = new Date();
					timeDifference =
						(testDate.getTime() - lastUpdate.getTime());
				}
				System.out.println("Time difference: " + timeDifference);
				//System.out.println("Infinite loop?");

				//At this point we should be out of the loop for any valid
				//reason
				//If we are still sending, we need to set the new lastUpdate
				//time to the time that this update finished
				if(sending){
					lastUpdate = testDate;
				}
				//Now we can make a calculation that takes the amount of time
				//given by time difference, and the speed that the device is
				//travelling to update it.

				//Equation... displacement over time multiplied by time
				//gives us our displacement.
				displacement = Motor.INCHES_PER_MILLISECOND*timeDifference;
				//This is if the robot is going forward.
				if(forward){
					x = x + (int)Math.floor(displacement*
							Math.cos(angle - (Math.PI)/4));
					y = y + (int)Math.floor(displacement*
							Math.sin(angle - (Math.PI)/4));
				}else{
					x = x - (int)Math.floor(displacement*
							Math.cos(angle - (Math.PI)/4));
					y = y - (int)Math.floor(displacement*
							Math.sin(angle - (Math.PI)/4));
				}

				boundaryCheck();

				posMessage = new PosMessage(speed, x, y, angle);
				Registers.connection.sendMessage(posMessage);
			}
			//Now that a single iteration is complete, we will go through
			//the logic again...
		}
	}


}
