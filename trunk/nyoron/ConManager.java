
//The following are for the config file functionality.
import java.util.Properties;
import java.io.FileInputStream;

//These are for networking functions.
import java.io.*;
import java.net.*;

//Neccesarry for multithreading
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;


import java.io.IOException;
import java.lang.ClassNotFoundException;

/**
 * <p>ConManager.java - This class contains the logic to maintain the
 *  connection between a server and a client. It has methods to send and
 *  receive objects that inherit from the message type</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>March 19, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Added new messages to switch types.</li>
 *		</ul>
 *		<li>March 19, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Integrated into the main code.</li>
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
 * @version			0.1
 */
public class ConManager
{
	//Constants to make the code read easier. Used for the boolean:
	//connectionType.
	public static final boolean SERVER = true;
	public static final boolean CLIENT = false;
	//I need both to make this object usable in multiple locations

	/**
	The actual socket connection between two machines.
	*/
	private Socket connection;// = srvr.accept();


	/**
	The outbound channel for traffic
	*/
	private ObjectOutputStream ObjOut;

	/**
	The inbound channel for traffic
	*/
	private ObjectInputStream ObjIn;


	/**
	The lock that keeps communication channels mutually exclusive.
	*/
	private ReentrantLock connectionLock;// = new ReentrantLock();

	//Need to know when closing ports and such.
	/**
	This tells whether this is a server connection or a client connection
	*/
	protected boolean connectionType;

	/**
	This tells if we are connected or not.
	*/
	private AtomicBoolean connected;

	/**
	The thread that listens for inbound messages and passes them to the
	message handler.
	*/
	private MessageListener msgListener;

	/**
	The motor for the robot.
	*/
	public boolean motorStarted;
	/**
	The temperature GUI panel needs to be globally accesible to receive
	incoming messages
	*/
	public boolean tempPanelStarted;

	/**
	The gui element that shows where the robot is.
	*/
	public boolean botPanelStarted;

	//Use default constructor on the client.

    /**
	Constructor. It gets its information from a hard properties file, so it
	takes no parameters.
	@author		Benjamin Gauronskas
    */
	public ConManager()
	{
		Properties config;

		connection = null;

		//First try to see if client.config exist.
		config = new Properties();
		try {
			config.load(new FileInputStream("client.config"));
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tFailed to read configuration file for " +
								"the connection");
			e.printStackTrace();
		}

		while(connection == null){

			try{
				connection = new Socket(
							config.getProperty("Address").trim(),
							Integer.parseInt(config.getProperty("Port").trim())
										);
			}
			catch (IOException e) {
				System.err.println(	"!@#$\tConnection failed!");
				e.printStackTrace();
			}

			try{
				Thread.sleep(5000);
			}catch(InterruptedException ex){}
		}

		//Get our streams set up.
		try{
			ObjOut = new ObjectOutputStream(connection.getOutputStream());
			ObjIn = new ObjectInputStream(connection.getInputStream());
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tFailure to open streams!");
			e.printStackTrace();
		}

		//Finally the lock for synchronization. I think locks are
		//easier to read then synchronize blocks.

		motorStarted = false;
		tempPanelStarted = false;
		botPanelStarted = false;


		connectionLock = new ReentrantLock();


		connectionType = CLIENT;
		Registers.connectionMade = true;

		connected = new AtomicBoolean(true);
		msgListener = new MessageListener();

	}

    /**
	Constructor. It is meant to be used with a serverSocket using the accept()
	method. the returned socket object should be used as a parameter for this
	method.
	@param		incoming		An accepted connection.

	@author		Benjamin Gauronskas
    */
	public ConManager(Socket incoming)
	{
		Properties config;

		connection = incoming;


		//Get our streams set up.
		try{
			ObjOut = new ObjectOutputStream(connection.getOutputStream());
			ObjIn = new ObjectInputStream(connection.getInputStream());
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tFailure to open streams!");
			e.printStackTrace();
		}

		//Finally the lock for synchronization. I think locks are
		//easier to read then synchronize blocks.
		connectionLock = new ReentrantLock();
		connectionType = SERVER;
		Registers.connectionMade = true;
		connected = new AtomicBoolean(true);
		msgListener = new MessageListener();

	}





	/**
	 * This sends a message object over the stream.
	 *
	 * @param		message	The message object to send.
	 *
	 * @author				Benjamin Gauronskas
	 */
	public void sendMessage(Message message){
		connectionLock.lock();

		//System.out.println(message);

		try{
			ObjOut.writeObject(message);
			ObjOut.flush();
			//System.out.println("!@#$\tMessage seems to have sent correctly.");
		}
		catch (IOException e) {
			System.err.println(	"!@#$\tCan't Write message!");
			e.printStackTrace();
		}
		//It can be disastrous if the lock is not unlocked because of a failure
		//in the try. finally{} forces it to happen.
		finally{
			connectionLock.unlock();
		}
		//END OF 2439FGUHFGAJNDFOPNADVKJGMFDA

	}

	/**
	 * <p>Message listener responds when a new message is received by the
	 * computer. It then creates a message handler thread to process whatever
	 * logic is entailed.</p>
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1
	 */
	private class MessageListener implements Runnable{
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
			if(connectionType == SERVER){
				while(	!tempPanelStarted ||
						!botPanelStarted);

			}
			else{
				while(!motorStarted);
			}
			while (true ){
				try{

					//Do nothing while we are unconnected.
					while(!connected.get());

					message = (Message) ObjIn.readObject();
					//Later make it so that a new thread is created here
					//to handle each message appropriately.
					//System.out.println(message);

					//Make a new thread to handle the message
					new MessageHandler(message);

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
	 * <p>Message handler takes a message object, deciphers the message type,
	 * and then it passes whatever logic is entailed to it to the proper
	 * resources.</p>
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1
	 */
	private class MessageHandler implements Runnable{
		Thread t;
		Message msg;
		//Socket client_socket;
		/**
		 * Constructor takes the message to be handles
		 *
		 *
		 * @param	msg		The message to handle
		 * @author			Benjamin Gauronskas
		 */
		MessageHandler (Message msg) {
			//client_socket = null;
			this.msg = msg;
			t = new Thread(this,"Message Listener");
			t.start();
		}
		/**
		 * This is a basic switch statement to put the message to the right
		 * handler method.
		 *
		 * @author			Benjamin Gauronskas
		 */
		public void run() {

			//Send the message to the correct method to handle
			switch(msg.getType()){
				case Message.DEBUG:		break; //Just throw it away.
				case Message.THERM:		therm((ThermMessage) msg); break;
				case Message.AI:		ai((AIMessage) msg); break;
				case Message.POSIT:		position((PosMessage) msg);break;
				case Message.MAPIM:		mapping((MapMessage) msg);break;
				case Message.MANUL:
					if (Registers.ai.aiType == AI.MANUAL){
						manual((ManMessage) msg);} break;
				case Message.MOTOR:
					if (Registers.ai.aiType == AI.MANUAL){
						motor((MotMessage) msg);} break;
				default: break; //Means junk... hopefully.
			}
		}


		/**
		 * This metod handles a motor message and then passes the instructions
		 * to the motor entailed in the Registers class.
		 *
		 *
		 * @param	msg		The motor message to parse.
		 * @author			Benjamin Gauronskas
		 */
		private void motor(MotMessage msg){
			//System.out.println("MOTOR: " + msg.control + " " + msg.value);
			switch(msg.control){
				case MotMessage.CTRL_STOP:
					System.out.println("Bye");
					Registers.motor.mode(Motor.INDEPENDANT_SIGNED);
					Registers.motor.rightMotor(0);
					Registers.motor.leftMotor(0);
					// turns on automatic motor timeout
					Registers.motor.command(Motor.TWO_SEC_TO_ENABLE);
					break;
				case MotMessage.CTRL_CMD:
					Registers.motor.mode(msg.value);
					break;
				case MotMessage.CTRL_FWD:
					Registers.motor.setForward(msg.value);
					break;
				case MotMessage.CTRL_TRN:
					Registers.motor.setTurn(msg.value);
					break;
				case MotMessage.CTRL_ACL:
					Registers.motor.accel(msg.value);
					break;
				default: break;
			}
		}

		/**
		 * This metod refreshes the temperature panel with a new message from
		 * the robot.
		 *
		 *
		 * @param	msg		The thermopile message to parse.
		 * @author			Benjamin Gauronskas
		 */
		private void therm(ThermMessage msg){
			Registers.tempPanel.refresh(msg.map);
		}

		/**
		 * This method changes the robot's direction and location depending
		 * on a button pressed at the terminal.
		 *
		 *
		 * @param	msg		The message with the direction pressed.
		 * @author			Benjamin Gauronskas
		 */
		private void manual(ManMessage msg){
			MovementLogic.calculateMan(msg.direction);
		}

		/**
		 * This method stops the old AI, and starts the new one.
		 *
		 *
		 * @param	msg		The message with the new AI type.
		 * @author			Benjamin Gauronskas
		 */
		private void ai(AIMessage msg){
			if(Registers.ai != null){
				//Stop the ai.
				Registers.ai.stop();

				switch(msg.aiType){
					case AI.MANUAL: Registers.ai = new ManualAI(); break;
					case AI.THERML: Registers.ai = new ThermlAI(); break;
					case AI.PASSWO: Registers.ai = new PassAI(); break;
					case AI.ROAMIN: Registers.ai = new RoamAI(); break;
					default: break;
				}
			}
		}

		/**
		 * Changes the positional information in the GUI.
		 *
		 *
		 * @param	msg		The message with the new Position information.
		 * @author			Benjamin Gauronskas
		 */
		private void position(PosMessage msg){
			Registers.mainPac.parsePosMessage(msg);
		}

		/**
		 * Updates the server's map.
		 *
		 *
		 * @param	msg		The message containing the updated map
		 * @author			Benjamin Gauronskas
		 */
		private void mapping(MapMessage msg){
			Registers.mainPac.updateMap(msg.mapImage);
		}
	}


}