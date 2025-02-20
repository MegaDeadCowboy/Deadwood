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
            PointTracker points = player.getPoints();
            
            // Set starting credits based on player count
            if (playerCount <= 3) {
                points.setStartingPoints(0, 0);

            } else if (playerCount <= 5) {
                points.setStartingPoints(0, 2);

            } else if (playerCount == 6) {
                points.setStartingPoints(0, 4);

            } else {  // 7-8 players
                player = new Actor(i, 2); 
                points.setStartingPoints(0, 0);
            }
            
            players.add(player);
        }
        
        // Initialize rooms
        createRooms();
        
        // Initialize trackers
        this.turnTracker = new TurnTracker(players, dayTracker, new Trailer());
        this.dayTracker = new DayTracker(4);
        
        // Place all players in trailer
        resetPlayerLocations();
    }
    
    private void createRooms() {
        // Create and connect all rooms
        // Trailer
        Trailer trailer = new Trailer();
        rooms.put("trailer", trailer);
        
        // Casting Office
        CastingOffice office = new CastingOffice();
        rooms.put("office", office);
        
        // Add other rooms and their connections
        // setting up all the sets, their adjacent rooms,
        // and their role cards
    }
    
    public void resetPlayerLocations() {
        Room trailer = rooms.get("trailer");
        for (Actor player : players) {
            player.getLocation().updatePlayerLocation("trailer");
        }
    }
    
    public Actor getCurrentPlayer() {
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
                // Reset scene cards and other day-specific items
            }
    }