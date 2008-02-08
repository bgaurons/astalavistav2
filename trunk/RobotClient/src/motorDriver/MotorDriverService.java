package motorDriver;

import java.io.*;
import coreProcessor.*;
import java.util.*;
import commonData.CommonData;
import service.Service;

//Motor Driver Service
//Responsible for receiving/Processing and sending message to the server
public class MotorDriverService extends Service {

	public CoreProcessor coreProcessor;
	public MotorDriver motorDriver;
	//The service uses a Command List to run incoming commands one-by-one
	//This will prevent preforming new task before previous task finishes
	private Vector<Integer> commands;
	private boolean isWorking;

	public MotorDriverService(CoreProcessor coreProcessor) throws Exception {
		this.coreProcessor = coreProcessor;
		this.motorDriver = new MotorDriver("COM2");
		this.commands = new Vector<Integer>();
		this.isWorking = false;
	}

	public void StartWorking() {
		this.isWorking = true;
	}

	public void StopWorking() {
		this.isWorking = false;
	}

	//Public function used to add command to the list
	public void AddCommand(int value) {
		synchronized (this.commands) {
			this.commands.addElement(new Integer(value));
		}
	}

	public void run() {
		int value;
		this.StartWorking();

		while (this.isWorking) {
			synchronized (this.commands) {
				//Take one command from the list and perform it
				if (!this.commands.isEmpty()) {
					value = this.commands.firstElement().intValue();
					//After the task is finished, remove the command from list
					this.commands.removeElementAt(0);
				} else {
					continue;
				}
			}

			try {
				switch (value) {
				//MoveForward
				case CommonData.MotorCommand_MoveForward:
					this.motorDriver.stepForward();
					break;
				//MoveBackward
				case CommonData.MotorCommand_MoveBackward:
					this.motorDriver.stepBackward();
					break;
				//MoveLeft
				case CommonData.MotorCommand_MoveLeft:
					this.motorDriver.turnLeft();
					break;
				//MoveRight
				case CommonData.MotorCommand_MoveRight:
					this.motorDriver.turnRight();
					break;
				//StopMove
				case CommonData.MotorCommand_MoveStop:
					this.motorDriver.leftMotor(0);
					this.motorDriver.rightMotor(0);
					break;

				default:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Stop() {

	}
}
