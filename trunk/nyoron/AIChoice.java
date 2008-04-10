import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
<p>AIChoice.java - The combo box for choosing the robot's AI.</p>
	<h1>Revision History:</h1>
	<ul>
		<li>April 9, 2008, Benjamin Gauronskas</li>
		<ul>
			<li>First edition.</li>
		</ul>
	</ul>


  @author                      Benjamin Gauronskas
  @version                     0.1
 */
public class AIChoice extends JComboBox
implements ActionListener {



	/**
	The modes of AI to operate in. Add possibilities to this array when more
	options become available.
	*/
	static private String[] modes = {
									"Manual",
									"Heat Seaking"};
	/**
	Default and only constructor, takes no arguments and creates a JComboBox
	that fulfills our needs.
	@author		Benjamin Gauronskas
	*/
	public AIChoice() {
		super(modes);
		this.addActionListener(this);
		this.setSelectedIndex(0);
		this.setFocusable(false);

	}

	/**
	This is the listener method that listens for an event on the combo box.
	When an option is selected.
	@param	e	The event that triggered the listener.
	@author		Benjamin Gauronskas
	*/
	public void actionPerformed(ActionEvent e) {
		Message msg;

		JComboBox cb = (JComboBox)e.getSource();

		char choice = (((String)cb.getSelectedItem()).charAt(0));
		System.out.println("OH FUCK. SHIT JUST GOT REAL."+
							" I AM OUT OF HERE, DOG.");

		switch(choice){
			case 'M':
				System.out.println("Manual pressed");
				msg = new AIMessage(AI.MANUAL);
				Registers.connection.sendMessage(msg);
				break;
			case 'H':
				System.out.println("Heat seaking pressed");
				msg = new AIMessage(AI.THERML);
				Registers.connection.sendMessage(msg);
				break;
			default: break;
		}
	}


}