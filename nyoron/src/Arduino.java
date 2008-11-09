/*
This class is pretty good
*/

import java.io.*;
import java.util.*;

import gnu.io.*;

/**
 * @author Scott Fisk
 *
 */
public class Arduino implements Runnable, SerialPortEventListener
{  
	/**
	Change how many data points to save when performing averages.
	*/
	static final int HISTORY_DATA_POINTS			= '5';
	
	/**
	Raw data coming from arduino
	 */
	private StringBuffer readBuffer = new StringBuffer(20);
	
	/** 
	most recent readings of datas from arduino 
	 */
	private int sonarFrontVal[] = new int[HISTORY_DATA_POINTS];
	private int sonarLeftVal[] = new int[HISTORY_DATA_POINTS];
	private int sonarRightVal[] = new int[HISTORY_DATA_POINTS];
	private float compassVal[] = new float[HISTORY_DATA_POINTS];
	
	/** 
	radian values of angle and distance offsets of sensors from center of robot 
	 */
	public static double sonarLeftAngle = (3*Math.PI)/2.0;
	public static double sonarRightAngle = Math.PI;
	public static double sonarFrontAngle = 0;
	
	public static int sonarLeftDistance = 9;
	public static int sonarRightDistance = 9;
	public static int sonarFrontDistance = 9;
	
	/**
	The input data stream from the arduino board.
	*/
	private InputStream in;
	
	/**
	The port to which the Arduino board is connected.
	*/
	private SerialPort serialPort;
	
	/**
	The thread which will constantly read arduino data
	*/
	Thread readThread;

	/**
	Tests the functionality of the sonar and compass sensor

	@param		args		These are ignored
	@author		Scott Fisk
    */
	public static void main(String[] args) {
		Arduino arduino = new Arduino("COM6");
		while(true){
			try {
				System.out.println(arduino.getRightSonar());
				System.out.println(arduino.getLeftSonar());
				System.out.println(arduino.getFrontSonar());
				System.out.println(arduino.getCompass());
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}

	/**
	Constructor to initalize the arduino serial port and then start the arduino thread.

	@param		comPort 	pass a comport of the arduino
	@author		Scott Fisk
    */
	public Arduino(String comPort) {
		
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();

		// earth to find the comport
		CommPortIdentifier channelId =
			(CommPortIdentifier) portList.nextElement();
		while (portList.hasMoreElements() &&
			!(channelId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
			channelId.getName().equals(comPort))) {
			channelId =	(CommPortIdentifier) portList.nextElement();
		}

		
		
	      // initalize serial port
	      try {
	         serialPort = (SerialPort) channelId.open("arduinoRead", 2000);
	      } catch (PortInUseException e) {}
	   
	      try {
	         in = serialPort.getInputStream();
	      } catch (IOException e) {}
	      
	      try {
			serialPort.addEventListener(this);
	      } catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	      }
	      
	      serialPort.notifyOnDataAvailable(true);
	      
	      try {
	         // set port parameters
	         serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
	                     SerialPort.STOPBITS_1, 
	                     SerialPort.PARITY_NONE);
	      } catch (UnsupportedCommOperationException e) {}
	      
	      // start the read thread
	      readThread = new Thread(this);
	      readThread.start();
	      
	   }
	
	/**
	gets the right sonar reading

	@author		Scott Fisk
    */
	public float getRightSonar(){
		float returnValue = 0;
		
		// consult history to obtain a more accurate reading
		returnValue = averageHistory(sonarRightVal);
		
		return returnValue;
	}

	/**
	gets the left sonar reading

	@author		Scott Fisk
    */
	public float getLeftSonar(){
		float returnValue = 0;
		
		// consult history to obtain a more accurate reading	
		returnValue = averageHistory(sonarLeftVal);
		
		return returnValue;
	}

	/**
	gets the front sonar reading

	@author		Scott Fisk
    */
	public float getFrontSonar(){
		float returnValue = 0;

		// consult history to obtain a more accurate reading
		returnValue = averageHistory(sonarFrontVal);
		
		return returnValue;
	}


	/**
	gets the compass reading

	@author		Scott Fisk
    */
	public float getCompass(){
		float returnValue = 0;
		
		// consult history to obtain a more accurate reading
		returnValue = averageHistory(compassVal);
		
		return returnValue;
	}

	/**
	Averages an array of ints. 
	
	@param		sonarLocal 	Pass the array of sonar data to be averaged.
	@author		Scott Fisk
    */
	private float averageHistory(int[] sonarLocal) {
		int i = 0;
		float sum = 0;
		float average = 0;
		
		// perform average
		for (;i<HISTORY_DATA_POINTS;i++){
			sum += sonarLocal[i];
		}
		average = sum/HISTORY_DATA_POINTS;
		
		return average;
	}

	/**
	Averages an array of floats. 
	
	@param		compassLocal 	Pass the array of sonar data to be averaged.
	@author		Scott Fisk
    */
	private float averageHistory(float[] compassLocal) {
		int i = 0;
		float sum = 0;
		float average = 0;
		
		// perform average
		for (;i<HISTORY_DATA_POINTS;i++){
			sum += compassLocal[i];
		}
		average = sum/HISTORY_DATA_POINTS;
		
		return average;
	}


	/**
	Starts a sleeping thread to handle interrupts 
	
	@author		Scott Fisk
    */
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	/**
	Handles serial events when data is available. 
	
	@param		event 	Serial port event.
	@author		Scott Fisk
    */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				// we get here if data is available on serial port
				readSerial();
		}
	}
	
	/**
	reads a raw serial line until a new line character

	@author		Scott Fisk
    */
	private void readSerial() {
		// variable to store the serial data char by char to check for new line
		int nextChar;
		try {
			// loop until newline
			while((nextChar=in.read()) != '\n'){
				if(nextChar != '\r')
					readBuffer.append((char)nextChar);
			}
//			System.out.println(readBuffer);
			parseBuffer(readBuffer.toString());
			readBuffer.setLength(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	Parses the buffer file and then adds to history 
	
	@param		readBuffer 	Raw data read off of serial from arduino
	@author		Scott Fisk
    */
	private void parseBuffer(String readBuffer) {
		String[] splitS = readBuffer.split(" ");
		int sonarFront = 0;
		int sonarLeft = 0;
		int sonarRight = 0;
		float compass = 0;
		
		sonarFront = Integer.parseInt(splitS[0]);
		sonarLeft = Integer.parseInt(splitS[1]);
		sonarRight = Integer.parseInt(splitS[2]);
		compass = Float.parseFloat(splitS[3]);
		
		addToHistory(sonarFront, sonarLeft, sonarRight, compass);
	}

	/**
	Adds all sensor data to a history of user specified points.  
	
	@param		sonarFront 	Front sonar data value to be added to history
	@param		sonarLeft 	Left sonar data value to be added to history
	@param		sonarRight 	Right sonar data value to be added to history
	@param		compass 	compass data value to be added to history
	@author		Scott Fisk
    */
	private void addToHistory(int sonarFront, int sonarLeft, int sonarRight,
			float compass) {
		int i = 0;
		// shift old data up
		for (;i<HISTORY_DATA_POINTS-1;i++){
			sonarFrontVal[i+1] = sonarFrontVal[i];
			sonarLeftVal[i+1] = sonarLeftVal[i];
			sonarRightVal[i+1] = sonarRightVal[i];
			compassVal[i+1] = compassVal[i]; 
		}

		// add new data to most recent part of array
		sonarFrontVal[0] = sonarFront;
		sonarLeftVal[0] = sonarLeft;
		sonarRightVal[0] = sonarRight;
		compassVal[0] = compass; 
	}
	
}
