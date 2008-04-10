import java.awt.image.BufferedImage;

/**
 <p>MapMessage.java - This sends an updated map to the server.</p>


 <h1>Revision History:</h1>
 <ul>
 	<li>April 10, 2008, Benjamin Gauronskas</li>
 	<ul>
 		<li>Created file.</li>
 	</ul>
 </ul>


 @author			Benjamin Gauronskas
 @version			0.1
 */
public class MapMessage extends Message
{

	/**
	The map to send.
	*/
	public BufferedImage mapImage;


	/**
	 * Makes a message with the current image made by the robot.
	 *
	 * @param	mapImage	The value for a command
	 * @author			Benjamin Gauronskas
	 */
	public MapMessage(BufferedImage mapImage){
		super(Message.MAPIM);

		this.mapImage = mapImage;

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
