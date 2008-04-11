
/**
 * <p>ThermalAI.java - This aritifical intelligence mode hopes to find an
 * unusual source of heat and follow it</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>March 24, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *
 * @author			Benjamin Gauronskas
 * @version			0.1
 */
public class ThermlAI extends AI
{


	/**
	 * Starts the Thermal AI.
	 *
	 * @author			Benjamin Gauronskas
	 */
	public ThermlAI(){
		super(AI.THERML);

	}

	/**
	The logic for heat sinking.
	@author		Benjamin Gauronskas
	@return		returns true, unless we need to stop the logic.
	*/
	public boolean logic(){
		boolean returnVal = true;

		//If the threshhold has been crossed, do something.
		if(Registers.thermopile.thresholdCrossed()){
			MovementLogic.stopRoaming();
			int angleColumn = Registers.thermopile.getHotColumn();
			MovementLogic.turn	(
				Math.PI *
				((angleColumn-Thermopile.HOR_MIDDLE)/Thermopile.HOR_WIDTH)
								);
		}
		return returnVal;
	}

	/**
	Starts motors up.
	@author		Benjamin Gauronskas
	*/
	public void init(){
		MovementLogic.forward();
	}


	/**
	Stops motors.
	@author		Benjamin Gauronskas
	*/
	public void cleanup(){
		MovementLogic.stopRoaming();
	}

}
