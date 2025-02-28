import org.w3c.dom.Document;
import java.util.Map;

public class BoardXMLTest {
    public static void main(String[] args) {
        try {
            System.out.println("=== Testing BoardXMLParser ===");

            // Step 1: Parse the XML file
            ParseXML parser = new ParseXML();
            Document doc = parser.getDocFromFile("board.xml"); // Make sure board.xml is in the right location

            // Step 2: Initialize the BoardXMLParser
            BoardXMLParser boardParser = new BoardXMLParser(doc);
            Map<String, Room> rooms = boardParser.parseRooms();

            // Step 3: Display all parsed rooms
            System.out.println("\n=== Parsed Rooms ===");
            for (String roomName : rooms.keySet()) {
                Room room = rooms.get(roomName);
                System.out.println("Room: " + room.getRoomID());
                System.out.println(" - Neighbors: " + room.getAdjacentRooms());
            }

            // Step 4: Verify Specific Room (e.g., Train Station)
            Room trainStation = rooms.get("train station");
            if (trainStation != null) {
                System.out.println("\nNeighbors of Train Station: " + trainStation.getAdjacentRooms());
            } else {
                System.out.println("\nERROR: Train Station not found!");
            }

            // Step 5: Check Trailer and Casting Office
            Room trailer = rooms.get("trailer");
            System.out.println("Trailer Neighbors: " + (trailer != null ? trailer.getAdjacentRooms() : "Not found"));

            Room office = rooms.get("office");
            System.out.println("Casting Office Neighbors: " + (office != null ? office.getAdjacentRooms() : "Not found"));

            System.out.println("\n=== BoardXMLParser Test Complete ===");

        } catch (Exception e) {
            System.out.println("Error during board XML parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
