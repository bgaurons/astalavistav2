import java.awt.Color;
import java.awt.Point;

/**
 * <p>PointMessage.java - This class is a message to be sent from presumably
 * a robot to the console including a point to send.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>

 *		<li>November 03, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class PointMessage extends Message
{
	/**
	This is an XY coordinate.
	*/
	public Point point;
	public Color color;

	/**
	 * Makes the message with the point
	 *
	 * @param	point		The xy coordinate.
	 * @author			Benjamin Gauronskas
	 */
	public PointMessage(Point point){
		super(Message.POINT);

		this.point = point;
		
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
