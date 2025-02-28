import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

public class GameBoard {
    private Map<String, Room> rooms;
    private List<Actor> players;
    private int playerCount;
    private TurnTracker turnTracker;
    private DayTracker dayTracker;
    
    public GameBoard(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 8) {
            throw new IllegalArgumentException("Game requires 2-8 players");
        }
        this.playerCount = numPlayers;
        this.players = new ArrayList<>();
        this.rooms = new HashMap<>();
        initiateBoardState();
    }
    
    public void initiateBoardState() {
        // Create players with appropriate starting conditions
        for (int i = 1; i <= playerCount; i++) {
            Actor player = new Actor(i, 1);
            players.add(player);
        }
    
        // Initialize rooms
        createRooms();
    
        // Fix: Ensure players exist before initializing turnTracker
        this.dayTracker = new DayTracker(4);
        this.turnTracker = new TurnTracker(players, dayTracker, (Trailer) rooms.get("trailer"), this);

        // Fix: Ensure player locations are properly set
        resetPlayerLocations();
    }
    
    private void createRooms() {
        BoardXMLParser boardParser = null;  // Declare boardParser outside try block
    
        try {
            ParseXML parser = new ParseXML();
            Document doc = parser.getDocFromFile("board.xml");
            boardParser = new BoardXMLParser(doc);  // Assign inside try block
        } catch (Exception e) {
            System.out.println("ERROR: Failed to parse board.xml - " + e.getMessage());
            e.printStackTrace();
            return;  // Exit early if parsing fails
        }
    
        Map<String, Room> parsedRooms = boardParser.parseRooms();  // Now this line works
    
        // Store all parsed rooms in GameBoard
        for (Map.Entry<String, Room> entry : parsedRooms.entrySet()) {
            rooms.put(entry.getKey(), entry.getValue());
        }
    }

    public void resetPlayerLocations() {
        Room trailer = rooms.get("trailer");
    
        if (trailer == null) {
            System.out.println("Error: Trailer room not found.");
            return;
        }
    
        for (Actor player : players) {
            player.getLocation().updatePlayerLocation(trailer);
        }
    }
    
    public Actor getCurrentPlayer() {
        if (players.isEmpty()) {
            System.out.println("Error: No players found in game.");
            return null;
        }
        return turnTracker.getCurrentPlayer();
    }
    
    public Room getRoom(String roomID) {
        return rooms.get(roomID);
    }
    
    public boolean validatePlayerMove(String fromRoom, String toRoom) {
        Room current = rooms.get(fromRoom.toLowerCase());
    
        if (current == null) {
            System.out.println("Error: Current room '" + fromRoom + "' not found.");
            return false;
        }
    
        // Convert all neighbor names to lowercase for case-insensitive comparison
        List<String> normalizedNeighbors = new ArrayList<>();
        for (String neighbor : current.getAdjacentRooms()) {
            normalizedNeighbors.add(neighbor.toLowerCase());
        }
    
        boolean validMove = normalizedNeighbors.contains(toRoom.toLowerCase());
    
        if (!validMove) {
            System.out.println("Move failed: " + toRoom + " is not adjacent to " + fromRoom + ".");
        }
    
        return validMove;
    }
    
    
    public void endTurn() {
        turnTracker.endTurn();
    }
    
    public Room getRoomByID(String roomID) {
        return rooms.get(roomID);
    }
    
    /**
     * Method to distribute role cards to sets at the beginning of a day
     * @param cards List of role cards parsed from XML
     */
    private void distributeCards(List<RoleCard> cards) {
        // Make a copy of the cards we can modify
        List<RoleCard> availableCards = new ArrayList<>(cards);
        
        // Shuffle the cards to randomize distribution
        Collections.shuffle(availableCards);
        
        // Find all rooms that can have sets (exclude trailer and office)
        List<Room> setRooms = new ArrayList<>();
        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();
            if (!(room instanceof Trailer) && !(room instanceof CastingOffice)) {
                setRooms.add(room);
            }
        }
        
        // Distribute cards to rooms
        int cardIndex = 0;
        for (Room room : setRooms) {
            if (cardIndex < availableCards.size()) {
                // Get the next card
                RoleCard card = availableCards.get(cardIndex++);
                
                // Get the number of shots from the board XML (or use a default)
                int shots = 3; // Default value - you might want to read this from XML
                
                // Create a set for this room with the card
                Set set = new Set(card, shots, 0); // Assume no extra roles for now
                
                // Assign the set to the room
                room.assignSet(set);
                
                System.out.println("Assigned scene '" + card.getSceneName() + 
                        "' to room '" + room.getRoomID() + "'");
            }
        }
        
        System.out.println("Distributed " + cardIndex + " cards to sets");
    }

    public List<String> getAllRoomNames() {
        return new ArrayList<>(rooms.keySet());
    }
}
