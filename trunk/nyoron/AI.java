import java.util.*;
import java.io.*;

/**
 * <p>AI.java - This class is the root class of all sets of AI. This, I figured
 * would make multi-threaded artificial intelligence easier to program for
 * people that have not learned how to use Java's multi-threaded API.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>April 1, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Added skeleton heat seaking AI.</li>
 *		</ul>
 *		<li>March 23, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Fixed mistake in shutdown logic.</li>
 *		</ul>
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
public abstract class AI implements Runnable
{
	/**
	The console controls the robot. The robot does not use any intelligence.
	*/
	public static final byte MANUAL	 = 0x00;

	/**
	The robot looks for heat.
	*/
	public static final byte THERML = 0x01;

	/**
	The robot looks for heat.
	*/
	public static final byte PASSWO = 0x02;

	/**
	The robot does nothing particularly useful.
	*/
	public static final byte ROAMIN = 0x03;

	/**
	The thread created and ran when an AI is started.
	*/
	public Thread aiThread;

	/**
	The boolean that is turned off to kill the thread.
	*/
	public boolean isRunning;

	/**
	The type of AI that is being run. as Defined by the constants in this file.
	*/
	public byte aiType;


	/**
	Constructor. Should be called by all classes extending AI.
	@param		aiType		The type of AI to run. Should correspond to a
							constant in this file.
	@author		Benjamin Gauronskas
	*/
	public AI(byte aiType)
	{
		this.isRunning = true;

		this.aiType = aiType;
		this.aiThread = new Thread(this,"AI thread");
		this.aiThread.start();
	}

	/**
	Logic that runs when a thread is created from this object.
	@author		Benjamin Gauronskas
	*/
	public void run(){
		boolean keepRunning;
		init();
		do{
			keepRunning = logic();
			System.out.println(
				"!@#$\tKeepRunning = " + keepRunning + "\n" +
				"!@#$\tIsRunning = " + isRunning);

		}while(keepRunning && isRunning);
		cleanup();
	}

	/**
	A method that runs once on the robot, and then is followed by the loop.
	@author		Benjamin Gauronskas
	*/
	protected abstract void init();

	/**
	This is where logic is actually put that will be run the AI.
	@author		Benjamin Gauronskas
	@return		Whether the thread should continue or not.
	*/
	protected abstract boolean logic();

	/**
	A method that does whatever finishing routines need to be made.
	@author		Benjamin Gauronskas
	*/
	protected abstract void cleanup();

	/**
	Logic that stops the thread from running. Obviously should be called when
	switching to a new thread.
	@author		Benjamin Gauronskas
	*/
	public void stop(){
		isRunning = false;
		//Busy wait to wait for the thread to end.
		while(aiThread.isAlive());

	}

}
