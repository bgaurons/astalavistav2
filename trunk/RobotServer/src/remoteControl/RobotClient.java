package remoteControl;

import java.io.*;
import java.net.Socket;

import commonData.CommonData;

//Defines thread maintaining communication with the Robot client
public class RobotClient implements Runnable {
	//Client socket
	private Socket clientSocket;
	//Client id
	private int id;
	//RemoteControlService
	private RemoteControlService remoteControlService;
	//Boolean value indicating the thread is running or paused
	private boolean isWorking;
	//Socket reader
	private BufferedReader reader;
	//Socket writer
	private PrintWriter writer;

	public RobotClient(int id, Socket socket,
			RemoteControlService remoteControlService) {
		this.id = id;
		this.clientSocket = socket;
		this.remoteControlService = remoteControlService;
		//Disable working
		this.isWorking = false;
		
		try {
			//Create socket reader
			this.reader = new BufferedReader(new InputStreamReader(
					this.clientSocket.getInputStream()));
			//Create socket writer
			this.writer = new PrintWriter(this.clientSocket
					.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		
		String tempData = "";
		
		while (this.isWorking) {
			try {
				//Wait for coming data
				tempData = this.reader.readLine();
			} catch (IOException e) {
				//Process error occurred in connection
				processConnectionException(e);
			}
			//Process the input data
			processInputData(tempData);
		}
	}

	private void processConnectionException(IOException e) {
		//Terminate the connection with the client
		this.processConnectionTermination();
		e.printStackTrace();
	}

	private void processConnectionTermination() {
		try {
			//Close this client
			this.clientSocket.close();
			synchronized (this.remoteControlService.clients) {
				//Update the clients list
				this.remoteControlService.clients.removeElement(this);
				//Notify the GUI
				this.remoteControlService.remoteControlGUI.ClientsChangedEventHandler
						.OnClientsChanged(new ClientsChangedEvent(
								this.remoteControlService.clients));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Function used to send data to the client (override)
	public void Send(int function, String value) {
		this.writer.println(function + CommonData.StringDataSeparator
				+ value);
		this.writer.flush();
	}
	
	//Function used to send data to the client (override)
	public void Send(int function, int value) {
		this.Send(function, value + "");
	}

	//Process the input data
	//Input data is a string in such format: "Function + Separator + Value"
	private void processInputData(String data) {
		//Get the position of the separator
		int separator = data.indexOf(CommonData.StringDataSeparator);
		//Get Function and Value
		int function = Integer.parseInt((data.substring(0, separator - 1)));
		String value = data.substring(separator
				+ CommonData.StringDataSeparator.length());

		//Process data according to the Function (to be complemented...)
		switch (function) {
		
		//Client is updating touch sensor information
		case CommonData.UpdateTouchSensorInformation:
			break;
			
		//Client is updating sonar sensor information
		case CommonData.UpdateSonarSensorInformation:
			break;
			
		//Client is updating motor information
		case CommonData.UpdateMotorInformation:
			break;
			
		//Client is updating Location information
		case CommonData.UpdateLocationInformation:
			break;
		
		//Client is going to terminate connection with server
		case CommonData.ConnectionTerminated:
			this.processConnectionTermination();
			break;

		//Client detected an object
		case CommonData.NotifyObjectDetected:
			break;
			
		//Client reported battery low
		case CommonData.NotifyLowBattery:
			break;

		//Client is sending user inputed password to server
		case CommonData.InputtedPassword:
			break;

		default:
			break;
		}
	}

	public int GetID() {
		return this.id;
	}

	public void StartWorking() {
		this.isWorking = true;
	}

	public void StopWorking() {
		this.isWorking = false;
	}
}
