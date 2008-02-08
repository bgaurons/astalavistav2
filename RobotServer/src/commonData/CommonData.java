package commonData;

//CommonData stores common constant values used by the program
public class CommonData {
	//All the ports number for commnunication
	public static final int InfoUpdateThreadPort = 30012;
	public static final int MotorControlServerPort = 30013;
	public static final int VideoReceiveThreadPort = 30014;
	public static final int RemoteControlServerPort = 9999;
	
	//Separator string for connecting Function and Value
	public static String StringDataSeparator = "<<>>";
	
	//Function and Values defined for communication policy
	public static final int ConnectionTerminated = 1000;
	public static final int NotifyNewID = 1001;
	public static final int MotorCommand = 1002;
	public static final int NotifyPasswordFailed = 1003;
	public static final int NotifyPasswordPassed = 1004;
	public static final int MotorCommand_MoveForward = 1005;
	public static final int MotorCommand_MoveBackward = 1006;
	public static final int MotorCommand_MoveLeft = 1007;
	public static final int MotorCommand_MoveRight = 1008;
	public static final int MotorCommand_MoveStop = 1009;
	public static final int UpdateTouchSensorInformation = 1010;
	public static final int UpdateSonarSensorInformation = 1011;
	public static final int UpdateMotorInformation = 1012;
	public static final int UpdateLocationInformation = 1013;
	public static final int NotifyObjectDetected = 1014;
	public static final int NotifyLowBattery = 1015;
	public static final int InputtedPassword = 1016;
}
