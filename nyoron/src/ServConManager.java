
//The following are for the config file functionality.
import java.util.Properties;
import java.io.FileInputStream;

//These are for networking functions.
import java.net.*;

//Neccesarry for multithreading
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import java.io.IOException;


/**
 * <p>ServConManager.java - This Creates a connection with a client.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>March 19, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Integrated logic.</li>
 *		</ul>
 *		<li>January 27, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *	<p>Sources for ideas come from tutorials and help at:
 *	<ul>
 *		<li>http://www.ashishmyles.com/tcpchat/index.html</li>
 *		<li>
 *		http://www.csc.villanova.edu/~mdamian/Sockets/JavaSocketsNoThread.htm
 *		</li>
 *		<li>
 *	http://java.sun.com/developer/technicalArticles/Programming/serialization/
 *		</li>
 *	</ul></p>
 *
 * @author			Benjamin Gauronskas
 * @version			0.2
 */
public class ServConManager
{

	//I need both to make this object usable in multiple locations
	/**
	The object that can be used to poll for incoming connections.
	*/
	private ServerSocket server;// = srvr.accept();

	//THIS SHOULD PROBABLY BE CHANGED AND MADE BETTER LATER
	//IDEAS INCLUDE VECTOR, ARRAY LIST, ETC...
	/**
	The connection that will be made by the server socket.
	*/
	public ConManager connection;

	/**
	A lock for concurrency when multiple connections are allowed.
	*/
	private ReentrantLock connectionLock;// = new ReentrantLock();



    /**
	Constructor. It gets its information from a hard properties file, so it
	takes no parameters.
	@author		Benjamin Gauronskas
    */
	public ServConManager()
	{

	    Properties config = new Properties();
	    try {
			config.load(new FileInputStream("server.config"));
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tFailed to read configuration file for " +
								"the connection");
			e.printStackTrace();
		}

		try{
			server = new ServerSocket(
							Integer.parseInt(config.getProperty("Port").trim())
									);
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tServer failed!");
			e.printStackTrace();
		}



		//Now we are not blocked by the blocking wait.
		//new GetClient();

		//Improve this later... We are ostensibly hoping to make multiple
		//connections possible to the server, and making the acceptance stage
		//multi-threaded.
		connection = null;
		try{
			//client_socket = server.accept();
			connection = new ConManager(server.accept());
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tCan't connect to foreign body!");
			e.printStackTrace();
		}


		//We do not need this yet, but we will later.
		connectionLock = new ReentrantLock();

	}



}