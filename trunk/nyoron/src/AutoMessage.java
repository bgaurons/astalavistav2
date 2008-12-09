import java.io.Serializable;

/**
 * <p>AutoMessage.java - A message from the Server to a robot to tell the robot
 *to go automatic.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>March 22, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class AutoMessage extends Message
{

	/**
	Whether or not the robot will be automatic 
	*/
	public boolean auto;


	/**
	 * Makes an Auto message.
	 *
	 * @param	auto	if the robot will be auto.
	 * @author			Benjamin Gauronskas
	 */
	public AutoMessage(boolean auto){
		super(Message.AUTO);

		this.auto = auto;

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
