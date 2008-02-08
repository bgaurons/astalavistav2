/**
 * 
 */
package remoteControl;

import java.util.EventObject;
import java.util.*;

//Defines the ClientsChanged event triggered when the clients list maintained in server is changed (new client connected OR client disconnected)
public class ClientsChangedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	//The event contains a clone to the clients list
	public ClientsChangedEvent(Vector<RobotClient> clients) {
		super(clients);
	}
}
