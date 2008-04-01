

/**
 * <p>PosMessage.java - This message contains information on the robot's
 * location, speed, and angle.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>March 27, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class PosMessage extends Message
{

	/**
	The robot's current moving speed. Hopefully in some intelligent unit one
	day.
	*/
	public int speed;

	/**
	The robot's X coordinate in inches.
	*/
	public int x;

	/**
	The robot's Y coordinate in inches.
	*/
	public int y;


	/**
	The robot's angle in Radians. Read comment in BotPanel for information
	*/
	public double angle;


	/**
	 * Makes a message with Robot's current geographical and directional
	 * information.
	 *
	 * @param	speed	The robot's speed
	 * @param	x		The robot's x coordinate
	 * @param	y		The robot's y coordinate
	 * @param	angle	The robot's angle
	 * @author			Benjamin Gauronskas
	 */
	public PosMessage(int speed, int x, int y, double angle){
		super(Message.POSIT);

		this.speed = speed;
		this.x = x;
		this.y = y;
		this.angle = angle;

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
