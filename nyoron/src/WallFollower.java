// WallFollower.java
// Wallfollowing AI
import java.lang.InterruptedException;
import java.util.concurrent.locks.*;
import java.awt.image.BufferedImage;
import java.awt.Point;


public class WallFollower {

	private static float sonar_L 				= 0;		
	private static float sonar_M 				= 0;
	private static float sonar_R 				= 0;
	private static float compass_degree 		= 0;
	
	private static Point point; 							// returned from Movement.getRobotPoint()
	
	public static int x							= 0;		// X,Y coordinates of the robot
	public static int y							= 0;
	
	private static final float lowerbound		= 0;		// Lowest acceptable distance from wall
	private static final float upperbound		= 10;		// Highest acceptable distance from wall
	
	private static final float sonarDistance	= 50;		// Sonar read distance

	public static final boolean LEFT = false;				// Left turn
	public static final boolean RIGHT = true;				// Right turn
	
	public static final int RIGHT_SONAR 		= 0;		// Used for follow
	public static final int LEFT_SONAR 		= 1;
	public static final int MIDDLE_SONAR 		= 2;
	public static final int NO_SONAR 			= 3;

	private static int following_sonar 		= RIGHT_SONAR;	// Sonar that the robot is following

	public static WallFollowThread wft;					// Logic handling thread


	/**
	Whether the movement is computer controlled or controlled by the robot.
	*/
	protected static boolean isAutomatic = true;

	/**
	INITIALIZE!
	@author Benjamin Gauronskas
	*/
	public static void initialize() {
		
		wft = new WallFollowThread();
		
	}
	
	// Main logic for the wall following algorithm.
	private static class WallFollowThread implements Runnable  {

		Thread t;
		boolean stopped;

		//Constructor takes no arguments.
		public WallFollowThread() {
			
			stopped = false;
			System.out.println("WallFollowThread STARTED RIGHT?!");
			t = new Thread(this, "Wall Follow Thread");
			t.start();
			
		}

		//Runs until stopped equals true.
		public void run() {
			
			System.out.print("w");
			
			while(!stopped) {

				while(!isAutomatic);

				compass_degree 	= Registers.arduino.getCompass();			// deviation from due north, theta
				
				sonar_L 		= Registers.arduino.getLeftSonar();
				sonar_M 		= Registers.arduino.getFrontSonar();
				sonar_R 		= Registers.arduino.getRightSonar();
				
				point = Movement.getRobotPoint();
				
				x = point.x;
				y = point.y;
				
				determineTurn();
				
				point = Movement.getRobotPoint();
				
				x = point.x;
				y = point.y;
				
				free(x, y, compass_degree, sonarDistance);
				
					
				Movement.forward();					// move forward until obstacle
					
				if(following_sonar == NO_SONAR) {
	
					// if robot is close enough to a wall, stop motors, update coordinates, and resume the do loop
					if(sonar_L >= lowerbound || sonar_M >= lowerbound || sonar_R >= lowerbound) {
							
						Movement.stopMotor();
						
						following_sonar = RIGHT_SONAR;
					}
	
					point = Movement.getRobotPoint();
						
					x = point.x;
					y = point.y;
					
					compass_degree = Registers.arduino.getCompass();	// take compass reading, calculate angle of turn derivation
					
				} 

			}
		}
		
		//determines the next turn to make if cornered.
		//designed to take care of immediate cases
		private static void determineTurn() {

			//Robot is blocked in front, right, and left
			if ( (sonar_L != 0 && sonar_L < upperbound) && (sonar_R != 0 && sonar_R < upperbound) && (sonar_M != 0 && sonar_M < upperbound) ) {
	
				//Turn completely around
				Movement.turn(Math.PI);
	
			//Robot is blocked in front and to the left
			} else if( (sonar_L != 0 && sonar_L < upperbound) && (sonar_M != 0 && sonar_M < upperbound) ) {
	
				//Turn right 90 degrees
				Movement.turn(Math.PI/2);
				
			//Robot is blocked in front and to the right
			} else if( (sonar_R != 0 && sonar_R < upperbound) && (sonar_M != 0 && sonar_M < upperbound) ) {
				
				//Turn left 90 degrees
				Movement.turn(-1*Math.PI/2);
				
			}
		}





	
		// Returns a portion of the buffered image corresponding to the front of
		// the robot so that we are able to decide what direction to travel
		// x_f and y_f are the x, y coordinates of the robot
		// theta is the compass_degree
		// d is direction relative snapshot distance
		// the integer returned indicates the direction the robot should turn
		public void free(float x_f, float y_f, float theta, float d) {
			
			// assume XY represents the dead center of the robot
			// relative directional displacements
			float x_left;
			float y_left;
			float x_mid;
			float y_mid;
			float x_right;
			float y_right;
			
			float x_Displacement	= d * (float)Math.sin(theta);		// d sin theta
			float y_Displacement	= d * (float)Math.cos(theta);		// d cos theta
			
			int[] left_rgbArray		= new int[(int)d*(int)d];
			int[] mid_rgbArray		= new int[(int)d*(int)d];
			int[] right_rgbArray		= new int[(int)d*(int)d];
			
			int leftcount			= 0;
			int midcount			= 0;
			int rightcount			= 0;
			
			// different quadrant cases
			if(theta < 0 && theta >= Math.PI/2) {						// theta less than 90
				
				x_left		= x_f;
				x_mid 		= x_f + x_Displacement;
				x_right 	= x_f + 2*x_Displacement;
				
				y_left 		= y_f - 3*y_Displacement;
				y_mid 		= y_f - 2*y_Displacement;
				y_right 	= y_f + y_Displacement;
				
			} else if(theta < Math.PI/2 && theta >= Math.PI) {	 	// theta less than 180
				
				x_left		= x_f;
				x_mid 		= x_f + x_Displacement;
				x_right 	= x_f + 2*x_Displacement;
				
				y_left 		= y_f + 2*y_Displacement;
				y_mid 		= y_f + y_Displacement;
				y_right 	= y_f;
				
			} else if(theta < Math.PI && theta >= (3*Math.PI)/2) { 	// theta less than 270
				
				x_left		= x_f - 3*x_Displacement;
				x_mid 		= x_f - 2*x_Displacement;
				x_right 	= x_f - x_Displacement;
				
				y_left 		= y_f + 2*y_Displacement;
				y_mid 		= y_f + y_Displacement;
				y_right 	= y_f;
				
			} else { 													// theta less than 360
				
				x_left		= x_f - 3*x_Displacement;
				x_mid 		= x_f - 2*x_Displacement;
				x_right 	= x_f - x_Displacement;
				
				y_left 		= y_f - 3*y_Displacement;
				y_mid 		= y_f - 2*y_Displacement;
				y_right 	= y_f - y_Displacement;
				
			}
			
			// return look ahead portion of the buffered image
			// getSubimage(x,y,width,height) originally used, but not used anymore
			// get pixel arrays
			// int[]	getRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
		
			System.out.println("\nx_left:\t" + x_left + "\ny_left:\t" + y_left + "\nx_Displacement:\t" + x_Displacement + "\ny_Displacement:\t" + y_Displacement + "\n\n");

			left_rgbArray	= Registers.map.getWallMapSection((int)x_left,(int)y_left,(int)d,(int)d,left_rgbArray);
			mid_rgbArray	= Registers.map.getWallMapSection((int)x_mid,(int)y_mid,(int)d,(int)d,mid_rgbArray);
			right_rgbArray	= Registers.map.getWallMapSection((int)x_right,(int)y_right,(int)d,(int)d,right_rgbArray);
			
			//get array obstacle pixel count
			for(int i=0; i< left_rgbArray.length; i++) {
				
				if((left_rgbArray[i] & 0x00ff0000) < 16) {
					leftcount++;
				}
			}
			
			for(int i=0; i< mid_rgbArray.length; i++) {
				
				if((mid_rgbArray[i] & 0x00ff0000) < 16) {
					midcount++;
				}
			}
			
			for(int i=0; i< right_rgbArray.length; i++) {
				
				if((right_rgbArray[i] & 0x00ff0000) < 16) {
					rightcount++;
				}
			}
			
			// return directional obstacle minflag
			if(leftcount > midcount && leftcount > rightcount) {			// left
				
				Movement.turn(-1*Math.PI/2);
				
			} else if(midcount > leftcount && midcount > rightcount) {	// forward
				
				// do nothing
				
			} else {														// right
				
				Movement.turn(Math.PI/2);
		
			}
		}
	}

	/**
	Sets whether or not the robot is automatic or not.
	@param	auto	Whether it will be automatic or not.
	@author	Benjamin Gauronskas
	*/
	public static void setAuto(boolean auto) {
		
		isAutomatic = auto;
		
	}
}
