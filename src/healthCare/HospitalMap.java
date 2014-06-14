package healthCare;

import java.awt.Point;
import java.util.Hashtable;

public class HospitalMap {

	static final Hashtable<String, Point> map =  new Hashtable<String, Point>()
	{{
		put("Pokój 101", new Point(0,0));
		put("Pokój 102", new Point(0,1));
		put("Pokój 103", new Point(0,2));
		put("Pokój 104", new Point(0,3));
		put("Pokój 105", new Point(0,4));
		put("Recepcja", new Point(1,6));
		put("Sala operacyjna", new Point(2,5));
		put("Pokój ordynatora", new Point(10,15));
		put("Pokój pielegniarek", new Point(10,16));
		put("Pokój lekarzy", new Point(10,18));
		put("Palarnia", new Point(19,19));
	}};
	public static Point getCordsByRoom(String roomName)
	{
		return map.get(roomName);
	}
	
	public static String[] getRooms() {
		return new String[] {
				"Pokój 101", 
				"Pokój 102", 
				"Pokój 103", 
				"Pokój 104", 
				"Pokój 105", 
				"Recepcja",
				"Sala operacyjna",
				"Pokój ordynatora",
				"Pokój pielegniarek",
				"Pokój lekarzy", 
				"Palarnia"
				
		};
	}
	
}
