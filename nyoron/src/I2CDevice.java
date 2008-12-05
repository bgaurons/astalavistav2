/**
<p>I2CDevice.java - A skeleton I2C driver..</p>
	<h1>Revision History:</h1>
	<ul>
		<li>March 14, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Rewrote command to send bool on success.</li>
			<li>Fixed problem with read method.</li>
			<li>Overloaded command to accept a sleep parameter.</li>
		</ul>
		<li>March 13, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Added method for reading bytes.</li>
			<li>Wrote; did not even test.</li>
			<li>Created comments.</li>
		</ul>

	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.1
 */

public class I2CDevice
{
	/**
	The I2c address of the device.
	*/
	public byte address;
	/**
	The I2c channel to use.
	*/
	public I2CChannel channel;

        /**
	Constructor. It constructs an I2CDevice object using an already
	built I2CChannel and the I2C address of the device
	@param		channel		The i2cchannel to use.
	@param		address		The address of the device on the i2c
					channel.
	@author		Benjamin Gauronskas
         */
	public I2CDevice(I2CChannel channel, byte address)
	{
		this.channel = channel;
		this.address = address;
	}

        /**
	sends a command to a device. It will basically run the command on the
	I2CChannel member variable. Refer to its man page for more details.
	If we want this multi-threaded, this is probably the place to do it.
	This is an overloaded default no sleeping method.
	@param		register	The register receiving the command.
	@param		cmd		The command that will be executed.
	@return				Whether the command completed successfully.
	@author		Benjamin Gauronskas
         */
	public boolean command(byte register, byte cmd)
	{
		int sleep = 0;
		return channel.i2cCommand(address, register, cmd, sleep);
	}

        /**
	sends a command to a device. It will basically run the command on the
	I2CChannel member variable. Refer to its man page for more details.
	If we want this multi-threaded, this is probably the place to do it.
	This is an overloaded default no sleeping method.
	@param		register	The register receiving the command.
	@param		cmd		The command that will be executed.
	@param		sleep 	how long to sleep after completing the command.
	@return				Whether the command completed successfully.
	@author		Benjamin Gauronskas
         */
	public boolean command(byte register, byte cmd, int sleep)
	{
		return channel.i2cCommand(address, register, cmd, sleep);
	}

        /**
	Reads a long from a device. It simply asks for a long
	@param		register	The register receiving the command.
	@return		The long that was taken off the inbound buffer.
	@author		Benjamin Gauronskas
         */
	public long readLong(byte register)
	{
		return channel.i2cReadLong(address, register);
	}

        /**
	Reads a byte from a device. It simply asks for a byte
	@param		register	The register receiving the command.
	@return		The long that was taken off the inbound buffer.
	@author		Benjamin Gauronskas
         */
	public byte readByte(byte register)
	{
		return channel.i2cReadByte(address, register);
	}




} // End of I2CDevice

