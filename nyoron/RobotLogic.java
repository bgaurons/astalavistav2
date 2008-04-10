/**

RobotLogic.java
Program connects to a TCP welcome socket. Once request is received and
connection is created a thread is started to handle communication on the
connection.

	<h1>Revision History:</h1>
	<ul>
		<li>March 23, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Added the AI module to the startup logic.</li>
		</ul>
		<li>March 21, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Reordered initialization to avoid null pointers.</li>
		</ul>
		<li>March 19, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Simplified the logic and changed it to client side logic.</li>
		</ul>
		<li>???, ???</li>
		<ul>
			<li>Someone made this, right?</li>
		</ul>
	</ul>


  @author                      ???
  @version                     0.2
*/
public class RobotLogic
{

	/**
	Initializes and connects to a server. Will probably crash at this point if
	there are no servers to connect to.

	@param		args		commandline arguments are ignored at this point.
	@author		???
    */
	public static void main(String[] args) throws Exception
	{
		// Open New welcome socket

		Registers.connection = new ConManager();

		Registers.ai = new ManualAI();
		I2CChannel com5 = new I2CChannel("COM5");
		Registers.motor = new Motor(com5);

		Thermopile thermopile = new Thermopile(com5);

		Registers.arduino = new Arduino("COM2");



		MovementLogic.initialize("example.gif");


		Thread mthread = new Thread(Registers.motor);
		mthread.start();


	}






}
