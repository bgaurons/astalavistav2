package remoteControl;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import commonData.CommonData;

//Graphic User Interface for Remote Control Service
public class RemoteControlGUI extends JFrame {
	private RemoteControlService remoteControlService;

	//Stores the current activated client (responsible for information update and receiving manual commands)
	private RobotClient client;

	private Container container = getContentPane();

	private JButton moveForwardButton;
	private JButton moveBackwardButton;
	private JButton moveLeftButton;
	private JButton moveRightButton;
	private JButton moveStopButton;

	private JButton exitButton;

	private JLabel touchSensorStatus;
	private JLabel sonarSensorStatus;
	private JLabel motorStatus;

	//Robot Clients List
	private JComboBox robotList;

	private JPanel robotListPanel = new JPanel();
	private JPanel infoPanel = new JPanel();
	private JPanel motorControlPanel = new JPanel();
	private JPanel systemControlPanel = new JPanel();

	//ClientsChanged event handler
	public ClientsChangedEventHandler ClientsChangedEventHandler;

	public RemoteControlGUI(RemoteControlService remoteControlService) {
		super("Remote Control Center");

		this.remoteControlService = remoteControlService;
		this.client = null;

		this.robotList = new JComboBox();
		this.robotList.addActionListener(new RobotListHandler());

		ButtonHandler buttonHandler = new ButtonHandler();

		this.moveBackwardButton = new JButton("Back");
		this.moveBackwardButton.addActionListener(buttonHandler);
		this.moveForwardButton = new JButton("Forward");
		this.moveForwardButton.addActionListener(buttonHandler);
		this.moveLeftButton = new JButton("Left");
		this.moveLeftButton.addActionListener(buttonHandler);
		this.moveRightButton = new JButton("Right");
		this.moveRightButton.addActionListener(buttonHandler);
		this.moveStopButton = new JButton("STOP");
		this.moveStopButton.addActionListener(buttonHandler);

		this.exitButton = new JButton("Exit");
		this.exitButton.addActionListener(buttonHandler);

		this.touchSensorStatus = new JLabel("TouchSensor Status: ");
		this.sonarSensorStatus = new JLabel("SonarSensor Status: ");
		this.motorStatus = new JLabel("Motor Status: ");

		this.infoPanel.add(this.touchSensorStatus);
		this.infoPanel.add(this.sonarSensorStatus);
		this.infoPanel.add(this.motorStatus);
		
		this.robotListPanel.add(this.robotList);

		this.motorControlPanel.add(this.moveLeftButton);
		this.motorControlPanel.add(this.moveForwardButton);
		this.motorControlPanel.add(this.moveBackwardButton);
		this.motorControlPanel.add(this.moveRightButton);
		this.motorControlPanel.add(this.moveStopButton);

		this.systemControlPanel.add(this.exitButton);

		this.container.setLayout(new GridLayout(6, 1));
		container.add(this.robotListPanel);
		container.add(this.touchSensorStatus);
		container.add(this.sonarSensorStatus);
		container.add(this.motorStatus);
		container.add(this.motorControlPanel);
		container.add(this.systemControlPanel, BorderLayout.SOUTH);

		//Implements ClientsChanged event handler
		//When the clients list maintained in RemoteControlService is changed, the service will invoke this handler to update the Robot Clients List
		this.ClientsChangedEventHandler = new ClientsChangedEventHandler() {
			public void OnClientsChanged(ClientsChangedEvent e) {
				Vector<RobotClient> clients = (Vector<RobotClient>) e.getSource();
				synchronized (clients) {
					robotList.removeAllItems();
					for (int i = 0; i < clients.size(); i++) {
						robotList.addItem(((RobotClient) clients.elementAt(i)).GetID() + "");
					}

					//If there is only one Robot Client connected, select it. Otherwise, keep the selection (do nothing).
					if (robotList.getItemCount() == 1)
					{
						robotList.setSelectedIndex(0);
						// RobotListHandler should be invoked automatically...
					}
				}
			}
		};
	}

	//Called when the Robot Clients ComboBox's selection is changed. 
	//The corresponding Robot Client will be notified to start sending information to the RemoteControlService.
	private void updateNewClient(RobotClient newClient) {
		this.client.StopWorking();
		this.client = newClient;
		newClient.StartWorking();
		//Send some information requests to the client here...
	}

	//ActionListener for the Robot Clients List ComboBox
	//When selection changed, update the GUI by new client's information
	private class RobotListHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			if (robotList.getItemCount() == 0)
				return;
			
			RobotClient oldClient = client;

			int id = Integer.parseInt((String) robotList.getSelectedItem());

			synchronized (remoteControlService.clients) {
				for (int i = 0; i < remoteControlService.clients.size(); i++) {
					if (((RobotClient) remoteControlService.clients
							.elementAt(i)).GetID() == id) {
						client = (RobotClient) remoteControlService.clients
								.elementAt(i);
						break;
					}
				}
			}

			if (client != oldClient) {
				updateNewClient(client);
			}
		}
	}

	//ActionListener for all buttons
	private class ButtonHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			//Exit
			if (e.getSource() == exitButton) {
				System.exit(0);
			}

			//If no client connected, return
			if (client == null) {
				return;
				
			//Send corresponding message to the client when button clicked
				
			//MoveForward
			} else if (e.getSource() == moveForwardButton) {
				client.Send(CommonData.MotorCommand,
						CommonData.MotorCommand_MoveForward);
				
			//MoveBackward
			} else if (e.getSource() == moveBackwardButton) {
				client.Send(CommonData.MotorCommand,
						CommonData.MotorCommand_MoveBackward);
				
			//MoveLeft
			} else if (e.getSource() == moveLeftButton) {
				client.Send(CommonData.MotorCommand,
						CommonData.MotorCommand_MoveLeft);
				
			//MoveRight
			} else if (e.getSource() == moveRightButton) {
				client.Send(CommonData.MotorCommand,
						CommonData.MotorCommand_MoveRight);
				
			//StopMove
			} else if (e.getSource() == moveStopButton) {
				client.Send(CommonData.MotorCommand,
						CommonData.MotorCommand_MoveStop);
			}
		}
	}
}
