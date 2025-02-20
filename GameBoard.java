import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Create and connect all rooms
        rooms.put("trailer", new Trailer());
        rooms.put("office", new CastingOffice());
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
        Room current = rooms.get(fromRoom);
        return current != null && current.getAdjacentRooms().contains(toRoom);
    }
    
    public void endTurn() {
        turnTracker.endTurn();
    }
    
    public Room getRoomByID(String roomID) {
        return rooms.get(roomID);
    }
}
