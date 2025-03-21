package deadwood.model;

import deadwood.model.CastingOffice;
import deadwood.controller.GameBoard;
import deadwood.model.PlayerLocation;
import deadwood.model.PointTracker;
import deadwood.model.RoleCard;
import deadwood.model.Room;
import deadwood.model.Set;
import deadwood.model.RoleCard.Role;
import java.util.List;
import java.util.ArrayList;

public class Actor {
    private int playerID;
    private int currentRank;
    private String currentRole;
    private PlayerLocation location;
    private PointTracker points;
    private boolean isExtraRole;
    
    // Constructor
    public Actor(int id, int rank) {
        this.playerID = id;
        this.currentRank = rank;
        this.currentRole = null;
        this.points = new PointTracker();
        this.location = new PlayerLocation(id);
        this.isExtraRole = false;
    }
    
    public boolean inputMove(String destinationRoomID, GameBoard gameBoard) {
        if (location.getCurrentRoom() == null) {
            System.out.println("Error: Player not in a valid room.");
            return false;
        }
        
        // Abandon current role if player has one
        abandonRole();
        
        Room currentRoom = location.getCurrentRoom();
    
        // Get and normalize adjacent room names
        List<String> adjacentRoomNames = currentRoom.getAdjacentRooms();
        
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
        
        // If destination was not inputted
        if (destinationRoom == null) {
            System.out.println("Error: Cannot find room " + destinationRoomID);
            return false;
        }
        
        // Update player location to the new room
        location.updatePlayerLocation(destinationRoom);
        System.out.println("You moved to " + destinationRoom.getRoomID() + ".");

         // Reset rehearsal bonus when moving to a different room
        points.resetRehearsalBonus();
    
        
        // Return true move was successful
        return true;
    }
    

    public boolean inputRole(String roleName) {
        // Get current room's set information
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null) {
            System.out.println("No set in this room.");
            return false;
        }
        
        if (!currentSet.isActive()) {
            System.out.println("The scene in this room has wrapped. No roles available.");
            return false;
        }
        
        // If already in a role
        if (currentRole != null) {
            System.out.println("You're already working as " + currentRole + ". Finish or abandon this role first.");
            return false;
        }
        
        // Get the role card and find the requested role
        RoleCard roleCard = currentSet.getRoleCard();
        RoleCard extraRolesCard = currentSet.getExtraRolesCard();
        RoleCard.Role role = null;
        boolean isExtraRole = false;
        
        // First search in the main scene roles
        if (roleCard != null) {
            for (RoleCard.Role r : roleCard.getSceneRoles()) {
                if (r.getName().equalsIgnoreCase(roleName)) {
                    role = r;
                    break;
                }
            }
        }
        
        // If not found in main roles, search in extra roles
        if (role == null && extraRolesCard != null) {
            for (RoleCard.Role r : extraRolesCard.getSceneRoles()) {
                if (r.getName().equalsIgnoreCase(roleName)) {
                    role = r;
                    isExtraRole = true;
                    break;
                }
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
        this.isExtraRole = isExtraRole;
        currentSet.assignRole(role.getName(), String.valueOf(playerID));
        
        System.out.println("Now working as " + role.getName() + " (rank " + role.getLevel() + ")");
        System.out.println("Line: \"" + role.getLine() + "\"");
        
        // Chosen role was successful return true
        return true;
    }

    public boolean inputAttemptScene(GameBoard gameBoard) {
        if (currentRole == null) {
            System.out.println("Not currently in a role.");
            return false;
        }
        
        // Get current room and set
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null) {
            System.out.println("No set in this room.");
            return false;
        }
        
        if (!currentSet.isActive()) {
            System.out.println("The scene in this room has wrapped. No acting can be done.");
            return false;
        }
        
        // Check if this role has already been successfully acted
        if (currentSet.hasRoleBeenActed(currentRole)) {
            System.out.println("You've already successfully completed this role. You cannot act in it again.");
            System.out.println("You may take a different role in this scene, move to another room, or end your turn.");
            return false;
        }
        
        // Get dice roll and compare against budget
        int diceRoll = rollDice();
        int rehearsalBonus = points.getRehearsalBonus();
        int totalRoll = diceRoll + rehearsalBonus;
        
        int budget = 0;
        
        // If it's an extra role, use the extra role budget
        if (isExtraRole) {
            budget = currentSet.getExtraRoleBudget();
        } else {
            // For starring roles, use the scene budget
            RoleCard roleCard = currentSet.getRoleCard();
            budget = roleCard.getSceneBudget();
        }
        
        System.out.println("Acting attempt - You rolled: " + diceRoll + 
                           (rehearsalBonus > 0 ? " + " + rehearsalBonus + " (rehearsal bonus)" : "") + 
                           " = " + totalRoll + " vs Budget: " + budget);
        
        // If acting success
        if (totalRoll >= budget) {
            System.out.println("Acting success!");
            
            // Mark this role as successfully acted
            currentSet.markRoleAsActed(currentRole);
            
            // Award points based on role type
            points.awardActingPoints(true, isExtraRole);
    
            // For extra roles or starring roles, decrement the shot counter
            boolean sceneWrapped = currentSet.decrementShots();
            
            // If the scene is now wrapped, complete it
            if (sceneWrapped) {
                System.out.println("Scene wrapped in " + currentRoom.getRoomID() + "!");
                
                // Award bonuses to all players in the scene
                gameBoard.awardSceneBonusesToPlayers(currentRoom, currentSet, this, currentRole);
                currentRoom.completeScene();
                
                // Reset the player's role now that the scene is complete
                currentRole = null;
                isExtraRole = false;
                points.resetRehearsalBonus();
            } else {
                // Important: Let player know they completed the role but the scene continues
                System.out.println("You've successfully completed your role, but the scene continues.");
                System.out.println("You may take a different role in this scene or move to another room on your next turn.");
            }
            
            return true;
        }
        else {
            // Acting failed
            System.out.println("Acting failed.");
            points.awardActingPoints(false, isExtraRole);
            return false;
        }
    }

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
        
        List<RoleCard.Role> availableRoles = new ArrayList<>();
        
        // Check for main scene roles
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard != null) {
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                // Check if role is appropriate for player rank, not taken, and not already acted
                if (role.getLevel() <= this.currentRank && 
                    !currentSet.isRoleTaken(role.getName()) &&
                    !currentSet.hasRoleBeenActed(role.getName())) {
                    
                    availableRoles.add(role);
                }
            }
        }
        
        // Check for extra roles
        RoleCard extraRolesCard = currentSet.getExtraRolesCard();
        if (extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                // Check if role is appropriate for player rank, not taken, and not already acted
                if (role.getLevel() <= this.currentRank && 
                    !currentSet.isRoleTaken(role.getName()) &&
                    !currentSet.hasRoleBeenActed(role.getName())) {
                    
                    availableRoles.add(role);
                }
            }
        }
        return availableRoles;
    }

    public boolean inputRehearse() {
        if (currentRole == null) {
            System.out.println("Not currently in a role.");
            return false;
        }
        
        // Get current room and set
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null) {
            System.out.println("No set in this room.");
            return false;
        }
        
        if (!currentSet.isActive()) {
            System.out.println("The scene in this room has wrapped. No rehearsal can be done.");
            return false;
        }
        
        // Check if this role has already been successfully acted
        if (currentSet.hasRoleBeenActed(currentRole)) {
            System.out.println("You've already completed acting for this role. No need to rehearse.");
            return false;
        }
        
        // Check if rehearsal would exceed the maximum bonus
        RoleCard roleCard = currentSet.getRoleCard();
        int budget = roleCard.getSceneBudget();
        int currentBonus = points.getRehearsalBonus();
        
        if (currentBonus >= budget - 1) {
            System.out.println("You already have the maximum rehearsal bonus for this scene (+" + currentBonus + ").");
            System.out.println("Your bonus is sufficient - time to act instead!");
            return false;
        }
        
        // Add rehearsal token
        boolean added = points.addRehearsalToken();
        
        if (added) {
            System.out.println("Rehearsal successful. Current bonus: +" + points.getRehearsalBonus());
            return true;
        } 
        else {
            System.out.println("You've reached the maximum rehearsal bonus. No more rehearsal is possible.");
            return false;
        }
    }
  

    public boolean inputUpgrade(int targetRank, String paymentType) {
        Room currentRoom = location.getCurrentRoom();
    
        // Not in casting office
        if (!(currentRoom instanceof CastingOffice)) {
            System.out.println("Must be in Casting Office to upgrade.");
            return false;
        }
        
        // Check if player is in a role
        if (currentRole != null) {
            System.out.println("You cannot upgrade while working on a role. Finish or leave your role first.");
            return false;
        }
        
        // Validate rank upgrade (can't downgrade or stay at same rank)
        if (targetRank <= currentRank) {
            System.out.println("Cannot upgrade to rank " + targetRank + ". Must be higher than your current rank (" + currentRank + ").");
            return false;
        }
        
        // Check if rank is valid
        if (targetRank < 1 || targetRank > 6) {
            System.out.println("Invalid rank. Valid ranks are 1 to 6.");
            return false;
        }
        
        // Normalize payment type
        paymentType = paymentType.toLowerCase();
        if (!paymentType.equals("cash") && !paymentType.equals("credit")) {
            System.out.println("Invalid payment type. Use 'cash' or 'credit'.");
            return false;
        }
        
        CastingOffice office = (CastingOffice) currentRoom;
        
        // Check if player can afford the upgrade
        if (office.validateUpgrade(currentRank, targetRank, paymentType, points)) {
            // Process the payment and upgrade
            office.checkOut(currentRank, points, targetRank, paymentType);
            
            // Update player rank
            currentRank = targetRank;
            
            System.out.println("Successfully upgraded to rank " + targetRank + "!");
            
            return true;
        } else {
            // Get the appropriate price for better error messages
            int price = -1;
            if (targetRank >= 2 && targetRank <= 6) {
                int index = targetRank - 2;
                if (paymentType.equals("cash")) {
                    // Access office's upgradePriceCash if possible
                    try {
                        java.lang.reflect.Field field = CastingOffice.class.getDeclaredField("upgradePriceCash");
                        field.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        List<Integer> prices = (List<Integer>) field.get(office);
                        if (index < prices.size()) {
                            price = prices.get(index);
                        }
                    } catch (Exception e) {
                        // If we can't access the field, use estimated prices
                        int[] estimatedPrices = {4, 10, 18, 28, 40};
                        if (index < estimatedPrices.length) {
                            price = estimatedPrices[index];
                        }
                    }
                } else {
                    // Access office's upgradePriceCredit if possible
                    try {
                        java.lang.reflect.Field field = CastingOffice.class.getDeclaredField("upgradePriceCredit");
                        field.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        List<Integer> prices = (List<Integer>) field.get(office);
                        if (index < prices.size()) {
                            price = prices.get(index);
                        }
                    } catch (Exception e) {
                        // If we can't access the field, use estimated prices
                        int[] estimatedPrices = {5, 10, 15, 20, 25};
                        if (index < estimatedPrices.length) {
                            price = estimatedPrices[index];
                        }
                    }
                }
            }
            
            // Provide more specific error message
            if (price > 0) {
                if (paymentType.equals("cash")) {
                    System.out.println("Cannot upgrade - insufficient funds. Rank " + targetRank + " costs $" + price + 
                                       ", but you only have $" + points.getPlayerCash() + ".");
                } else {
                    System.out.println("Cannot upgrade - insufficient credits. Rank " + targetRank + " costs " + price + 
                                       " credits, but you only have " + points.getPlayerCredit() + " credits.");
                }
            } else {
                System.out.println("Cannot upgrade - insufficient funds or invalid rank.");
            }
            
            return false;
        }
    }

   /**
     * Checks if the player's current role has been completed (successfully acted)
     * 
     * @return true if the current role has been completed, false otherwise
     */
    public boolean isCurrentRoleCompleted() {
        // If no role or no current room, role is not completed
        if (currentRole == null || location.getCurrentRoom() == null) {
            return false;
        }
        
        // Get the current set
        Room currentRoom = location.getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        // If no set or set is not active, role is not completed
        if (currentSet == null || !currentSet.isActive()) {
            return false;
        }
        
        // Check if this role has been acted (completed)
        return currentSet.hasRoleBeenActed(currentRole);
    }

    /**
     * Modified abandonRole method that only allows abandoning completed roles
     * 
     * @return true if the role was abandoned, false otherwise
     */
    public boolean abandonRole() {
        // Check if player has a role
        if (currentRole == null) {
            return false;
        }
        
        // Check if role is completed before allowing abandonment
        if (!isCurrentRoleCompleted()) {
            System.out.println("Cannot abandon an incomplete role. You must act or rehearse.");
            return false;
        }
        
        // Get current room and set
        Room currentRoom = location.getCurrentRoom();
        if (currentRoom == null) {
            return false;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null) {
            // Edge case: Role exists but set is gone
            // Just reset player state
            String abandonedRole = currentRole;
            currentRole = null;
            this.isExtraRole = false;
            points.resetRehearsalBonus();
            System.out.println("Role was abandoned: " + abandonedRole);
            return true;
        }
        
        // Release the role in the set
        currentSet.releaseRole(currentRole);
        System.out.println("Abandoning completed role: " + currentRole);
        
        // Reset player role
        String abandonedRole = currentRole;
        currentRole = null;
        this.isExtraRole = false;
        
        // Reset rehearsal bonus when abandoning role
        points.resetRehearsalBonus();
        
        return true;
    }

    public String getCurrentSceneInfo() {
        Room currentRoom = location.getCurrentRoom();
        if (currentRoom == null) {
            return null;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Main scene card info
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard != null) {
            sb.append(String.format("Scene %d: \"%s\" (Budget: $%d)\nDescription: %s\n",
                roleCard.getSceneID(),
                roleCard.getSceneName(),
                roleCard.getSceneBudget(),
                roleCard.getSceneDescription()));
                
            sb.append("\nStarring Roles:\n");
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                sb.append(String.format("  - %s (Rank %d): \"%s\"\n", 
                    role.getName(), 
                    role.getLevel(),
                    role.getLine()));
            }
        }
        
        // Extra roles info
        RoleCard extraRolesCard = currentSet.getExtraRolesCard();
        if (extraRolesCard != null) {
            sb.append("\nExtra Roles:\n");
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                sb.append(String.format("  - %s (Rank %d): \"%s\"\n", 
                    role.getName(), 
                    role.getLevel(),
                    role.getLine()));
            }
        }
        
        return sb.toString();
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
    
}