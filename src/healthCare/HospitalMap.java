package healthCare;

import java.awt.Point;
import java.util.Hashtable;

public class HospitalMap {

	static final Hashtable<String, Point> map =  new Hashtable<String, Point>()
	{{
		put("Pok�j 101", new Point(0,0));
		put("Pok�j 102", new Point(0,1));
		put("Pok�j 103", new Point(0,2));
		put("Pok�j 104", new Point(0,3));
		put("Pok�j 105", new Point(0,4));
		put("Recepcja", new Point(1,6));
		put("Sala operacyjna", new Point(2,5));
		put("Pok�j ordynatora", new Point(10,15));
		put("Pok�j pielegniarek", new Point(10,16));
		put("Pok�j lekarzy", new Point(10,18));
		put("Palarnia", new Point(19,19));
	}};
	public static Point getCordsByRoom(String roomName)
	{
		return map.get(roomName);
	}
	
	public static String[] getRooms() {
		return new String[] {
				"Pok�j 101", 
				"Pok�j 102", 
				"Pok�j 103", 
				"Pok�j 104", 
				"Pok�j 105", 
				"Recepcja",
				"Sala operacyjna",
				"Pok�j ordynatora",
				"Pok�j pielegniarek",
				"Pok�j lekarzy", 
				"Palarnia"
				
		};
	}
	
}
