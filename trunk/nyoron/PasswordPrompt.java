
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
public class PasswordPrompt
{

	private static char[] password = {'2', '4', '6', '8'};
	private static Keyboard keyboard = new Keyboard();


	/**
	Reads a line from standard input

	@return		Whether a correctly formatted password
				matched the password in the logic
	@author		Benjamin Gauronskas
	*/
	public static boolean passwordPrompt(){
		String input;// = new char[password.length];
		boolean stupidInput;
		boolean returnValue = false;

		SoundSystem.play("enterPW.au");
		do{


			stupidInput = false;

			try{
				Thread.sleep(9000);
			}catch(InterruptedException e){}

			//for(int i = 0; i < input.length; i++){
			//	input[i] = ((char)keyboard.read());
			//}
			input = keyboard.readLine();
			if(input.length() < password.length){
				stupidInput = true;
			}



			for(int i = 0; i < input.length(); i++){
				if(input.charAt(i) < '0' || input.charAt(i) > '9'){
					stupidInput = true;
				}
			}

			if(stupidInput){
				SoundSystem.play("retry.au");
			}

		}while(stupidInput);


		SoundSystem.play("confirm.au");

		try{
			Thread.sleep(4000);
		}catch(InterruptedException e){}

		for(int i = 0; i < input.length(); i++){
			readNumber(input.charAt(i));
		}

		String passString = new String(password);

		System.out.println(passString);
		System.out.println(input);

		if(input.equals(passString)){
			System.out.println("Success");
			returnValue = true;
		}
		else{
			System.out.println("Failure");
			returnValue = false;
		}


		return returnValue;

	}


	/**
	Says the number that has been input

	@param	number	character to say out loud.
	@author		Benjamin Gauronskas
	*/
	public static void readNumber(char number){
		String filename;
		switch(number){
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				filename = number + ".au";
				SoundSystem.play(filename);
				try{
					Thread.sleep(2000);
				}catch(InterruptedException e){}
			break;

			default: break;
		}



	}

	/**
	Prompts the user for input. Alarm goes off if they do not enter the
	correct password in three tries.


	@author		Benjamin Gauronskas
	*/
	public static void promptUser(){
		char count = '3';
		boolean passwordCorrect = false;
		while(!passwordCorrect && count != '1')
		{
			passwordCorrect = passwordPrompt();
			count -= 1;
		}
		if(passwordCorrect){
			SoundSystem.play("acknowledge.au");
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}

		}
		else{
			SoundSystem.play("alert.au");
			try{
				Thread.sleep(15000);
			}catch(InterruptedException e){}
		}


	}

}