package motorDriver;

import java.io.*;
import gnu.io.*;
import java.util.*;

public class MotorDriver implements Runnable
{
	CommPortIdentifier portId;
	CommPortIdentifier com2;
	RXTXPort motors;
	static boolean go = true;
	public byte [] outpkt;
	public byte [] inpkt;
	int val;
	Integer tempVal;
	String temp;
	Enumeration portList;
	public OutputStream out;
	public InputStream in;

	int forward;
	int turn;
	int modeNum;
	int commandNum;
	int accelVal;
	double voltVal;

	int distX; // stores distance traveled in x (in blocks)
	int distY; // stores distance traveled in y (in blocks)
	int direction; // stores direction in degrees the robot is facing;

	int ack;


	/* constructor for motor established connection with Com port
	   and provides a channel for communication to com port via
	   virutal serial bus created by USB to I2C adapter. */
	public MotorDriver(String comPort) throws Exception
	{
		portList = CommPortIdentifier.getPortIdentifiers();
		portId = (CommPortIdentifier) portList.nextElement();
		outpkt = new byte [5]; // byte array to handle outgoing communication
		inpkt = new byte[10]; // byte array to handle incoming communication

		while (portList.hasMoreElements())
		{
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				if (portId.getName().equals(comPort))
				{
					break;
				}
			}
			portId = (CommPortIdentifier) portList.nextElement();
		}

		com2 = portId;
		motors = (RXTXPort) com2.open("motor driver", 5);
		motors.setSerialPortParams(19200,SerialPort.DATABITS_8,SerialPort.STOPBITS_2,SerialPort.PARITY_NONE);
		motors.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		out = motors.getOutputStream();
		in = motors.getInputStream();
		motors.setInputBufferSize(20);
		motors.setOutputBufferSize(20);

		distX = 0;
		distY = 0;
		direction = 0;

		// code for demo
		forward = 0;
		turn = 0;

		command(51); // turn off automatic motor timeout
		ack = in.read();

		mode(3); // set mode to one -127: full reverse, 0: stop, 127: full forward motors
				 // controlled together with left as turn constant
		ack = in.read();

		rightMotor(0); // set forward motor speed to stop
		ack = in.read();

		leftMotor(0); // set turn motor speed to stop
		ack = in.read();

		accel(10);
		ack = in.read();
	}

	// Thread for reading/writing to motor controller */
	public void run()
	{
		try
		{
			do
			{
				leftMotor(turn);
				ack = in.read();

				Thread.sleep(50);
				val = getVoltage();
				voltVal = (double)val/10;

				System.out.println("Voltage: " + val);

				rightMotor(forward);
				ack = in.read();
				Thread.sleep(50);
			}
			while(go);
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
			command(51); // turn automatic timeout on
		}

		motors.close(); // close communication with motors
	} // end run method*/


	/* turns robot 90 degrees to the left */
	public void turnLeft() throws IOException
	{
		command(51);
		ack = in.read();
		mode(3);
		ack = in.read();
		rightMotor(0);
		ack = in.read();
		leftMotor(100);
		ack = in.read();

		direction = (direction + 90) % 360;
	}

	/* turns robot 90 degrees to the right */
	public void turnRight() throws IOException
	{
		command(51);
		ack = in.read();
		mode(3);
		ack = in.read();
		rightMotor(0);
		ack = in.read();
		leftMotor(-100);
		ack = in.read();

		direction = (direction - 90) % 360;
	}

	/* moves robot forward one block */
	public void stepForward() throws IOException
	{
		command(51); // enable two second timeout
		ack = in.read();
		mode(3);
		ack = in.read();
		rightMotor(100);
		ack = in.read();

		// need to add code to track distance traveled

	}

	/* moves robot backward one block */
	public void stepBackward() throws IOException
	{
		command(51);
		ack = in.read();
		mode(3);
		ack = in.read();
		rightMotor(-100);
		ack = in.read();
	}

	// returns an array of length two containing the
	// x and y coordinates of the robot relative to its starting position
	public int [] distanceTraveled()
	{
		int [] result = new int [2];
		result[0] = distX;
		result[1] = distY;

		return result;
	}



	/* sets the commands for motors
	   values in decimal
	   32: Resets encoder registers to zero
	   48: Disables Speed Regulation
	   49: Enables speed regulation (default)
	   50: Disables 2 second timeout of motors
	   51: Enables 2 second timeout of motors when there is no I2C communication
	   160: 1st in sequence to change i2c address
	   170: 2nd in sequence to change i2c address
	   165: 3rd in sequence to change i2c address
	   */
	public void command(int cmd)
	{
		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB0; // address of I2C Bus
		outpkt[2] = (byte)16; // Register number
		outpkt[3] = (byte)0x01; // Number of data bytes to follow
		outpkt[4] = (byte)cmd;  // Value

		try
		{
			out.write(outpkt);
			out.flush();
		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
	}



	/* rightMotor() controls the speed of the right motor when in
	   modes 0 or 1, controls the speed of both motors when in modes
	   2 or 3.

	   Parameters: speed is the integer value which represents the
	   new speed that the motors will be set to.  When in mode 1 or 3
	   -127 is full reverse, 0 is stop and 127 full forward.
	   When in mode 0 or 2 0 is full reverse 127 stop and 255 full forward.
	*/
	public void rightMotor(int speed)
	{
		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB0; // address of I2C Bus
		outpkt[2] = (byte)0; // Register number
		outpkt[3] = (byte)0x01; // Number of data bytes to follow
		outpkt[4] = (byte)speed;// value

		try
		{
			out.write(outpkt);
			out.flush();
		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
	}

	/* controls speed of left motor or
	   controls turn constant when in mode
	   2 or 3 */
	public void leftMotor(int speed)
	{
		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB0; // address of I2C Bus
		outpkt[2] = (byte)1; // Register number
		outpkt[3] = (byte)0x01; // Number of data bytes to follow
		outpkt[4] = (byte)speed; // value

		try
		{
			out.write(outpkt);
			out.flush();
		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
	}

	/* mode can take on four values
	0 : Motors are controlled independantly with range
	of 0 (stop) to 255 (full forward)
	1 : Motors are controlled independently with range
	of -127 full reverse to 127 full forward
	2 : Motors are controlled By the same register unsigned
		Second register is turn constant
	3 : Motors are controlled by the same register signed
		Second register is turn constant */
	public void mode(int mode)
	{
		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB0; // address of I2C Bus
		outpkt[2] = (byte)15; // Register Number
		outpkt[3] = (byte)0x01; // Number of data bytes to follow
		outpkt[4] = (byte)mode; // value

		try
		{
			out.write(outpkt);
			out.flush();
		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
	}

	/* controls acceleration of motors:
	   steps to new speed = (new speed - old speed) / acceleration value
	   time to new speed = steps * 25 ms
	   */
	public void accel(int accel)
	{
		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB0; // address of I2C Bus
		outpkt[2] = (byte)14; // Register Number
		outpkt[3] = (byte)0x01; // Number of data bytes to follow
		outpkt[4] = (byte)accel; // value

		try
		{
			out.write(outpkt);
			out.flush();
		}
		catch(IOException ioex)
		{
			System.out.println("IOException Motors");
		}
	}

	/* returns battery voltage reading as integer
	   if negative one is returned there was an
	   error when communicating with motor driver

	   voltage returned is 10 times the voltage
	   (12 volts will return 120)
	*/
	public int getVoltage()
	{
		int volts = -1; // initialize to negative one;
		byte num = 33;

		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB1; // address of I2C Bus
		outpkt[2] = (byte)10; // Register Number for voltage reading
		outpkt[3] = (byte)1; // Number of data bytes to read

		try
		{
			out.write(outpkt);
			out.flush();
			while(in.available() == 0);
			volts = (int)in.read();

		}
		catch(IOException ioex)
		{
			System.out.println("Motors IOException Getting Voltage");
		}

		return volts;
	}

	/* returns current through the left motor
	   if negative one is returned there was an
	   error when communicating with motor driver

	   value returned is 10 times the amperage
	   (2.5 Amps will return 25)
	*/
	public int getLeftCurrent()
	{
		int amps = -1; // initialize to negative one;
		byte num = 33;

		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB1; // address of I2C Bus
		outpkt[2] = (byte)11; // Register Number for left current
		outpkt[3] = (byte)1; // Number of data bytes to read

		try
		{
			out.write(outpkt);
			out.flush();
			while(in.available() == 0);
			amps = (int)in.read();
		}
		catch(IOException ioex)
		{
			System.out.println("Motors IOException Getting Voltage");
		}

		return amps;
	}

	/* returns current through the right motor
		   if negative one is returned there was an
		   error when communicating with motor driver

		   value returned is 10 times the amperage
		   (2.5 Amps will return 25)
		*/
	public int getRightCurrent()
	{
		int amps = -1; // initialize to negative one;
		byte num = 33;

		outpkt[0] = (byte)0x55; // default for USB-12C controller
		outpkt[1] = (byte)0xB1; // address of I2C Bus
		outpkt[2] = (byte)12; // Register Number for right current
		outpkt[3] = (byte)1; // Number of data bytes to read

		try
		{
			out.write(outpkt);
			out.flush();
			while(in.available() == 0);
			amps = (int)in.read();
		}
		catch(IOException ioex)
		{
			System.out.println("Motors IOException Getting Voltage");
		}

		return amps;
	}

	public void setTurn(int num)
	{
		forward = 0;
		turn = num;
	}

	public void setForward(int num)
	{
		turn = 0;
		forward = num;
	}

} // end motor class

