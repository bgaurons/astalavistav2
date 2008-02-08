package touchSensor;

import service.Service;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import coreProcessor.*;

public class TouchSensorService extends Service implements SerialPortEventListener {
	static CommPortIdentifier portId;
	InputStream inputStream;
	SerialPort serialPort;
	Thread readThread;

	private Vector<TouchedEventHandler> touchedEventHandlers;
	public CoreProcessor coreProcessor;

	public void run() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
	}

	public void Stop() {
	}



	public TouchSensorService(CoreProcessor coreProcessor)
	{
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
		
		this.coreProcessor = coreProcessor;
		this.touchedEventHandlers = new Vector<TouchedEventHandler>();
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
			byte[] readBuffer = new byte[1];

			try {
				while (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}
				
				if (readBuffer[0] == 49)
				{
					this.onTouched();
				}
				
			} catch (IOException e) {
			}
			break;
		}
	}

	public synchronized void AddTouchedEventHandler(TouchedEventHandler handler) {
		this.touchedEventHandlers.addElement(handler);
	}

	public synchronized void RemoveTouchedEventHandler(
			TouchedEventHandler handler) {
		this.touchedEventHandlers.removeElement(handler);
	}

	private void onTouched() {
		for (int i = 0; i < this.touchedEventHandlers.size(); i++) {
			this.touchedEventHandlers.elementAt(i).OnTouched(new TouchedEvent(this));
		}
	}
}

//public static void main(String[] args) {
//System.out.println("Bump Sensor Output");
//portList = CommPortIdentifier.getPortIdentifiers();
////System.out.println("This still runs!");
//while (portList.hasMoreElements()) {
//	//System.out.println("This still runs!");
//	portId = (CommPortIdentifier) portList.nextElement();
//	if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
//		System.out.println(portId.getName());
//		if (portId.getName().equals("COM3")) {
//			{
//				TouchSensorService reader = new TouchSensorService();
//				System.out.println("Something found on COM3!");
//			}
//			//  if (portId.getName().equals("/dev/term/a")) {
//
//		}
//	}
//}
//}
