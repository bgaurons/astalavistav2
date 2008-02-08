package coreProcessor;

import java.io.*;
import java.net.Socket;
import commonData.CommonData;
import service.Service;
import mapLearner.*;
import motorDriver.*;
import sonarSensor.*;
import touchSensor.*;

public class CoreProcessor extends Service {

	//All the services
	public MapLearnerService mapLearnerService;
	public MotorDriverService motorDriverService;
	public SonarSensorService sonarSensorService;
	public TouchSensorService touchSensorServcie;
	
	//Communication Related
	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	
	//Boolean value indicating the working status
	private boolean isWorking;
	//Robot ID
	private int id;

	public CoreProcessor() throws Exception {
		this.isWorking = false;
		this.id = CommonData.NULL;
		//Create the connection to the server
		this.socket = new Socket("127.0.0.1",
				CommonData.RemoteControlServerPort);
		//Create reader and writer
		this.reader = new BufferedReader(new InputStreamReader(this.socket
				.getInputStream()));
		this.writer = new PrintWriter(this.socket.getOutputStream());

		//Create all the services
		this.touchSensorServcie = new TouchSensorService(this);
		this.sonarSensorService = new SonarSensorService(this);
		this.motorDriverService = new MotorDriverService(this);
		this.mapLearnerService = new MapLearnerService(this,
				this.sonarSensorService, this.motorDriverService);

		//Register services to events
		this.touchSensorServcie
				.AddTouchedEventHandler(new TouchedEventHandler() {
					public void OnTouched(TouchedEvent e) {
						//((TouchSensorService)e.getSource()).coreProcessor.motorDriverService.Backward();
					}
				});
		this.touchSensorServcie
				.AddTouchedEventHandler(new TouchedEventHandler() {
					public void OnTouched(TouchedEvent e) {
						//((TouchSensorService)e.getSource()).coreProcessor.remoteControlService.UpdateSomething();
					}
				});

		//Start services
		//new Thread(this.touchSensorServcie).start();
		//new Thread(this.sonarSensorService).start();
		new Thread(this.motorDriverService).start();
		//new Thread(this.mapLearnerService).start();
	}

	public void StartWorking() {
		this.isWorking = true;
	}

	public void StopWorking() {
		this.isWorking = false;
	}

	public void Stop() {

	}

	public void run() {

		String tempData = "";
		this.StartWorking();

		while (this.isWorking) {
			try {
				//Waiting for incoming data
				tempData = this.reader.readLine();
			} catch (IOException e) {
				//Process connection errors
				this.processConnectionException(e);
			}
			//Process input data
			this.processInputData(tempData);
		}		
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
		
		//Server is going to terminate connection with this client
		case CommonData.ConnectionTerminated:
			break;
			
		//Server is sending new client id to this client
		case CommonData.NotifyNewID:
			this.id  = Integer.parseInt(value);
			break;
			
		//Server is sending manual motor command to this client
		case CommonData.MotorCommand:
			this.motorDriverService.AddCommand(Integer.parseInt(value));
			break;
			
		//Server notified this client the password sent to the server failed
		case CommonData.NotifyPasswordFailed:
			break;
		
		//Server notified this client the password sent to the server passed		
		case CommonData.NotifyPasswordPassed:
			break;
		
		default:
			break;
		}
	}
	
	//Function used to send data to the server
	public void Send(int function, String value) {
		this.writer.println(function + CommonData.StringDataSeparator  + value);
		this.writer.flush();
	}

	private void processConnectionException(IOException e) {
		try {
			this.socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		e.printStackTrace();
	}

	public static void main(String[] args) {
		try {
			new Thread(new CoreProcessor()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
