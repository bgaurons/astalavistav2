import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
//import java.util.*;
//import java.net.*;
//import java.io.*;


/**
<p>BotPanel.java - A GUI element giving a representation of where the robot
	is currently situated. It also handles input.</p>
	<h1>Revision History:</h1>
	<ul>
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
public class BotPanel extends JPanel
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
	angle of rotation in radians
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
		sPane = newStats;
		speed = 50;
		botRad = 40;
		camRad = 10;

		botX = 400;
		botY = 400;

		angle = 2*Math.PI;

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

		mouthTimer = new javax.swing.Timer(16, new mouthMover());
		mouthTimer.start();

		this.setFocusable(true);
		this.setPreferredSize(new Dimension(800,400));
		this.addKeyListener(new arrowListener());


		speed = 50;
		pacSpeedControl = speed/10;
		turnspeed = 15;
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

		camX = -camRad/2 + botX + (int)Math.floor((botRad-20)*Math.cos(angle - (Math.PI)/4));
		camY = -camRad/2 + botY + (int)Math.floor((botRad-20)*Math.sin(angle - (Math.PI)/4));

		page.setColor(color);
		page.fillPolygon(poly);
		page.setColor(Color.black);
		page.fillOval(camX,camY,camRad,camRad);
	}

	/**
	mouthMover
	This is a listener for actions. When an action occurs, calculations are
	made to repaint the frame.

	@author	???
	@version 0.1
	*/
	public class mouthMover implements ActionListener
	{
		/**
		Responds to any action by calculating the location of drawings, and
		then making them again.
		@param		mouthEvent	The event that occured.
		@author		???
    	*/
		public void actionPerformed(ActionEvent mouthEvent)
		{
			if (right == true)
			{
				angle = (angle + (Math.PI/30)) % (2*Math.PI);
			}
			else if (left == true)
			{
				angle = (angle - (Math.PI/30)) % (2*Math.PI);
			}
			else if (up == true)
			{
				botX = botX + (int)Math.floor(pacSpeedControl*Math.cos(angle - (Math.PI)/4));
				botY = botY + (int)Math.floor(pacSpeedControl*Math.sin(angle - (Math.PI)/4));
				if (botY+botRad < 0)
					botY = 840;
				if (botX-botRad > 800)
					botX = -40;
				if (botY-botRad > 800)
					botY = -40;
				if (botX+botRad < 0)
					botX = 840;
			}

			else if (down == true)
			{
				botX = botX - (int)Math.floor(pacSpeedControl*Math.cos(angle - (Math.PI)/4));
				botY = botY - (int)Math.floor(pacSpeedControl*Math.sin(angle - (Math.PI)/4));
				if (botY+botRad < 0)
					botY = 840;
				if (botX-botRad > 800)
					botX = -40;
				if (botY-botRad > 800)
					botY = -40;
				if (botX+botRad < 0)
					botX = 840;
			}

			repaint();
		}
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
			MotMessage msg;
			try
			{
				switch(e.getKeyCode())
				{
					case KeyEvent.VK_RIGHT :
					{
						//out.writeBytes("" + turnspeed + " " + 4 +"\n");
						msg = new MotMessage(	(byte)turnspeed,
												MotMessage.CTRL_TRN);
						Registers.connection.sendMessage((Message)msg);


						right = true;
						up = false;
						down = false;
						left = false;
						break;
					}
					case KeyEvent.VK_LEFT :
					{
						msg = new MotMessage(	(byte)(turnspeed*-1),
												MotMessage.CTRL_TRN);
						Registers.connection.sendMessage((Message)msg);
						//out.writeBytes("" + turnspeed * -1 + " " + 4 +"\n");
						right = false;
						up = false;
						down = false;
						left = true;
						break;
					}
					case KeyEvent.VK_UP :
					{
						if (speed < 0)
							speed = speed * -1;

						msg = new MotMessage(	(byte)speed,
												MotMessage.CTRL_FWD);
						Registers.connection.sendMessage((Message)msg);
						//out.writeBytes("" + speed + " " + 3 +"\n");
						right = false;
						up = true;
						down = false;
						left = false;
						break;
					}
					case KeyEvent.VK_DOWN :
					{
						if (speed > 0)
							speed = speed * -1;

						msg = new MotMessage(	(byte)speed,
												MotMessage.CTRL_FWD);
						Registers.connection.sendMessage((Message)msg);
						//out.writeBytes("" + speed + " " + 3 +"\n");
						right = false;
						up = false;
						down = true;
						left = false;
						break;
					}
					case KeyEvent.VK_Z :
					{
						setSpeed(-20);
						msg = new MotMessage(	(byte)speed,
												MotMessage.CTRL_FWD);
						Registers.connection.sendMessage((Message)msg);
						//out.writeBytes("" + speed + " " + 3 +"\n");
						break;
					}
					case KeyEvent.VK_A :
					{
						setSpeed(20);
						msg = new MotMessage(	(byte)speed,
												MotMessage.CTRL_FWD);
						Registers.connection.sendMessage((Message)msg);


						//out.writeBytes("" + speed + " " + 3 +"\n");
						break;
					}
					// stop
					case KeyEvent.VK_SPACE :
					{
						up = false;
						down = false;
						msg = new MotMessage(	(byte)0x0,
												MotMessage.CTRL_FWD);
						Registers.connection.sendMessage((Message)msg);
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
		}
		//***** End keyPressed method *****//

		/**
		Responds to key releases.
		@param		e	The event that occured.
		@author		???
    	*/
		public void keyReleased(KeyEvent e)
		{
			MotMessage msg;

			try
			{
				switch(e.getKeyCode())
				{
					case KeyEvent.VK_LEFT :
					{
						left = false;
						msg = new MotMessage(	(byte)0x00,
												MotMessage.CTRL_TRN);
						Registers.connection.sendMessage((Message)msg);
						//out.writeBytes("" + 0 + " " + 4 +"\n");
						break;
					}
					case KeyEvent.VK_RIGHT :
					{
						right = false;
						msg = new MotMessage(	(byte)0x00,
												MotMessage.CTRL_TRN);
						Registers.connection.sendMessage((Message)msg);
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

