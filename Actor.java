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
        // Get current room's set information
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null || !currentSet.isActive()) {
            System.out.println("No active set in this room.");
            return false;
        }
        
        // Already in a role
        if (currentRole != null) {
            System.out.println("You're already working as " + currentRole + ". Finish or abandon this role first.");
            return false;
        }
        
        // Get the role card and find the requested role
        RoleCard roleCard = currentSet.getRoleCard();
        RoleCard.Role role = null;
        
        // Search for the role (case insensitive)
        for (RoleCard.Role r : roleCard.getSceneRoles()) {
            if (r.getName().equalsIgnoreCase(roleName)) {
                role = r;
                break;
            }
        }
        
        // Role not found
        if (role == null) {
            System.out.println("Role '" + roleName + "' not found in this scene.");
            return false;
        }
        
        // Check rank requirements
        if (role.getLevel() > currentRank) {
            System.out.println("Cannot take this role - your rank (" + currentRank + 
                               ") is too low for " + role.getName() + " (rank " + role.getLevel() + ").");
            return false;
        }
        
        // Check if role is already taken
        if (currentSet.isRoleTaken(role.getName())) {
            System.out.println("This role is already taken by another player.");
            return false;
        }
        
        // Take the role
        currentRole = role.getName();
        currentSet.assignRole(role.getName(), String.valueOf(playerID));
        
        System.out.println("Now working as " + role.getName() + " (rank " + role.getLevel() + ")");
        System.out.println("Line: \"" + role.getLine() + "\"");
        
        return true;
    }

    public boolean inputAttemptScene() {
        if (currentRole == null) {
            System.out.println("Not currently in a role.");
            return false;
        }
        
        // Get current room and set
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null || !currentSet.isActive()) {
            System.out.println("No active set in this room.");
            return false;
        }
        
        // Get dice roll and compare against budget
        int diceRoll = rollDice();
        int rehearsalBonus = points.getRehearsalBonus();
        int totalRoll = diceRoll + rehearsalBonus;
        
        RoleCard roleCard = currentSet.getRoleCard();
        int budget = roleCard.getSceneBudget();
        
        System.out.println("Acting attempt - You rolled: " + diceRoll + 
                           (rehearsalBonus > 0 ? " + " + rehearsalBonus + " (rehearsal bonus)" : "") + 
                           " = " + totalRoll + " vs Budget: " + budget);
        
        if (totalRoll >= budget) {
            // Acting success
            System.out.println("Acting success!");
            
            // Award points
            boolean isExtra = true; // Determine if this is an extra role or starring role
            points.awardActingPoints(true, isExtra);
            
            // Decrement shot counter
            Boolean sceneWrapped = currentSet.decrementShots();
            
            // If the scene is now wrapped, complete it
            if (sceneWrapped) {
                System.out.println("Scene wrapped in " + currentRoom.getRoomID() + "!");
                
                // Award scene bonus
                // TODO: Award scene bonus to all players in roles
                
                // Complete the scene in the room
                currentRoom.completeScene();
                
                // Reset player role
                currentRole = null;
            }
            
            return true;
        }
        else {
            // Acting failed
            System.out.println("Acting failed.");
            points.awardActingPoints(false, true); // Extra roles still get a dollar on failure
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

        /**
     * Get all available roles at the player's current location
     * @return List of RoleCard.Role objects that are available, or null if no set is present
     */
    public List<RoleCard.Role> getAvailableRoles() {
        // Check if player is at a valid location
        Room currentRoom = location.getCurrentRoom();
        if (currentRoom == null) {
            return null;
        }
        
        // Check if there's an active set
        Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            return null;
        }
        
        // Get roles from the RoleCard
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard == null) {
            return null;
        }
        
        // Get all roles that are appropriate for the player's rank and not already taken
        List<RoleCard.Role> availableRoles = new ArrayList<>();
        
        for (RoleCard.Role role : roleCard.getSceneRoles()) {
            // Check if role is appropriate for player rank and not taken
            if (role.getLevel() <= this.currentRank && !currentSet.isRoleTaken(role.getName())) {
                availableRoles.add(role);
            }
        }
        
        // Also check for extra roles if any
        // (This would depend on your implementation of extra roles)
        
        // Debug output
        System.out.println("Found " + availableRoles.size() + " available roles for player rank " + currentRank);
        for (RoleCard.Role role : availableRoles) {
            System.out.println("Available: " + role.getName() + " (Rank " + role.getLevel() + ")");
        }
        
        return availableRoles;
    }

    /**
     * Get the scene information at the player's current location
     * @return String with scene details, or null if no scene
     */
    public String getCurrentSceneInfo() {
        Room currentRoom = location.getCurrentRoom();
        if (currentRoom == null) {
            return null;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            return null;
        }
        
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard == null) {
            return null;
        }
        
        return String.format("Scene %d: \"%s\" (Budget: $%d)\nDescription: %s",
            roleCard.getSceneID(),
            roleCard.getSceneName(),
            roleCard.getSceneBudget(),
            roleCard.getSceneDescription());
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