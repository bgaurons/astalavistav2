
/**
<p>Registers.java - These are the global variables for the progrm. It might be
a good idea to make  a seperate one for the server and the client at some
point.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>April 1, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Added BotPanel since it now depends on messages.</li>
		</ul>
		<li>March 19, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Wrote the method and added what I needed.</li>
		</ul>
	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.1
 */
public class Registers
{
	/**
	The connection has been made
	*/
	public static boolean connectionMade = false;

	/**
	A one way connection to another computer.
	*/
	public static ConManager connection;
	/**
	An object that allows us to establish new connections to other computers.
	*/
	public static ServConManager servCon;
	/**
	The motor for the robot.
	*/
	public static Motor motor;
	/**
	The temperature GUI panel needs to be globally accesible to receive
	incoming messages
	*/
	public static TempPanel tempPanel;
	/**
	The current robot AI
	*/
	public static AI ai;
	/**
	The gui element that shows where the robot is.
	*/
	public static BotPanel mainPac;

	/**
	The infrared sensor
	*/
	public static Arduino arduino;

	/**
	The infrared sensor, we only have 3 sensors.
	*/
	public static Sonar sonars[] = new Sonar[3];

	/**
	The infrared sensor
	*/
	public static Thermopile thermopile;

	/**
	The object handling all maps
	*/
	public static Map map;

}
