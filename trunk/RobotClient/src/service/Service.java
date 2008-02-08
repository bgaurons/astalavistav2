package service;

//Defines common interface for a Service
public abstract class Service implements Runnable {

	public String DeviceName;
	public byte DeviceID;
	
	public abstract void run();
	public abstract void Stop();
	
}
