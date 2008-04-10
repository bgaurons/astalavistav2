import java.util.*;
import java.io.*;

/**
 * <p>Message.java - This class is a data structure for sending messages over
 *	TCP. It is meant to be extended to be more useful. That is, this class in
 *	its raw form is nearly useless except for organizational purposes. I hope
 *	to add some constants to define message_type.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>April 1, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Added type constants.</li>
 *		</ul>
 *		<li>January 27, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class Message implements Serializable
{
	public static final byte DEBUG	 = 0x00;
	public static final byte MOTOR	 = 0x01;
	public static final byte THERM	 = 0x02;
	public static final byte AI		 = 0x03;
	public static final byte POSIT	 = 0x04;
	public static final byte MANUL	 = 0x05;
	public static final byte MAPIM	 = 0x06;

	private Date timestamp;
	private byte message_type;

	/**
	 * Probably useless, but here is a default constructor
	 *
	 * @author			Benjamin Gauronskas
	 */
	public Message(){
		timestamp = new Date();
		message_type = DEBUG;
	}

	/**
	 * A constructor that uses a resultSet object. Assumes that the ResultSet is
	 * on the correct row.
	 *
	 * @param		message_type	The type of message that is going to be
	 *								sent. Should comply with a future coded
	 *								value list.
	 *
	 * @author						Benjamin Gauronskas
	 */
	public Message(byte message_type){
		timestamp = new Date();
		this.message_type = message_type;
	}

	/**
	 * Creates a string representation of the message.
	 *
	 * @return			a string representation of the object.
	 * @author			Benjamin Gauronskas
	 */
	public byte getType(){
		return message_type;
	}


	/**
	 * Creates a string representation of the message.
	 *
	 * @return			a string representation of the object.
	 * @author			Benjamin Gauronskas
	 */
	public String toString(){
		return "!@#$\tMessage Timestamp:\t" + timestamp +
				"\tMessage Type:\t" + message_type;
	}

}
