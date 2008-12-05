import java.io.*;
import gnu.io.*;
import java.util.*;

import java.util.concurrent.locks.*;

/**
<p>I2CChannel.java - This channel allows access to an I2c channel using
FTDI USB-I2c converter.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>March 14, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Fixed the read method.</li>
			<li>command returns a boolean reporting success.</li>
			<li>Command now takes a sleep parameter to make threads wait a
				certain amount of time after a command is issued.</li>
		</ul>
		<li>March 13, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Compiled.</li>
			<li>I had to remove a parenthesis.</li>
			<li>Added method to read bytes.</li>
		</ul>
		<li>March 12, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Wrote; did not even test.</li>
			<li>Created comments.</li>
		</ul>
		<li>???, ???</li>
		<ul>
			<li>Someone made this, right?</li>
		</ul>
	</ul>


  @author                      ???
  @version                     0.2
 */
public class I2CChannel
{



	/**
	The com port that represents the I2c channel.
	It is not used much except to find the outputstream and input stream.
	It might be removable as a class variable at a later time unless it is
	needed for cleanup
	*/
	public SerialPort channel;
	/**
	The output buffer to the I2c channel.
	This is where commands go to the outside world.
	*/
	public byte [] outpkt;
	/**
	The input buffer from the I2c channel.
	This is where information read from the input stream goes before
	being converted to some useful form
	*/
	public byte [] inpkt;
	/**
	The I2c channel output stream.
	*/
	public OutputStream out;
	/**
	The I2c channel input stream.
	*/
	public InputStream in;
	/**
	The I2c mutual exclusivity lock for the i2c channel.
	Don't play with this. It is meant to keep more than one thread from
	writing to the I2C channel at once.
	*/
	private ReentrantLock channelLock;


	//Constants
	private static final int OUT_BUFFER_SIZE = 5;
	private static final int IN_BUFFER_SIZE = 4;

	//This is the address of the computer. In communication, it should be
	//the first piece of a command.
	private static final byte MASTER_ADDRESS = 0x55;


        /**
	Constructor. It constructs an I2CChannel object from the comport
	given as a parameter.
	@param		comPort		The comPort the usb-i2c converter
					uses.
	@author		???
         */
	public I2CChannel(String comPort) throws Exception
	{
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier channelId =
			(CommPortIdentifier) portList.nextElement();
		outpkt = new byte [OUT_BUFFER_SIZE];
		inpkt = new byte[IN_BUFFER_SIZE];

		//This chunk of voodoo is going to find the correct channel
		//Passed as a string to the constructor. We go through this
		//loop until we either run out of ports to check, or we find
		//the correct port type with the correct name.
		while (portList.hasMoreElements() &&
		!(channelId.getPortType() == CommPortIdentifier.PORT_SERIAL &&
		channelId.getName().equals(comPort))) {
			channelId =
				(CommPortIdentifier) portList.nextElement();
		}
		//At this point the channel should be identified.

		//The constructor takes a timeout and a name.
		channel = (SerialPort) channelId.open(comPort, 5);
		channel.setSerialPortParams(
			19200,
			SerialPort.DATABITS_8,
			SerialPort.STOPBITS_2,
			SerialPort.PARITY_NONE);

		channel.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

		out = channel.getOutputStream();
		in = channel.getInputStream();
		channel.setOutputBufferSize(20);

		//Finally the lock for synchronization. I think locks are
		//easier to read then synchronize blocks.
		channelLock = new ReentrantLock();

	}

        /**
	sends a command to a device. It does not expect to receive a return
	value, so it can be easily ran in a seperate thread to reduce blocking.
	This might need to be changed to issue multi-byte commands in the
	future, but that will not be hard.
	@param		address		The address of the I2C device to
					command.
	@param		register	The register receiving the command.
	@param		cmd		The command that will be executed.
	@param		sleep	How long to sleep afterwards.
	@return			Whether the command sent correctly or not.
	@author		Benjamin Gauronskas
         */
	public boolean i2cCommand(byte address, byte register, byte cmd, int sleep)
	{

		//For now, the command length is a constant. Can easily be
		//changed, or overloaded.
		byte COMMAND_LENGTH = 0x01;
		boolean returnVal = false;


		channelLock.lock();

		outpkt[0] = MASTER_ADDRESS;
		outpkt[1] = address; // address of I2C Bus
		outpkt[2] = register; // Register number
		outpkt[3] = COMMAND_LENGTH; // Number of data bytes to follow
		outpkt[4] = cmd;  // Value

		try
		{
			out.write(outpkt);
			out.flush();
			//A thread sleep might be appropriate here.
			//I am not sure.
			//A one or zero will be written for success or failure...
			//might want to make this function bool instead of void.
			in.read(inpkt);
			returnVal = (inpkt[0] == 1);

			if(sleep > 0)
				Thread.sleep(sleep);
		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}
		finally{
			channelLock.unlock();
		}

		return returnVal;
	}

        /**
	Reads a long from a device. It simply asks for a long.
	@param		address		The address of the I2C device to
					command.
	@param		register	The register receiving the command.
	@return		The long that was taken off the inbound buffer.
	@author		Benjamin Gauronskas
         */
	public long i2cReadLong(byte address, byte register)
	{

		long returnVal = -1; // initialize to negative one;
		byte BYTES_IN_INTEGER = 0x04;

		channelLock.lock();

		outpkt[0] = MASTER_ADDRESS; // default for USB-12C controller
		outpkt[1] = (byte)(address+1); // address of I2C Bus
		outpkt[2] = register; // Register Number
		outpkt[3] = BYTES_IN_INTEGER; // Number of data bytes to read


		try
		{
			out.write(outpkt);
			out.flush();

			Thread.sleep(1000);

			//I think a wait needs to go here but...

			in.read(inpkt);
			//Ok... this is bad... I can't test if this is correct
			//or not. Assuming that the
			returnVal = 0;
			returnVal |= inpkt[0] & 0xFF;
			returnVal <<= 8;
			returnVal |= inpkt[1] & 0xFF;
			returnVal <<= 8;
			returnVal |= inpkt[2] & 0xFF;
			returnVal <<= 8;
			returnVal |= inpkt[3] & 0xFF;


		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}
		finally{
			channelLock.unlock();
		}

		return returnVal;
	}

        /**
	Reads a byte from a device. It simply asks for a byte.
	@param		address		The address of the I2C device to
					command.
	@param		register	The register receiving the command.
	@return		The byte that was taken off the inbound buffer.
	@author		Benjamin Gauronskas
         */
	public byte i2cReadByte(byte address, byte register)
	{

		byte returnVal = (byte)0xFF; // initialize to negative one;
		byte BYTES_TO_READ = 0x01;

		channelLock.lock();

		outpkt[0] = MASTER_ADDRESS; // default for USB-12C controller
		outpkt[1] = (byte)(address+1); // address of I2C Bus
		outpkt[2] = register; // Register Number
		outpkt[3] = BYTES_TO_READ; // Number of data bytes to read


		try
		{
			out.write(outpkt);
			out.flush();

			//Thread.sleep(1000);




			//I think a wait needs to go here but...

			in.read(inpkt);
			//Ok... this is bad... I can't test if this is correct
			//or not. Assuming that the
			returnVal = inpkt[0];



		}
		catch(IOException ioex)
		{
			System.out.println("IOException");
		}
		//catch(InterruptedException ex){
		//		ex.printStackTrace();
		//}
		finally{
			channelLock.unlock();
		}

		return returnVal;
	}


}

