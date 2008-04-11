import gnu.io.*;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.lang.NumberFormatException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.concurrent.locks.*;
import java.util.Scanner;


/**

IRarduino.java
This code manages the connection to the IR sensor connected to an arduino
board. Additionally, said board will be connected at some point to Push-button
sensors, and said information will need to be collected from it.

	<h1>Revision History:</h1>
	<ul>
		<li>March 28, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Rewrote the code so that it would work with the robot.</li>
			<li>Fixed bugs in output.</li>
			<li>Added comments.</li>
		</ul>
		<li>March ??, 2008, Joyce Tang</li>
		<ul>
			<li>Initial version.</li>
		</ul>
	</ul>


  @author                      Joyce Tang
  @version                     0.2
*/
public class Arduino implements Runnable, SerialPortEventListener {


/*
The arduino should probably work like so,
buttonsOn is a boolean that defaults to true.
sent is a boolean that defaults to false.

if a push button sensor is pressed and buttonsOn == true and sent == false
	send a one to output.
	set some boolean sent = true;
if a push button sensor  depressed and buttonsOn == true and sent == true
	set some boolean sent = false;
if a stop code (say a byte 0x01) is received
	set boolean "buttonsOn" = false
if a start code (say a byte 0x02) is received
	set boolean "buttonsOn" = true
if a IRget code (say a byte 0x03) is received
	send the reading from the IR sensor.


*/

	/**
	Message to stop bump sensors.
	*/
	//public static final byte BUMPER_STOP	= 'a';
	/**
	Message to start bump sensors
	*/
	//public static final byte BUMPER_START	= 'b';
	/**
	Message to get Infrared reading.
	*/
	//public static final byte IR_GET			= 'c';
	/**
	Message to get touch reading.
	*/
	//public static final byte TOUCH_GET		= 't';
	/**
	How long Arduino needs to send information to the
	*/
	public static final int ARDUINO_SLEEP	= 150;
	/**
	Message to get arduino data.
	*/
	public static final byte GET_DATA	= 'd';
	/**
	The size of a buffer used to receive info.
	*/
	public static final int BUFFER_SIZE		= 10;
	/**
	The size of a buffer used to receive info.
	*/
	public static final int BUMPSENSOR_COUNT= 1;
	/**
	Touch sensor reading.
	*/
	public boolean[] isBumped;
	/**
	IR Range Finder reading.
	*/
	public int IRSensorVal;
	/**
	The stream from which we receive information from the arduino board.
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
	A thread that spins busily while waiting for information from the arduino
	board.
	*/
	private Thread readThread;

	/**
	This lock keeps the channels mutually exclusive
	*/
	private ReentrantLock dataLock;

	/**
	Tests the functionality of the infrared and touch sensors

	@param		args		These are ignored
	@author		Benjamin Gauronskas
    */
	public static void main(String[] args) {
		Arduino arduino = new Arduino("COM5");
		while(true){
			System.out.println(arduino.getIRSensor());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	/**
	Constructor for the sensor object

	@param		comPort		A human readable name for the port which the
							device is connected to.
	@author		Benjamin Gauronskas
    */
	public Arduino(String comPort) {
		IRSensorVal = 0;
		isBumped = new boolean[BUMPSENSOR_COUNT];

		dataLock = new ReentrantLock();

		Enumeration portList = CommPortIdentifier.getPortIdentifiers();


		//This voodoo cycles through all com ports to find com 4.
		CommPortIdentifier channelId =
			(CommPortIdentifier) portList.nextElement();
		while (portList.hasMoreElements() &&
			!(channelId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
			channelId.getName().equals(comPort))) {
			channelId =	(CommPortIdentifier) portList.nextElement();
		}

		//Connect to the port, God Knows what this voodoo is.
		//I think the magical 2000 is the timeout period.
		try {
			serialPort = (SerialPort) channelId.open("irRead", 2000);
		} catch (PortInUseException e) {}

		//Allows us to read from the serial port.
		try {
			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();
			serialPort.setOutputBufferSize(20);
		} catch (IOException e) {}

		//This is the one piece of code that interests me...
		//figure out how to EXPLOIT this technique.
		//To temporarily remove event listener,
		//we use the aptly named method removeEventListener()
		//it takes no parameters, and throws no exceptions.
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {}

		//I imagine this has to do with the above.
		//We need to compartmentalize this.
		//serialPort.notifyOnDataAvailable(true);

		try {
			serialPort.setSerialPortParams(	9600,
											SerialPort.DATABITS_8,
											SerialPort.STOPBITS_1,
											SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {}

		readThread = new Thread(this);
		readThread.start();
	}

	/**
	An infinite loop that will be interrupted and then sensor information
	will be taken care of in a seperate thread.


	@author		Joyce Tang
    */
	public void run() {

		//Serial string reading.
		String sensorValue = "";
		String[] sensorSplit;
		String readString;
		char nextChar;

		byte[] arduinoRead = new byte[BUFFER_SIZE];
		char arduinoCharToRead;

		int bumpValue = 0;
		int index = 0;

		while(true){


			try{

				readString = "";
				in.skip(in.available());

				out.write(GET_DATA);
				out.flush();

				Thread.sleep(ARDUINO_SLEEP);
				//in.read(arduinoRead);
				System.out.print("Arduino reading: ");
				while((nextChar = ((char)in.read()))!= '\n'){
					System.out.print(nextChar);
					readString += nextChar;
				}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}

				System.out.println();
				in.skip(in.available());


				//in.read(touchSensorReading);


				if(arduinoRead != null){
					//sensorValue = (new String(arduinoRead)).trim();
					sensorValue = (readString).trim();
				}
				else{
					sensorValue = "-1,-1";
				}
			}
			catch (IOException ex) {}
			catch (InterruptedException e) {}


			sensorSplit = sensorValue.split(",");

			if(!(sensorSplit[0].equals("-1")) &&
				!(sensorSplit[0].trim().equals(""))){

				dataLock.lock();

				IRSensorVal = Integer.parseInt(sensorValue.split(",")[0]);
				System.out.println("IRSENSOR1 " + sensorSplit[0]);
				System.out.println("IRSENSOR2 " + IRSensorVal);

				dataLock.unlock();
							try {
								Thread.sleep(500);
			} catch (InterruptedException e) {}
			}

			for(index = 1; index < sensorSplit.length; index++){
				if(sensorSplit[index].equals("1")){
					new BumpPressed(index);
				}
			}


			try {
				Thread.sleep(ARDUINO_SLEEP);
			} catch (InterruptedException e) {}
		}

	}


	/**
	gets the IR reading

	@author		Benjamin Gauronskas
    */
	public int getIRSensor(){
		int returnValue;
		dataLock.lock();
		returnValue = IRSensorVal;
		dataLock.unlock();

		System.out.println("Getting IR Sensor: " + returnValue +
							" Reading" + IRSensorVal);
					try {
						Thread.sleep(500);
			} catch (InterruptedException e) {}

		return returnValue;
	}

	/**
	The interrupt for when data is available.

	@param		event		The happening that triggered the interrupt.

	@author		Joyce Tang
    */
	public void serialEvent(SerialPortEvent event) {
		byte[] readBuffer;
		byte nextChar = '0';

		switch (event.getEventType()){
			case SerialPortEvent.BI:
			case SerialPortEvent.OE:
			case SerialPortEvent.FE:
			case SerialPortEvent.PE:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: break;

			//This is the only thing we are looking for.
			case SerialPortEvent.DATA_AVAILABLE:

				//System.out.println("Interrupted.");
				readBuffer = new byte[BUFFER_SIZE];
				int numBytes = 0;

				try {
					int i = 0;
					while (nextChar != '\n' && i < BUFFER_SIZE) {
						//Get rid of whatever is in the buffer
						nextChar = (byte)in.read();
						readBuffer[i] = nextChar;
						i++;
					}

					System.out.println((new String(readBuffer)).trim());

					//Insert logic to handle the bump... probably back up
					//then rotate and drive again.
				} catch (IOException e) {}

			default: break;
		}
	}

	/**
	 * The thread that responds to a bump sensor
	 *
	 * @author			Benjamin Gauronskas
	 * @version			0.1
	 */
	private static class BumpPressed implements Runnable
	{

		public Thread t;
		public int bumpNumber;


		// Constructor initilized vars
		/**
		 Constructor takes bump Pressed.

		 @param	bumpNumber		Sensor triggered.
		 @author			Benjamin Gauronskas
		 */
		public BumpPressed(int bumpNumber)
		{

			this.bumpNumber = bumpNumber;
			t = new Thread(this,"button " + bumpNumber);
			t.start();
		}

		/**
		 What to do when a button is pressed


		 @author			Benjamin Gauronskas
		 */
		public void run()
		{
			System.out.println("Bump number " + bumpNumber);
		}
	}


}



/*
The arduino should probably work like so,
buttonsOn is a boolean that defaults to true.
sent is a boolean that defaults to false.

if a push button sensor is pressed and buttonsOn == true and sent == false
	send a one to output.
	set some boolean sent = true;
if a push button sensor  depressed and buttonsOn == true and sent == true
	set some boolean sent = false;
if a stop code (say a byte 0x01) is received
	set boolean "buttonsOn" = false
if a start code (say a byte 0x02) is received
	set boolean "buttonsOn" = true
if a IRget code (say a byte 0x03) is received
	send the reading from the IR sensor.


*/