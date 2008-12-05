import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
//import java.util.*;
//import java.net.*;
//import java.io.*;


/**
<p>BotPanel.java - A GUI element giving a representation of where the robot
	is currently situated. It also handles input.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>April 1, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Moved map logic to Robot.</li>
		</ul>
		<li>March 19, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Refitted for updated network code.</li>
			<li>Removed extraneous imports.</li>
			<li>Added comments.</li>
		</ul>
		<li>???, ???</li>
		<ul>
			<li>Someone made this, right?</li>
		</ul>
	</ul>


  @author                      ???
  @version                     0.2
 */
public class BotPanel extends JPanel implements Scrollable
{
	/**
	A label for the panel.
	*/
	private StatsPanel sPane;

	/**
	Controls the speed that the front of the robot is drawn.
	*/
	private javax.swing.Timer mouthTimer;

	/**
	Controls how often the robot is drawn.
	*/
	private int pacSpeedControl;

	/**
	Tells whether the robot is travelling in any of the given directions.
	*/
	private boolean up, down, left, right;


	/**
	The forward speed ofthe robot.
	*/
	private int speed;
	/**
	Rotation speed of the robot.
	*/
	private int turnspeed;

	/**
	The color of the robot.
	*/
	private Color color;
	/**
	X and Y coordinates of the robot.
	*/
	private int botY, botX;
	/**
	X and Y coordinates of the camera(?).
	*/
	private int camX, camY;
	/**
	angle of rotation in radians.
	FUCKING IMPORTANT NOTE:
	0 IS EAST
	PI/2 IS SOUTH
	PI IS WEST
	3PI/2 IS NORTH.
	2PI IS EAST AGAIN.
	*/
	private double angle;
	/**
	The amount the camera and the robot need to rotate.
	*/
	private int botRad, camRad;
	/**
	A shape representing... the robot, I think.
	*/
	private Polygon poly;

	/**
	X coordinates of all the points in poly.
	*/
	private int [] xs;
	/**
	Y coordinates of all the points in poly.
	*/
	private int [] ys;

	//THIS IS WHERE BEN ADDS SHIT

	/**
	The floor plan of the maze.
	*/
	private BufferedImage map;



	/**
	The amount of pixels per inch.
	*/
	public static final int PPI = 2;


	/**
	The amount of pixels per inch.
	*/
	private static final double ANGLE_OFFSET = (Math.PI/4);


	/**
	The amount of pixels per foot.
	*/
	public static final int PPF = PPI*12;

	/**
	The robots radius in inches
	*/
	public static final int ROBOT_RADIUS_INCHES = 5;

	/**
	The robots radius in inches
	*/
	public static final int SPEED_DIVISOR = 10;

	/**
	The robots radius in pixels
	*/
	public static final int ROBOT_RADIUS_PIXELS = ROBOT_RADIUS_INCHES * PPI;

	/**
	The "feet" across that the window screen will be.
	*/
	public static final int WINDOW_FEET_X = 65;

	/**
	The "feet" tall that the window screen will be.
	*/
	public static final int WINDOW_FEET_Y = 40;

	/**
	The pixels across that the window screen will be.
	*/
	private static final int WINDOW_PIXELS_X = WINDOW_FEET_X*PPF;

	/**
	The pixels tall that the window screen will be.
	*/
	private static final int WINDOW_PIXELS_Y = WINDOW_FEET_Y*PPF;

	/**
	The area of the scroll box: Y coordinate
	*/
	private static final int VIEWABLE_Y = 500;

	/**
	The area of the scroll box: X coordinate
	*/
	private static final int VIEWABLE_X = 800;

	/**
	The amount to scroll over at anyone time.
	*/
	private int maxUnitIncrement;

	/**
	Whether a key is being pressed or not.
	*/
	private boolean keyPressed;
	//THE END OF BEN'S SHIT.


	//public BotPanel (StatsPanel newStats, DataOutputStream dos, Socket s)
    /**
	Constructor. Needs a StatsPanel passed to it... not sure why.
	@param		newStats		previously constructed stats panel.
	@author		???
    */
	public BotPanel (StatsPanel newStats)
	{
		//out = dos;
		//sock = s;
		keyPressed = false;
		maxUnitIncrement = 1;
		sPane = newStats;
		speed = Movement.SPEED_DEFAULT;
		botRad = ROBOT_RADIUS_PIXELS;
		camRad = ROBOT_RADIUS_PIXELS/4;

		botX = 400;
		botY = 400;


		map = null;
		try {
			map = ImageIO.read(new File("example.gif"));
		} catch (IOException e) {
		}





		//GOD DAMN MOTHERFUCKING ANGLES ARE BACKWARDS.
		//WHO THE FUCK MAKES A CIRCLE GO POSITIVE IN THE
		//NEGATIVE X DIRECTION, GOD DAMN YOU.
		//GOD DAMN YOU TO FUCKING HELL.

		angle = 0+ANGLE_OFFSET;

		camX = -camRad/2 + botX + (int)Math.floor((botRad-20)*Math.cos(angle - (Math.PI)/4));
		camY = -camRad/2 + botY + (int)Math.floor((botRad-20)*Math.sin(angle - (Math.PI)/4));

		color = Color.white; // Color of robot

		xs = new int [4];
		ys = new int [4];

		xs[0] = botX + (int)Math.floor(botRad*Math.cos(angle));
		xs[1] = botX + (int)Math.floor(botRad*Math.cos(angle - (Math.PI)/2));
		xs[2] = botX + (int)Math.floor(botRad*Math.cos(angle - (2*Math.PI)/2));
		xs[3] = botX + (int)Math.floor(botRad*Math.cos(angle - (3*Math.PI)/2));
		ys[0] = botY + (int)Math.floor(botRad*Math.sin(angle));
		ys[1] = botY + (int)Math.floor(botRad*Math.sin(angle - (Math.PI)/2));
		ys[2] = botY + (int)Math.floor(botRad*Math.sin(angle - (2*Math.PI)/2));
		ys[3] = botY + (int)Math.floor(botRad*Math.sin(angle - (3*Math.PI)/2));



		poly = new Polygon(xs,ys,4);

		//mouthTimer = new javax.swing.Timer(16, new mouthMover());
		//mouthTimer.start();

		this.setFocusable(true);
		this.setPreferredSize(new Dimension(WINDOW_PIXELS_X,WINDOW_PIXELS_Y));
		//this.setPreferredSize(new Dimension(800,800));
		this.addKeyListener(new arrowListener());


		speed = 50;
		pacSpeedControl = speed/SPEED_DIVISOR;
		turnspeed = 15;

		Registers.connection.botPanelStarted = true;
	}

    /**
	Sets the map as the map that is sent from the robot.
	@param	map	The updated map graphic.
	@author		Benjamin Gauronskas
    */
    public void updateMap(BufferedImage map) {
		this.map = map;
    }


    /**
	No idea what this does... necessary for scrollable interface
	@return		false
	@author		Benjamin Gauronskas
    */
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
	No idea what this does... necessary for scrollable interface
	@return		false
	@author		Benjamin Gauronskas
    */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
	No idea what this does... necessary for scrollable interface
	@param		visibleRect		What we see right now
	@param		orientation		The flow direction of the component
	@param		direction		The direction we are moving in
	@return		Returns how far to scroll in which direction.
	@author		Benjamin Gauronskas
    */
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    /**
	No idea what this does... necessary for scrollable interface
	@param	visibleRect		Current viewable direction
	@param	orientation		What direction the panel flows
	@param	direction		The direction we are moving the panel.
	@return		tells what the new view should be
	@author		Benjamin Gauronskas
    */
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                             (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }
    }

    /**
	No idea what this does... necessary for scrollable interface
	@return		The size of the scrollable area.
	@author		Benjamin Gauronskas
    */
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(VIEWABLE_X, VIEWABLE_Y);
    }

    /**
	Redraws the screen. Ran every time repaint() is called.
	@param		page	The Screen to be redrawn.
	@author		???
    */
	public void paintComponent(Graphics page)
	{

		super.paintComponent(page);



		setBackground(Color.black);
		this.setBackground(Color.black);


		page.drawImage(map, 0, 0, null);


		pacSpeedControl = (int)Math.ceil(Math.abs(speed)/10);

		xs[0] = botX + (int)Math.floor(botRad*Math.cos(angle));
		xs[1] = botX + (int)Math.floor(botRad*Math.cos(angle - (Math.PI)/2));
		xs[2] = botX + (int)Math.floor(botRad*Math.cos(angle - (2*Math.PI)/2));
		xs[3] = botX + (int)Math.floor(botRad*Math.cos(angle - (3*Math.PI)/2));
		ys[0] = botY + (int)Math.floor(botRad*Math.sin(angle));
		ys[1] = botY + (int)Math.floor(botRad*Math.sin(angle - (Math.PI)/2));
		ys[2] = botY + (int)Math.floor(botRad*Math.sin(angle - (2*Math.PI)/2));
		ys[3] = botY + (int)Math.floor(botRad*Math.sin(angle - (3*Math.PI)/2));
		poly = new Polygon(xs,ys,4);

		edgeDetect(xs, ys);

		camX = -camRad/2 + botX + (int)Math.floor((botRad-5)*Math.cos(angle - (Math.PI)/4));
		camY = -camRad/2 + botY + (int)Math.floor((botRad-5)*Math.sin(angle - (Math.PI)/4));

		page.setColor(color);
		page.fillPolygon(poly);
		page.setColor(Color.black);
		page.fillOval(camX,camY,camRad,camRad);
	}

    /**
	Checks if there has been a collision with a wall, and reverses robot
	movement.
	@param		x	The x coordinates of the robot.
	@param		y	The y coordinates of the robot.
	@author		Benjamin Gauronskas
    */
	public void edgeDetect(int[] x, int[] y)
	{
		//To do edge detection use...
		//map.getRGB(x,y).equals(Color.red)
		//where x and y are the coordinates of the robot... if these are the
		//same then we need to reverse direction.

		if(x.length == y.length){
			for(int i =0; i < x.length; i++){
				//If we are at a wall...
				if(	x[i] < map.getWidth() && y[i] < map.getHeight() &&
					x[i] >= 0 && y[i] >= 0 &&
					map.getRGB(x[i], y[i]) == Color.red.getRGB() ){
					System.out.println("FOOBAR");
				}
			}
		}

	}

    /**
	Takes a Position message, and converts it to gui stuff.
	@param		msg		The message parsed from the TCP connection.
	@author		Benjamin Gauronskas
    */
	public void parsePosMessage(PosMessage msg)
	{
		//System.out.println("Received position message:\n!@#$" + msg);
		this.speed = msg.speed/SPEED_DIVISOR;
		this.botX = msg.x;
		this.botY = msg.y;
		this.angle = msg.angle;

		repaint();

	}


	/**
	arrowListener
	Listens for key presses, and will then send a message to a robot with
	updated information.

	@author	???
	@version 0.1
	*/
	public class arrowListener extends KeyAdapter
	{

		/**
		Responds to key presses.
		@param		e	The event that occured.
		@author		???
    	*/
		public void keyPressed (KeyEvent e)
		{
			Message msg;
			if(!keyPressed){
				keyPressed = true;
				try
				{
					switch(e.getKeyCode())
					{
						case KeyEvent.VK_D :
						{
							//out.writeBytes("" + turnspeed + " " + 4 +"\n");
							//msg = new MotMessage(	(byte)turnspeed,
							//						MotMessage.CTRL_TRN);
							//Registers.connection.sendMessage(msg);
							msg = new ManMessage(Movement.RIGHT);
							Registers.connection.sendMessage(msg);

							//right = true;
							//up = false;
							//down = false;
							//left = false;
							break;
						}
						case KeyEvent.VK_A :
						{
							//msg = new MotMessage(	(byte)(turnspeed*-1),
							//						MotMessage.CTRL_TRN);
							//Registers.connection.sendMessage(msg);
							msg = new ManMessage(Movement.LEFT);
							Registers.connection.sendMessage(msg);
							//out.writeBytes("" + turnspeed * -1 + " " + 4 +"\n");
							//right = false;
							//up = false;
							//down = false;
							//left = true;
							break;
						}
						case KeyEvent.VK_W :
						{
							//if (speed < 0)
							//	speed = speed * -1;

							//msg = new MotMessage(	(byte)speed,
							//						MotMessage.CTRL_FWD);
							//Registers.connection.sendMessage(msg);
							msg = new ManMessage(Movement.FORWARD);
							Registers.connection.sendMessage(msg);
							//out.writeBytes("" + speed + " " + 3 +"\n");
							//right = false;
							//up = true;
							//down = false;
							//left = false;
							break;
						}
						case KeyEvent.VK_S :
						{
							//if (speed > 0)
							//	speed = speed * -1;

							//msg = new MotMessage(	(byte)speed,
							//						MotMessage.CTRL_FWD);
							//Registers.connection.sendMessage(msg);
							msg = new ManMessage(Movement.BACKWARD);
							Registers.connection.sendMessage(msg);
							//out.writeBytes("" + speed + " " + 3 +"\n");
							//right = false;
							//up = false;
							//down = true;
							//left = false;
							break;
						}
						//case KeyEvent.VK_Z :
						//{
							/*
							setSpeed(-20);
							msg = new MotMessage(	(byte)speed,
													MotMessage.CTRL_FWD);
							Registers.connection.sendMessage(msg);
							//out.writeBytes("" + speed + " " + 3 +"\n");
							break;
							*/
						//}
						//case KeyEvent.VK_A :
						//{
							/*
							setSpeed(20);
							msg = new MotMessage(	(byte)speed,
													MotMessage.CTRL_FWD);
							Registers.connection.sendMessage(msg);


							//out.writeBytes("" + speed + " " + 3 +"\n");
							break;
							*/
						//}
						// stop
						case KeyEvent.VK_SPACE :
						{

							//up = false;
							//down = false;
							//msg = new MotMessage(	(byte)0x0,
							//						MotMessage.CTRL_FWD);
							//Registers.connection.sendMessage(msg);
							msg = new ManMessage(Movement.STOP);
							Registers.connection.sendMessage(msg);
							//out.writeBytes("" + 0 + " " + 3 +"\n");
							break;

						}
						default :
						{
							System.out.println("unknown key");
							break;
						}
					}
				} // end switch
				catch(Exception exe)
				{
					exe.printStackTrace();
					System.out.println(exe.toString());
				}
			}//End if !keyPressed
		}
		//***** End keyPressed method *****//

		/**
		Responds to key releases.
		@param		e	The event that occured.
		@author		???
    	*/
		public void keyReleased(KeyEvent e)
		{
			Message msg;

			if(keyPressed){
				keyPressed = false;
				try
				{
					switch(e.getKeyCode())
					{
						case KeyEvent.VK_A :
						case KeyEvent.VK_S :
						case KeyEvent.VK_W :
						{
							//left = false;
							//msg = new MotMessage(	(byte)0x00,
							//						MotMessage.CTRL_TRN);
							//Registers.connection.sendMessage((Message)msg);
							msg = new ManMessage(Movement.STOP);
							Registers.connection.sendMessage(msg);
							//out.writeBytes("" + 0 + " " + 4 +"\n");
							break;
						}
						case KeyEvent.VK_D :
						{
							//right = false;
							msg = new ManMessage(Movement.STOP);
							Registers.connection.sendMessage(msg);
							//msg = new MotMessage(	(byte)0x00,
							//						MotMessage.CTRL_TRN);
							//Registers.connection.sendMessage((Message)msg);
							//out.writeBytes("" + 0 + " " + 4 +"\n");
							break;
						}
						default :
						{
							// do nothing
						}
					}
				}
				catch(Exception exe)
				{
					exe.printStackTrace();
					System.out.println(exe.toString());
				}
			}//end if keyPressed
		}
		//***** End keyReleased method *****//
	}


	/**
	Increases the current speed by the offset given as a parameter. It does
	some bare-minimum error checking and sanitizing of input.
	@param		offset	The amount to change the speed by.
	@author		???
	*/
	public void setSpeed (int offset)
	{
		//increase speed
		if(offset > 0)
		{
			//if moving forward
			if (speed < 107 && speed >= 0 )
				speed = speed + offset;
			else if (speed < 0)
				speed = speed - offset;
		}
		//Decrease speed
		else
		{
			//if moving forward0
			if (speed > 0 )
			{
				speed = speed + offset;
				if (speed < 5)
					speed = 5;
			}
			else if (speed > -107 && speed <= 0)
			{
				speed = speed - offset;
				if (speed > -5)
					speed = -5;
			}
		}
	}
}

