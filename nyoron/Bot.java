import javax.swing.*;
import java.awt.*;

/**
<p>Bot.java - This is the server gui that allows one to see what the robot is
	doing. Right now, this is the main method, so to say, for the controlling
	console.</p>
	<h1>Revision History:</h1>
	<ul>

		<li>April 1, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Using register wide version of BotPanel now.</li>
		</ul>
		<li>March 19, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Refitted for updated network code.</li>
			<li>Removed extraneous imports.</li>
			<li>Converted to server from client in network relationship.</li>
		</ul>
		<li>???, ???</li>
		<ul>
			<li>Someone made this, right?</li>
		</ul>
	</ul>


  @author                      ???
  @version                     0.2
 */
public class Bot extends JFrame
{
	/**
	Window width.
	*/
	private static int width;
	/**
	Window height.
	*/
	private static int height;
	/**
	The frame for which all GUI components will be embedded.
	*/
	private static JFrame mainFrame;



        /**
	Main method. It basically initializes things and then communicates with,
	at this point, <b>one</b> robot.

	@param		args		commandline arguments are ignored at this point.
	@author		???
         */
	public static void main (String [] args) throws Exception
	{
		Registers.servCon = new ServConManager();
		Registers.connection = Registers.servCon.connection;
		Registers.tempPanel = new TempPanel();

		width = 850;
		height = 700;
		mainFrame = new JFrame("Capstone Robot Demo");
		StatsPanel stats = new StatsPanel();
		Registers.mainPac = new BotPanel(stats);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		mainPanel.add(stats);
		mainPanel.add(Registers.mainPac);
		mainPanel.add(Registers.tempPanel);

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(width,height);
		mainFrame.getContentPane().add(mainPanel);
		mainFrame.setVisible(true);

	}
}