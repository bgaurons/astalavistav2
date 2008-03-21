import java.io.Serializable;

/**
 * <p>MotorMessage.java - This class is a message to be sent from presumably
 * a networked console to a robot's motor.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>March 19, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class MotMessage extends Message
{
	//These are the currently implemented features in the message sending.
	/**
	Command to stop the bot.
	*/
	public static final byte CTRL_STOP = 0x01;
	/**
	Command to send a command to the motor command register.
	*/
	public static final byte CTRL_CMD = 0x02;
	/**
	Command to move forward.
	*/
	public static final byte CTRL_FWD = 0x03;
	/**
	Command to turn.
	*/
	public static final byte CTRL_TRN = 0x04;
	/**
	Command to accelerate
	*/
	public static final byte CTRL_ACL = 0x05;

	/**
	For certain commands, the amount to which they should be executed,
	or the command to pass to a register.
	*/
	public byte value;

	/**
	One of the CTRL constants to tell the motor what to do.
	*/
	public byte control;

	/**
	 * Makes a message with a control and a value.
	 *
	 * @param	value	The value for a command
	 * @param	control	The control command.
	 * @author			Benjamin Gauronskas
	 */
	public MotMessage(byte value, byte control){
		super(Message.MOTOR);

		this.value = value;
		this.control = control;
	}



	/**
	 * Creates a string representation of the message.
	 *
	 * @return			a string representation of the object.
	 * @author			Benjamin Gauronskas
	 */
	public String toString(){
		return super.toString();
	}

}
