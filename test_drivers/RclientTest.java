/** 
 * <p>RclientTest.java - Test driver for Rclient.</p>
 *
 *
 * 	<h1>Revision History:</h1>
 *	<ul>
 *		<li>January 27, 2008, Benjamin Gauronskas</li>
 *		<ul>
 *			<li>Created file.</li>
 *		</ul>
 *	</ul>
 *
 *	<p>Sources for ideas come from tutorials and help at:
 *	<ul>
 *		<li>http://www.ashishmyles.com/tcpchat/index.html</li>
 *		<li>
 *		http://www.csc.villanova.edu/~mdamian/Sockets/JavaSocketsNoThread.htm
 *		</li>
 *		<li>
 *	http://java.sun.com/developer/technicalArticles/Programming/serialization/
 *		</li>
 *	</ul></p>
 *
 * @author			Benjamin Gauronskas
 * @version			0.1 
 */
public class RclientTest
{

	/**
	 * The main thread for the test driver.
	 *
	 * @param	args	commandline arguments
	 * @author			Benjamin Gauronskas
	 */
	public static void main(String args[]){
		try{
			System.out.println("!@#$\tInitializing.");
			Rclient.initialize();
			System.out.println("!@#$\tSending a message.");
			Rclient.sendMessage(new Message());
			Thread.sleep(4000);
			Rclient.sendMessage(new Message());
			System.out.println("!@#$\tTest Driver done.");
		}
		catch(InterruptedException e){
			System.err.println("!@#$\tSleeping interrupted!");
		}
	}
}
