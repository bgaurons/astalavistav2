
/**
 * <p>RoamAI.java - Robot runs around like a dumbass.</p>
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
public class RoamAI extends AI
{

	//The amount of time it waits before asking for a password.
	private static int A_LONG_TIME = 20000;

	/**
	 * Starts the Thermal AI.
	 *
	 * @author			Benjamin Gauronskas
	 */
	public RoamAI(){
		super(AI.ROAMIN);

	}

	/**
	The logic for asking for passwords.
	@author		Benjamin Gauronskas
	@return		returns true, unless we need to stop the logic.
	*/
	public boolean logic(){
		try{
			Thread.sleep(A_LONG_TIME);
		}catch(InterruptedException ex){}


		return true;
	}


}
