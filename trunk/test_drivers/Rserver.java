
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


/** 
 * <p>Rserver.java - This class maintains a TCP connection with another
 *	entity.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>January 27, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Fixed, tested, and submitted to SVN.</li>
 *		</ul>
 *		<li>January 26, 2008, Benjamin Gauronskas</li>
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
public class Rserver
{
	static private ServerSocket server;// srvr = new ServerSocket(1234);
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
			System.err.println(	"!@#$\tConnection failed!");
			e.printStackTrace();
		}
		System.out.println("!@#$\tServer set successfully.");

		//Initializing locks.
		//connection_lock = new Lock();

		//Make this true when we connect, false otherwise. Using the set method
		//for concurrency.
		connected = new AtomicBoolean(false);
		


		//Make a new thread that will listen for the client.
		client = null;
		//Now we are not blocked by the blocking wait.
		new GetClient();

		//Also start up the message listener.
		new MessageListener();


	}


	/** 
	 * <p>GetClient is an internal class made for multi-threading purposes.
	 *	because Socket.accept() is a blocking wait, to have this happen in
	 *	the mainline thread would actively "halt" progress, and stop any other
	 *	initialization work that can be done.</p>
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1 
	 */
	static class GetClient implements Runnable{
		Thread t;
		//Socket client_socket;
		/**
		 * Create a thread with new GetClient(); This will make the blocking
		 *	wait work somewhere else.
		 *
		 * @author			Benjamin Gauronskas
		 */
		GetClient () {
			//client_socket = null;
			t = new Thread(this,"Client Get");
			t.start();
		}
		/**
		 * Simply makes a thread to accept an incoming client connection.
		 *
		 * @author			Benjamin Gauronskas
		 */
		public void run() {
			try{
				//client_socket = server.accept();
				Rserver.setClient(server.accept());
			}
			catch (IOException e) {
				System.err.println(	"!@#$\tCan't connect to foreign body!");
				e.printStackTrace();
			}
			//Rserver.setClient(client_socket);
		}
	}

	/** 
	 * <p>GetClient is an internal class made for multi-threading purposes.
	 *	because Socket.accept() is a blocking wait, to have this happen in
	 *	the mainline thread would actively "halt" progress, and stop any other
	 *	initialization work that can be done.</p>
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1 
	 */
	static class MessageListener implements Runnable{
		Thread t;
		//Socket client_socket;
		/**
		 * This thread listens for incoming messages.
		 *
		 * @author			Benjamin Gauronskas
		 */
		MessageListener () {
			//client_socket = null;
			t = new Thread(this,"Message Listener");
			t.start();
		}
		/**
		 * Simply makes a thread to pass message objects received to the proper
		 * resource.
		 *
		 * @author			Benjamin Gauronskas
		 */
		public void run() {
			Message message;
			//This should probably run forever... someone figure out why this
			//is a bad idea, and I'll be happy to change this.
			while (true ){
				try{

					//Do nothing while we are unconnected.
					while(!connected.get());
						message = (Message) ObjIn.readObject();
						//Later make it so that a new thread is created here
						//to handle each message appropriately.
						System.out.println(message);

				}
				catch (EOFException e){
					//Do nothing.
				}
				catch (IOException e) {
					System.err.println(	"!@#$\tFOOBAR!");
					e.printStackTrace();
				}
				catch (ClassNotFoundException e) {
					System.err.println( "!@#$\tSome kinda weird class was" +
							" given unto us.");
				}

				//Rserver.setClient(client_socket);
			}//End of while...
		}
	}

	/**
	 * This sets the client socket. I don't imagine anyone should be using
	 * this outside of this class, but I'll leave it as a public class for now.
	 *
	 * @author			Benjamin Gauronskas
	 */
	public static void setClient(Socket client_socket){
		connection_lock.lock();


		client = client_socket;
		System.out.println("!@#$\tConnection established.\n\t"+
							"Client address:\t" + client.getInetAddress());
		//THIS IS FOR FUCKING TESTING ONLY, FUCKING DELETE LATER
		try{
			ObjOut = new ObjectOutputStream(client.getOutputStream());
			ObjIn = new ObjectInputStream(client.getInputStream());
			connected.set(true);
			System.out.println("!@#$\tHopefully connected now.");
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tCan't read network output/Input!");
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
