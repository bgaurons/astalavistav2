package sonarSensor;

import service.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;
import java.util.Vector;
import commonData.CommonData;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import coreProcessor.*;

public class SonarSensorService extends Service implements SerialPortEventListener {
	
	static CommPortIdentifier portId;
	private InputStream inputStream;
	private SerialPort serialPort;
	private int distance;
	public CoreProcessor coreProcessor;
	
	public void Stop() {
		
	}
	
	public SonarSensorService(CoreProcessor coreProcessor) {
		try {
			serialPort = (SerialPort) portId.open("MainClassApp", 2000);
		} catch (PortInUseException e) {
		}
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
		}
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		}
		
		this.distance = 0;
		
		this.coreProcessor = coreProcessor;
	}

	public void run() {
		while (true)
			;
	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[20];

			try {
				while (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}
				
				int inch = Integer.parseInt((new String(readBuffer).substring(1)));
				if (this.distance != inch)
				{
					this.DistanceValue(true, inch);
				}
				else
				{
					this.DistanceValue(true, inch);
				}
			
			} catch (IOException e) {
			}
			break;
		}
	}
	
	//This function is used to update/retrieve the current distance value
	//Since the sonar sensor has uncertain update frequency, this function enforces that everytime the value is requested, the sensor needs to measure the new distance again.
	//To improve the performance, use Read/Write Lock on the distance value
	public synchronized int DistanceValue(boolean direction, int inch)
	{
		if (direction == true)
		{
			this.distance = inch;
			return 0;
		}
		else
		{
			if (this.distance != CommonData.NULL)
			{
				int dis = this.distance;
				this.distance = CommonData.NULL;
				return dis;
			}
			else
				return CommonData.NULL;
		}
	}
		
//	public static void main(String[] args) {
//		System.out.println("Sonar Output");
//		portList = CommPortIdentifier.getPortIdentifiers();
//		//System.out.println("This still runs!");
//		while (portList.hasMoreElements()) {
//			//System.out.println("This still runs!");
//			portId = (CommPortIdentifier) portList.nextElement();
//			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
//				System.out.println(portId.getName());
//				if (portId.getName().equals("COM1")) {
//					{
//						SonarSensorService reader = new SonarSensorService();
//						System.out.println("Something found on COM1!");
//					}
//					//  if (portId.getName().equals("/dev/term/a")) {
//
//				}
//			}
//		}
//	}
}