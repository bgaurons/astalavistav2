/**
 * 	Simple wrapper class for accessing arduino sonar data
 */

/**
 * @author Scott Fisk
 *
 */
public class Sonar {

	/**
	the distance from center of robot
	*/
	public int dist_offset;
	
	/**
	the angle from center of robot
	*/
	public double angle_offset;
	
	/**
	which direction the sensor is facing in laymans
	*/
	private int orientation = 0;

	
	/**
	Constructor to initalize the determine which globals are relevant

	@param		orientation 	pass the orientation of the sensor you are accessing
	@author		Scott Fisk
    */
	public Sonar(int orientation) {
		switch(orientation) {
			// front
			case 0: 
				this.orientation = orientation;
				dist_offset = Arduino.sonarFrontDistance;
				angle_offset = Arduino.sonarFrontAngle;
				break;
			// left
			case 1:
				this.orientation = orientation;
				dist_offset = Arduino.sonarFrontDistance;
				angle_offset = Arduino.sonarFrontAngle;
				break;
			// right
			case 2:
				this.orientation = orientation;
				dist_offset = Arduino.sonarFrontDistance;
				angle_offset = Arduino.sonarFrontAngle;
				break;
			default:
				break;
		}
	}
	
	/**
	Returns the value of the sonar sensor

	@author		Scott Fisk
    */
	public float getMeasurement(){
		float returnValue = 0;
		
		switch(orientation) {
			// front
			case 0:
				returnValue = Registers.arduino.getFrontSonar();
				break;
			// left
			case 1:
				returnValue = Registers.arduino.getLeftSonar();
				break;
			// right
			case 2:
				returnValue = Registers.arduino.getLeftSonar();
				break;
			default:
				break;
		}
		
		return returnValue;
	}
	
}
