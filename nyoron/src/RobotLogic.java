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
		I2CChannel com5 = new I2CChannel("/dev/ttyUSB0");
		Registers.motor = new Motor(com5);

		Registers.thermopile = new Thermopile(com5);

		/** We're using 3 here because we've only got 3 sensors,
		and regardless of how scalable Ben would like to make
		it we simply can't afford more sensors so it won't
		happen anyway. **/
		for (int sonar_index = 0; sonar_index < 3; sonar_index++)
		{
		    Registers.sonars[sonar_index] = new Sonar(sonar_index);
		}

		System.out.println("Startup Arduino Code");
		Registers.arduino = new Arduino("/dev/ttyUSB1");



		Movement.initialize();


		Thread mthread = new Thread(Registers.motor);
		mthread.start();


	}






}
