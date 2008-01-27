
//The following are for the config file functionality.
import java.util.Properties;
import java.io.FileInputStream;

//These are for networking functions.
import java.lang.*;
import java.io.*;
import java.net.*;

//Neccesarry for multithreading
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;


import java.io.IOException;
//import java.sql.SQLException;
//import java.lang.ClassNotFoundException;

/** 
 * <p>Rclient.java - This class connects to the Rserver and then can send
 *		messages to it.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
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
 * @version			0.1 
 */
public class Rclient
{
	static private Socket client;// = srvr.accept();

	static private final ReentrantLock connection_lock = new ReentrantLock();

	static private AtomicBoolean connected;
	//System.out.print("Server has connected!\n");
	//DELETE LATER DSAGDHIDFJVANJVNAJKFVDCCV
	static private ObjectOutputStream ObjOut;
	static private ObjectInputStream ObjIn;
	//END OF DSIOGAIUJFBVDAJKNFVLVAN


	/**
	 * Starts up the server listening capabilities and prepares to listen for
	 * an incoming connection.
	 *
	 * @author			Benjamin Gauronskas
	 */
	static public void initialize(){

	    Properties config = new Properties();
	    try {
			config.load(new FileInputStream("client.config"));
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tFailed to read configuration file for " +
								"the connection");
			e.printStackTrace();
		}

		try{
			client = new Socket(
							config.getProperty("Address").trim(),
							Integer.parseInt(config.getProperty("Port").trim())
									);
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tConnection failed!");
			e.printStackTrace();
		}
		System.out.println("!@#$\tConnected to server successfully.");

		//Get our streams set up.
		try{
			ObjOut = new ObjectOutputStream(client.getOutputStream());
			ObjIn = new ObjectInputStream(client.getInputStream());
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tFailure to open streams!");
			e.printStackTrace();
		}

		//Initializing locks.
		//connection_lock = new Lock();

		//Make this true when we connect, false otherwise. Using the set method
		//for concurrency.
		connected = new AtomicBoolean(true);
		


	}


	/**
	 * This sends a message object over the stream.
	 *
	 * @param		message	The message object to send.
	 *
	 * @author				Benjamin Gauronskas
	 */
	public static void sendMessage(Message message){
		connection_lock.lock();

		System.out.println("!@#$\tAcquired locks for sending message:\n" +
							message);
		//THIS IS FOR FUCKING TESTING ONLY, FUCKING DELETE LATER
		try{
			ObjOut.writeObject(message);
			ObjOut.flush();
			System.out.println("!@#$\tMessage seems to have sent correctly.");
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tCan't Write message!");
			e.printStackTrace();
		}
		//It can be disastrous if the lock is not unlocked because of a failure
		//in the try. finally{} forces it to happen.
		finally{
			connection_lock.unlock();
		}
		//END OF 2439FGUHFGAJNDFOPNADVKJGMFDA

	}


} 
