import java.util.List;
import java.util.ArrayList;

// This is the updated Actor class with improved movement logic
public class Actor {
    private int playerID;
    private int currentRank;
    private String currentRole;
    private PlayerLocation location;
    private PointTracker points;
    
    // Constructor
    public Actor(int id, int rank) {
        this.playerID = id;
        this.currentRank = rank;
        this.currentRole = null;
        this.points = new PointTracker();
        this.location = new PlayerLocation(id);
    }
    
    public boolean inputMove(String destinationRoomID, GameBoard gameBoard) {
        if (location.getCurrentRoom() == null) {
            System.out.println("Error: Player not in a valid room.");
            return false;
        }
        
        // Debug output to help troubleshoot
        Room currentRoom = location.getCurrentRoom();
        // System.out.println("Debug - Current room: " + currentRoom.getRoomID());
        // System.out.println("Debug - Attempting to move to: " + destinationRoomID);
        
        // Get and normalize adjacent room names
        List<String> adjacentRoomNames = currentRoom.getAdjacentRooms();
        // System.out.println("Debug - Adjacent rooms: " + adjacentRoomNames);
        
        List<String> normalizedNeighbors = new ArrayList<>();
        for (String neighbor : adjacentRoomNames) {
            normalizedNeighbors.add(neighbor.toLowerCase());
        }
        
        // Check if destination is valid
        if (!normalizedNeighbors.contains(destinationRoomID.toLowerCase())) {
            System.out.println("Invalid destination. " + destinationRoomID + " is not adjacent to " + 
                currentRoom.getRoomID());
            return false;
        }
        
        // Find the destination room (case insensitive)
        Room destinationRoom = null;
        // First try exact match
        destinationRoom = gameBoard.getRoomByID(destinationRoomID.toLowerCase());
        
        if (destinationRoom == null) {
            // If exact match fails, try to find a case-insensitive match
            for (String roomName : gameBoard.getAllRoomNames()) {
                if (roomName.equalsIgnoreCase(destinationRoomID)) {
                    destinationRoom = gameBoard.getRoomByID(roomName);
                    break;
                }
            }
        }
        
        if (destinationRoom == null) {
            System.out.println("Error: Cannot find room " + destinationRoomID);
            return false;
        }
        
        // Update player location
        location.updatePlayerLocation(destinationRoom);
        System.out.println("You moved to " + destinationRoom.getRoomID() + ".");
        
        return true;
    }
    
    public boolean inputRole(String roleName) {
        // Get current room's set information and validate role
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null || !currentSet.isActive()) {
            System.out.println("No active set in this room.");
            return false;
        }
        
        // Validate role rank against player rank
        RoleCard roleCard = currentSet.getRoleCard();

        if (roleCard.validateRole(roleName, currentRank, getRoleRank(roleName))) {
            currentRole = roleName;
            System.out.println("Now working as " + roleName);
            return true;
        }
        else {
            System.out.println("Cannot take this role - rank too low or role unavailable.");
            return false;
        }
    }

    public boolean inputAttemptScene() {
        if (currentRole == null) {
            System.out.println("Not currently in a role.");
            return false;
        }
        
        // Get dice roll and compare against budget
        int diceRoll = rollDice();
        int rehearsalBonus = points.getRehearsalBonus();
        int totalRoll = diceRoll + rehearsalBonus;
        
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        int budget = currentSet.getRoleCard().getSceneBudget();
        
        if (totalRoll >= budget) {
            points.awardActingPoints(true, currentRole.equals("extra"));
            System.out.println("Acting success! Roll: " + totalRoll + " vs Budget: " + budget);
            currentSet.decrementShots();
            return true;
        }
        else {
            points.awardActingPoints(false, currentRole.equals("extra"));
            System.out.println("Acting failed. Roll: " + totalRoll + " vs Budget: " + budget);
            return false;
        }
    }

    public boolean inputRehearse() {
        if (currentRole == null) {
            System.out.println("Not currently in a role.");
            return false;
        }
        
        points.addRehearsalToken();
        System.out.println("Rehearsal successful. Current bonus: " + points.getRehearsalBonus());
        return true;
    }
    
    public boolean inputUpgrade(int targetRank, String paymentType) {
        Room currentRoom = location.getCurrentRoom();

        if (!(currentRoom instanceof CastingOffice)) {
            System.out.println("Must be in Casting Office to upgrade.");
            return false;
        }
        
        CastingOffice office = (CastingOffice) currentRoom;
        
        if (office.validateUpgrade(currentRank, targetRank, paymentType, points)) {
            currentRank = targetRank;
            office.checkOut(currentRank, points, targetRank, paymentType);
            System.out.println("Successfully upgraded to rank " + targetRank);
            return true;
        }
        else {
            System.out.println("Cannot upgrade - insufficient funds or invalid rank.");
            return false;
        }
    }
    
    // Helper method for dice rolling
    private int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }
    
    // Getters
    public int getPlayerID() { 
        return playerID;
    }

    public int getCurrentRank() { 
        return currentRank; 
    }

    public String getCurrentRole() { 
        return currentRole; 
    }

    public PlayerLocation getLocation() { 
        return location; 
    }

    public PointTracker getPoints() { 
        return points; 
    }
    
    public int getRoleRank(String roleName) {
        // Implement logic to return the role's required rank
        return 2; // Example: Hardcode for now or implement proper logic
    }
}