import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

// Initializes the game
public class GameBoard {
    private Map<String, Room> rooms;
    private List<Actor> players;
    private int playerCount;
    private TurnTracker turnTracker;
    private DayTracker dayTracker;
    private List<RoleCard> cards;
    
    public GameBoard(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 8) {
            throw new IllegalArgumentException("Game requires 2-8 players");
        }
        this.playerCount = numPlayers;
        this.players = new ArrayList<>();
        this.rooms = new HashMap<>();
        this.cards = new ArrayList<>();
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
        
        // Parse cards and distribute them to sets
        loadCardsAndDistribute();
    
        // Create dayTracker first
        this.dayTracker = new DayTracker(4);
        
        // Create turnTracker after players and dayTracker are initialized
        this.turnTracker = new TurnTracker(players, dayTracker, (Trailer) rooms.get("trailer"), this);

        // Set initial player locations
        resetPlayerLocations();
    }
    
    
    private void loadCardsAndDistribute() {
        try {
            // Parse cards XML
            ParseXML parser = new ParseXML();
            Document cardDoc = parser.getDocFromFile("cards.xml");
            
            // Use CardXMLParser to extract card information
            CardXMLParser cardParser = new CardXMLParser(cardDoc);
            this.cards = cardParser.parseCards();
            
            // Distribute cards to sets
            distributeCards(cards);
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to load cards - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createRooms() {
        BoardXMLParser boardParser = null;
    
        try {
            ParseXML parser = new ParseXML();
            Document doc = parser.getDocFromFile("board.xml");
            boardParser = new BoardXMLParser(doc);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to parse board.xml - " + e.getMessage());
            e.printStackTrace();
            return;
        }
    
        Map<String, Room> parsedRooms = boardParser.parseRooms();
    
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
    
    
    public int endTurn() {
        System.out.println("Ending turn for Player " + getCurrentPlayer().getPlayerID());
        
        // Use turnTracker to advance to the next player
        turnTracker.endTurn();
        
        // Get the new current player ID
        int newPlayerID = getCurrentPlayer().getPlayerID();
        
        System.out.println("Starting turn for Player " + newPlayerID);
        
        return newPlayerID;
    }
    
    
    public void reloadAndDistributeCards() {
        // First, clear all existing sets from rooms
        for (Room room : rooms.values()) {
            // Remove any existing set
            room.completeScene(); 
        }
        
        // Then load and distribute new cards
        loadCardsAndDistribute();
    }
    
    public Room getRoomByID(String roomID) {
        return rooms.get(roomID);
    }
    
    
    private void distributeCards(List<RoleCard> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("ERROR: No cards available to distribute!");
            return;
        }
        
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
                
                // Check if the room already has a set with extra roles
                Set existingSet = room.getSet();
                RoleCard extraRolesCard = null;
                if (existingSet != null) {
                    extraRolesCard = existingSet.getExtraRolesCard();
                    shots = existingSet.getShotCounter(); // Preserve the shot counter
                }
                
                // Create a new set for this room with the card, preserving extra roles
                Set set = new Set(card, shots, extraRolesCard != null ? extraRolesCard.getSceneRoles().size() : 0);
                
                // If we had extra roles, keep them
                if (extraRolesCard != null) {
                    set.setExtraRolesCard(extraRolesCard);
                }
                
                // Assign the set to the room
                room.assignSet(set);
                
                System.out.println("Assigned Scene " + card.getSceneID() + " to " + room.getRoomID());
            }
        }
    }

    public List<String> getAllRoomNames() {
        return new ArrayList<>(rooms.keySet());
    }
    
    
    public List<Actor> getAllPlayers() {
        return players;
    }
}