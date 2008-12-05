import  sun.audio.*;    //import the sun.audio package
import  java.io.*;

/**

SoundSystem.java
Controls sound... logic.

	<h1>Revision History:</h1>
	<ul>
		<li>April 4, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>Created file.</li>

		</ul>
	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.1
*/
public class SoundSystem{


	/**
	The currently loaded sound file.
	*/
	protected static SoundFile soundFile;



	/**
	Plays the file given as a parameter
	@param	filename	The file to play
	@author		Benjamin Gauronskas
	*/
	public static void play(String filename){
		if(soundFile != null && soundFile.isPlaying)
			stopPlay();
		soundFile = new SoundFile(filename);

	}


	/**
	Stops playing the file.
	@author		Benjamin Gauronskas
	*/
	public static void stopPlay(){
		if(soundFile.isPlaying)
			soundFile.stopPlaying();

	}

	/**
	Represents a sound file that we would like to play.
	*/
	private static class SoundFile implements Runnable{
		protected boolean isPlaying;
		protected AudioStream stream;
		Thread t;

		/**
		Constructs a sound file object
		@param	filename	The file to play
		@author		Benjamin Gauronskas
		*/
		public SoundFile(String filename)
		{

			try{
				this.stream = new AudioStream(new FileInputStream(filename));
				t = new Thread(this,"Playing: " + filename);
				t.start();
			}
			catch(java.io.IOException ex){
				ex.printStackTrace();
			}
		}

		/**
		Plays the file, waits for it to end, and then stops.
		@author		Benjamin Gauronskas
		*/
		public void run(){
			isPlaying = true;
			AudioPlayer.player.start(stream);
		}


		/**
		Stops playing the currently playing sound file.
		@param	filename	The file to play
		@author		Benjamin Gauronskas
		*/
		protected void stopPlaying(){
			AudioPlayer.player.stop(stream);
			try{
				stream.close();
			}
			catch(java.io.IOException ex){}
			isPlaying = false;
		}
	}




}