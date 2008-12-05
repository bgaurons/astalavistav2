import java.io.Serializable;

/**
 * <p>AIMessage.java - A message from the Server to a robot to tell the robot
 *to</p>
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
public class AIMessage extends Message
{

	/**
	The new type of AI to run from the list of AI constants in the AI class.
	*/
	public byte aiType;


	/**
	 * Makes an AI message with the new AI to run.
	 *
	 * @param	aiType	The new ai to run.
	 * @author			Benjamin Gauronskas
	 */
	public AIMessage(byte aiType){
		super(Message.AI);

		this.aiType = aiType;

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
