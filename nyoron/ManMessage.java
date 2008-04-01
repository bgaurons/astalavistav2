import java.io.Serializable;

/**
 * <p>ManMessage.java - A message from the server to the robot telling it which
 * way to move when it is in manual mode.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>April 1, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Written to replace MotMessage.</li>
 *		</ul>
 *		<li>March 29, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class ManMessage extends Message
{

	/**
	Tells what direction the robot will move. See the directional constants
	in MovementLogic.
	*/
	public byte direction;

	/**
	 * Makes a message with a control and a value.
	 * Makes the direction the default (NO_DIRECTION).
	 *
	 * @param	direction		The direction the robot must go.
	 * @author			Benjamin Gauronskas
	 */
	public ManMessage(byte direction){
		super(Message.MANUL);

		this.direction = direction;
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
