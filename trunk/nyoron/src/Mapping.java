import java.util.Date;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.lang.InterruptedException;
import java.util.concurrent.locks.*;
import java.awt.Color;
import java.awt.Point;
/**

Mapping.java
This is code borne from the first semester MovementLogic class. It has been
decided that it should be split into parts to better track the myriad
functionalities it encompassed. This is meant to be run on the robot.

	<h1>Revision History:</h1>
	<ul>
		<li>November 07, 2008, Benjamin Gauronskas and Joyce Tang</li>
		<ul>
			<li>Looked over and added neccessary method headers.</li>
		</ul>
		<li>November 06, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Created Mapping.</li>
		</ul>
		<li>March 27, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Created MovementLogic.</li>
		</ul>

	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.1
*/
public class Mapping
{


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
	The thread that maps walls found from the sonar sensors.
	*/
	//protected static MapSonar ms;




	/**
	Initializes things for movement logic.
	@author		Benjamin Gauronskas
	*/
	public static void initialize()
	{
		SonarMapper[] sonarMappers = new SonarMapper[Registers.sonars.length];
		//Busy loop until sonar
		while(Registers.sonars == null);
		for(int i = 0; i < Registers.sonars.length; i++){
			sonarMappers[i] = new SonarMapper(Registers.sonars[i]);
		}
		//ms = new MapSonar();
	}

    /**
		    Plots a path traversed by the robot.
		    @param	start			The start of the line segment
		    @param	finish			The end of the line segment.


		    @author		Benjamin Gauronskas
    */
	public static void mapPath(Point start, Point finish){
	
		Registers.map.plotSegment(start, finish, Map.TRAV_MAP);
		Message msg = new SegmentMessage(start, finish, Map.TRAV_MAP);
	}

    /**
		    Returns the area in front of robot.

		@return		The [][] array of the area in front of the robot. 
		    @author		Benjamin Gauronskas
    */
	public static int[][] getFrontArea(){
		return null;}


    /**
	Plots a point
	@param	xR			X coordinate of the robot
	@param	yR			Y coordinate of the robot
	@param	distanceP	Distance to the plot point.
	@param	angleR		Distance to the plot point.
	@return					the cartesian point that should correspond to the
								reading. Null if reading out of bounds.

	@author		Benjamin Gauronskas
    */
	public static Point getWallPoint(	int xR,
											int yR,
											int distanceP,
											double angleR){

		Point returnValue = null;
		if(distanceP != -1){ //If the sonar was not out of range.
			int mapLength = Map.PPI * distanceP;
	
			int plotX = (int)(mapLength * Math.cos(angleR));
			int plotY = (int)(mapLength * Math.sin(angleR))*-1;
	
			plotX+=xR;
			plotY+=yR;
			returnValue = new Point(plotX, plotY);
		}

		return returnValue;

	}


    /**
	Plots a wall
	@param	start			Start of the line segment
	@param	end			end of the Line segment

	@author		Benjamin Gauronskas
    */
	public static void plotWall(Point start, Point end){
		Registers.map.plotSegment(start, end, Map.WALL_MAP);

		//Send this wall to the server...

		Message msg = new SegmentMessage(start, end, Map.WALL_MAP);
		Registers.connection.sendMessage(msg);
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
											Color color){

		//System.out.println("plotPoint\t" + xR + " " + yR + " " + distanceP +
		//" " + angleR + " " + color);

		//int red = Color.red.getRGB();
		//int blue = Color.blue.getRGB();

		//Map length is converting the measurement made in inches to a measurement
		//"made in pixels."
		Message msg;
		int mapLength = Map.PPI * distanceP;

		int plotX = (int)(mapLength * Math.cos(angleR));
		int plotY = (int)(mapLength * Math.sin(angleR))*-1;

		plotX+=xR;
		plotY+=yR;

		//Or it now.
		//color |= Registers.map.getRGB(plotX, plotY);

		//Get rid of path variable.
		//color &= ~Map.PATH_COLOR;



		//if(map.getRGB(plotX, plotY) != blue)
		//Or the color with the old color in case it is hot
		//Registers.map.setRGB(	plotX,
		//								plotY,
		//								color);
		//Send this coordinate to the server...

		//msg = new PointMessage(new Point(plotX, PlotY), color);
		//Registers.connection.sendMessage(msg);
	}











	/**
	This thread plots the ambient heat every so often as a filled in circle
	on the map.

	@author			Benjamin Gauronskas
	@version			0.1
	*/
	private static class HeatPlotter implements Runnable
	{

		/**
		Arbitrarily chosen time to pause (in milliseconds) between each plot.
		*/
		public static final int PAUSE = 5000;

		/**
		The thread that maps the readings from the sonar sensors.
		*/
		public Thread t;

		/**
		The last read ambient temperature.
		*/
		public byte temperature;

		/**
		Whether the "infinite loop" should be stopped or not.
		*/
		public boolean stopped;



		/**
		No arguments for the constructor.

		@author			Benjamin Gauronskas
		*/
		public HeatPlotter()
		{

			stopped = false;

			t = new Thread(this,"Heat Plotter");
			t.start();

		}

		/**
		This loop will pause for some time, and then plot a heat point.

		@author			Benjamin Gauronskas
		*/
		public void run()
		{
			System.out.println("Heat Plotter begun.");

			
			
			while(!Registers.connectionMade);


			while(!stopped){
				try{
					Thread.sleep(PAUSE);
				}
				catch(InterruptedException ex){
					ex.printStackTrace();
				}

				Registers.map.plotHeat(	Registers.thermopile.readAmbientTemp(),
												Movement.getRobotPoint());

			}

		}
	}




	/**
	This thread keeps track of readings from one sonar sensor and maps the data
	as appropriate.

	@author			Benjamin Gauronskas
	@version			0.1
	*/
	private static class SonarMapper implements Runnable
	{
		/**
		The thread that maps the readings from the sonar sensors.
		*/
		public Thread t;

		/**
		Whether the "infinite loop" should be stopped or not.
		*/
		public boolean stopped;

		/**
		The last mapped point.
		*/
		public Point lastPoint;

		/**
		The newest mapped point.
		*/
		public Point newPoint;

		/**
		The sonar sensor to keep track of.
		*/
		public Sonar sonar;

		/**
		The amount of sonar mapping threads created thus far.
		*/
		public static int threadCounter = 0;


		/**
		All that needs to be passed is the sonar sensor to follow.

		@param	sonar	The sensor to follow.

		@author			Benjamin Gauronskas
		*/
		public SonarMapper(Sonar sonar)
		{
			this.sonar = sonar;
			lastPoint = null;
			newPoint = null;
			stopped = false;

			t = new Thread(this,"Sonar mapping: " + threadCounter);
			t.start();
			threadCounter++;
		}

		/**
		This loop will ask the IR for information, get the current coordinates,
		and then map them.

		@author			Benjamin Gauronskas
		*/
		public void run()
		{
			System.out.println("Mapping begun");
			int sonarLength;
			//int[] sonarAngleOffsets = getAngleOffsets();
			//int[] sonarDistanceOffsets = getDistanceOffsets();
			double currentX;
			double currentY;
			float compassReading;
			double currentAngle;
			Point robotPoint;
			
			
			while(!Registers.connectionMade);


			while(!stopped){

				sonarLength = (int)sonar.getMeasurement();
				
				compassReading = Registers.arduino.getCompass();
				/**
				Note to Terrence, because of mutual exclusivity... this
				probably needs to be changed in such a way that, for example, one
				method returns a "Point" object. See the Java api for the point
				data struct.
				*/
				robotPoint = Movement.getRobotPoint();


				//At this point, all time sensitive operations of the loop are
				//complete.
				currentX = robotPoint.getX();
				currentY = robotPoint.getY();
				currentAngle = Math.toRadians((double)compassReading);

				sonarLength += sonar.dist_offset;
				currentAngle += sonar.angle_offset;
				newPoint = getWallPoint(
								(int)currentX,
								(int)currentY,
								sonarLength,
								currentAngle);

				//If we got a valid reading from the sonar sensor two times in a row
				if(newPoint != null && lastPoint != null){
					Registers.map.plotSegment(	lastPoint,
											newPoint,
											Map.WALL_MAP);

				}
				//Now replace the last point with the newest point.
				lastPoint = newPoint;


			}

		}
	}

//setRGB(int x, int y, int rgb)

}
