

/**
 * <p>ThermMessage.java - This class sends a 32x8 array, presumably from the
 * Thermopile array to a console to display it in graphical form.</p>
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
public class ThermMessage extends Message
{

	/**
	A heat map presumably from the thermopile and presumably wanting to be sent
	to the server.
	*/
	public byte[][] map;


	/**
	 * Makes a message with a temperature map.
	 *
	 * @param	map	The value for a command
	 * @author			Benjamin Gauronskas
	 */
	public ThermMessage(byte[][] map){
		super(Message.THERM);

		this.map = map;

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
