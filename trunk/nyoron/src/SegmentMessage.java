import java.awt.Point;

/**
 * <p>SegmentMessage.java - This class is a message to be sent from presumably
 * a robot to the console including a segment to send.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>November 06, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Added Colors.</li>
 *		</ul>
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
public class SegmentMessage extends Message
{
	/**
	This is an XY coordinate.
	*/
	public Point start;

	/**
	This is an XY coordinate.
	*/
	public Point end;

	/**
	This is the map to draw on, see the class MAP for more information.
	*/
	public byte target;

	/**
	 * Makes the message with the point
	 *
	 * @param	start		segment start coordinate
	 * @param	end		segment end coordinate
		@param	target	the map to draw on, see Map.java for reference.
	 * @author			Benjamin Gauronskas
	 */
	public SegmentMessage(Point start, Point end, byte target){
		super(Message.SGMNT);

		this.start = start;
		this.end = end;
		this.target = target;
		
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
