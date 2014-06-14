package healthCare;

import java.awt.Point;

import jade.core.Agent;

public class ControllerAgent extends Agent {
	
	
	public static Point getRoomCords(String roomName)
	{
		return HospitalMap.getCordsByRoom(roomName);
	}

	

}
