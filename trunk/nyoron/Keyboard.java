import java.io.* ;

/**
Allows us to read input from the keyboard. Makes code for reading strings from
Standard-in not look like dicks.

	<h1>Revision History:</h1>
	<ul>
		<li>April 09, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Created file.</li>
		</ul>

	</ul>

@version 0.1
@author Benjamin Gauronskas
*/
public class Keyboard extends BufferedReader
{

	/**
	Constructor for the keyboard object.

	@author		Benjamin Gauronskas
	*/
	public Keyboard(){
		super((new InputStreamReader(System.in)));
	}

	/**
	Reads a line from standard input

	@return		Whatever string was typed in.
	@author		Benjamin Gauronskas
	*/
	public String readLine(){
		String returnValue = null;
		try{
			returnValue = super.readLine();
		}
		catch(IOException e){}

		return returnValue;
	}

}