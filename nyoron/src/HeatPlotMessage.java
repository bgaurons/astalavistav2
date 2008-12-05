import java.awt.Point;

/**
 * <p>HeatPlotMessage.java - This class is a message to be sent from presumably
 * a robot to the console including a heat point to send.</p>
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
public class HeatPlotMessage extends Message
{
	/**
	This is an XY coordinate.
	*/
	public Point location;



	/**
	This is the temperature
	*/
	public byte temperature;

	/**
	 * Makes the message with the point
	 *
	 * @param	location		Location temperature reading was made
	 * @param	temperature		temperature reading
	 * @author			Benjamin Gauronskas
	 */
	public HeatPlotMessage(Point location, byte temperature){
		super(Message.HTPLT);

		this.location = location;
		this.temperature = temperature;
		
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
