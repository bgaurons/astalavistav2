/**
 * 
 */
package remoteControl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import commonData.*;
import service.Service;

//RemoteControlService
//Starts GUI, and maintains a clients list storing threads for each connected client
//Responsible for sending message and receiving/processing message read.
public class RemoteControlService extends Service {
	//GUI
	public RemoteControlGUI remoteControlGUI;
	//Boolean value indicating the service is running or paused
	public boolean isListening;
	//Server socket
	public ServerSocket serverSocket;
	//Clients list
	public Vector<RobotClient> clients;
	//The max id assigned to each client
	private int maxID;

	public RemoteControlService() {
		//Create the GUI
		this.remoteControlGUI = new RemoteControlGUI(this);
		this.remoteControlGUI.setSize(400, 500);
		this.remoteControlGUI.setResizable(false);
		this.remoteControlGUI.setVisible(true);

		//Disable listening
		this.isListening = false;
		//Create the client list
		this.clients = new Vector<RobotClient>();
		//Set first client id to 1
		this.maxID = 1;

		try {
			//Start server socket
			this.serverSocket = new ServerSocket(
					CommonData.RemoteControlServerPort, 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		//Start listening
		this.StartListening();
		
		//Loop while listening is enabled
		while (this.isListening) {
			try {
				//Wait for client to connect
				Socket clientSocket = this.serverSocket.accept();
				//Create new client with id, client socket and RemoteControlService.				
				RobotClient client = new RobotClient(this.maxID++,
						clientSocket, this);

				//Lock the clients
				synchronized (this.clients)
				{
					//Add new client to clients list
					this.clients.add(client);
					//Notify the GUI to handle the event
					this.remoteControlGUI.ClientsChangedEventHandler.OnClientsChanged(new ClientsChangedEvent(this.clients));
				}
				
				//Start the client thread, the thread will maintain the communication with the Robot Client
				new Thread(client).start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Stop() {
	}

	public void StartListening() {
		this.isListening = true;
	}

	public void StopListening() {
		this.isListening = false;
	}

	public static void main(String[] args) {
		new Thread(new RemoteControlService()).start();
	}
}
