/**
 * 
 */
package touchSensor;

import java.util.EventObject;

/**
 * @author Cindy
 *
 */
public class TouchedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TouchedEvent(TouchSensorService touchSensor) {
		super(touchSensor);

	}

}
