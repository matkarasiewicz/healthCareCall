package healthCare;

import java.awt.Point;
import java.util.Hashtable;

public class HospitalMap {

    static final Hashtable<String, Point> map = new Hashtable<String, Point>() {
        {
            put("Pokoj 101", new Point(0, 0));
            put("Pokoj 102", new Point(0, 1));
            put("Pokoj 103", new Point(0, 2));
            put("Pokoj 104", new Point(0, 3));
            put("Pokoj 105", new Point(0, 4));
            put("Recepcja", new Point(1, 6));
            put("Sala operacyjna", new Point(2, 5));
            put("Pokoj ordynatora", new Point(10, 15));
            put("Pokoj pielegniarek", new Point(10, 16));
            put("Pokoj lekarzy", new Point(10, 18));
            put("Palarnia", new Point(19, 19));
        }
    };

    public static Point getCordsByRoom(String roomName) {
        return map.get(roomName);
    }

    public static double getDistanceBetweenRooms(String roomOne, String roomTwo) {
        Point roomOneCords = HospitalMap.getCordsByRoom(roomOne);
        Point roomTwoCords = HospitalMap.getCordsByRoom(roomTwo);

        return roomOneCords.distance(roomTwoCords);
    }

    public static String[] getRooms() {
        return new String[]{
            "Pokoj 101",
            "Pokoj 102",
            "Pokoj 103",
            "Pokoj 104",
            "Pokoj 105",
            "Recepcja",
            "Sala operacyjna",
            "Pokoj ordynatora",
            "Pokoj pielegniarek",
            "Pokoj lekarzy",
            "Palarnia"
        };
    }
}
