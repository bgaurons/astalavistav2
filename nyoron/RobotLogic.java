/**

RobotLogic.java
Program connects to a TCP welcome socket. Once request is received and
connection is created a thread is started to handle communication on the
connection.

	<h1>Revision History:</h1>
	<ul>
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

		I2CChannel com5 = new I2CChannel("COM5");
		Registers.motor = new Motor(com5);
		Thermopile thermopile = new Thermopile(com5);
		//TSpam tspam = new TSpam(com5);
		Thread mthread = new Thread(Registers.motor);
		mthread.start();


	}






}