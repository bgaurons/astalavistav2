
/**
<p>Registers.java - These are the global variables for the progrm. It might be
a good idea to make  a seperate one for the server and the client at some
point.</p>
	<h1>Revision History:</h1>
	<ul>
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

}