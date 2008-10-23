/*
This class is pretty good
*/

import java.io.*;
import java.util.*;
import gnu.io.*;

public class Arduino
{  
	/**
	Message to get sonar0 reading.
	*/
	public static final byte FRONTSONAR_GET			= '0';

	/**
	Message to get sonar1 reading.
	*/	
	public static final byte LEFTSONAR_GET			= '1';
	
	/**
	Message to get sonar2 reading.
	*/
	public static final byte RIGHTSONAR_GET			= '2';
	
	/**
	Message to get compass reading.
	*/
	public static final byte COMPASS_GET		= 'c';
	
	/**
	Front sonar sensor reading.
	*/
	private int sonarFrontVal;
	
	/**
	Left sonar sensor reading.
	*/
	private int sonarLeftVal;
	
	/**
	Right sonar sensor reading.
	*/
	private int sonarRightVal;
	
	/**
	Compass sensor reading.
	*/
	private float compassVal;
	
	
	public static int sonarLeftAngle = 270;
	public static int sonarRightAngle = 90;
	public static int sonarFrontAngle = 0;
	
	/**
	The input data stream from the arduino board.
	*/
	private InputStream in;
	
	/**
	The stream to which we write information to the Arduino board
	*/
	private OutputStream out;
	
	/**
	The port to which the Arduino board is connected.
	*/
	private SerialPort serialPort;


	/**
	Tests the functionality of the sonar and compass sensor

	@param		args		These are ignored
	@author		Scott Fisk & Benjamin Gauronskas
    */
	public static void main(String[] args) {
		Arduino arduino = new Arduino("COM6");
		while(true){
			try {
				System.out.println(arduino.getCompass());
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}


	public Arduino(String comPort) {
		compassVal = 0;
		sonarFrontVal = 0;
		sonarLeftVal = 0;
		sonarRightVal = 0;
		
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();

		
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
	         out = serialPort.getOutputStream();
	      } catch (IOException e) {}
	      
	      
	      try {
	         // set port parameters
	         serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
	                     SerialPort.STOPBITS_1, 
	                     SerialPort.PARITY_NONE);
	      } catch (UnsupportedCommOperationException e) {}
	      
	      // start the read thread
	      //readThread = new Thread(this);
	      //readThread.start();
	      try {
			Thread.sleep(1300);
		} catch (InterruptedException e) {
	      	// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	   }
	
	/**
	gets the right sonar reading

	@author		Scott Fisk
    */
	public float getRightSonar(){
		float returnValue = 0;
		byte[] readBuffer = new byte[10];

		try {
			out.write(RIGHTSONAR_GET);
			out.flush();
			Thread.sleep(30);
				
           	in.read(readBuffer);

           	String result  = new String(readBuffer);
            
            if ( result.trim().length () != 0 ){
            	sonarRightVal = Integer.parseInt(result);	
            }
            else{
            	System.out.println("Trim 0 string");
            	sonarRightVal = 0;
			}

		}
		catch (IOException ex) {}
		catch (InterruptedException e) {}
		
		returnValue = sonarRightVal;
		
		return returnValue;
	}

	/**
	gets the left sonar reading

	@author		Scott Fisk
    */
	public float getLeftSonar(){
		float returnValue = 0;
		byte[] readBuffer = new byte[10];

		try {
			out.write(LEFTSONAR_GET);
			out.flush();
			Thread.sleep(30);
				
           	in.read(readBuffer);

           	String result  = new String(readBuffer);
            
            if ( result.trim().length () != 0 ){
            	sonarLeftVal = Integer.parseInt(result);	
            }
            else{
            	System.out.println("Trim 0 string");
            	sonarLeftVal = 0;
			}

		}
		catch (IOException ex) {}
		catch (InterruptedException e) {}
		
		returnValue = sonarLeftVal;
		
		return returnValue;
	}

	/**
	gets the front sonar reading

	@author		Scott Fisk
    */
	public float getFrontSonar(){
		float returnValue = 0;
		byte[] readBuffer = new byte[10];

		try {
			out.write(FRONTSONAR_GET);
			out.flush();
			Thread.sleep(30);
				
           	in.read(readBuffer);

           	String result  = new String(readBuffer);
            
            if ( result.trim().length () != 0 ){
            	sonarFrontVal = Integer.parseInt(result);	
            }
            else{
            	System.out.println("Trim 0 string");
            	sonarFrontVal = 0;
			}

		}
		catch (IOException ex) {}
		catch (InterruptedException e) {}
		
		returnValue = sonarFrontVal;
		
		return returnValue;
	}

	

	/**
	gets the compass reading

	@author		Scott Fisk
    */
	public float getCompass(){
		float returnValue = 0;
		byte[] readBuffer = new byte[10];

		try {
			out.write(COMPASS_GET);
			out.flush();
			Thread.sleep(30);
				
           	in.read(readBuffer);

           	String result  = new String(readBuffer);
            
            if ( result.trim().length () != 0 ){
            	compassVal = Float.parseFloat(result);	
            }
            else{
            	System.out.println("Trim 0 string");
            	compassVal = 0;
			}

		}
		catch (IOException ex) {}
		catch (InterruptedException e) {}
		
		returnValue = compassVal;
		
		return returnValue;
	}

}

