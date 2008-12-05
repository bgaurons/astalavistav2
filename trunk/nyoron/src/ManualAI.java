
/**
 * <p>ManualAI.java - The manual "AI." It, in essence turns off AI, and allows
 * The server to control the motors.
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
public class ManualAI extends AI
{


	/**
	 * Starts the manual "AI."
	 *
	 * @author			Benjamin Gauronskas
	 */
	public ManualAI(){
		super(AI.MANUAL);

	}

	/**
	The logic for manual... is to do nothing.
	@author		Benjamin Gauronskas
	@return		False to end logic.
	*/
	public boolean logic(){
		stop();
		return false;
	}


	/**
	Does nothing
	@author		Benjamin Gauronskas
	*/
	public void init(){}


	/**
	Does nothing
	@author		Benjamin Gauronskas
	*/
	public void cleanup(){}

}
