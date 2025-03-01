import java.util.List;
import java.util.Scanner;

// Main view for the player until we implement the GUI
public class PlayerInterface {
    private Scanner scanner;
    private Actor currentPlayer;
    private GameBoard gameBoard;
    
    public PlayerInterface(GameBoard board) {
        this.scanner = new Scanner(System.in);
        this.gameBoard = board;
    }
    
    public void setCurrentPlayer(Actor player) {
        this.currentPlayer = player;
    }
    
    public void processCommand() {
        if (currentPlayer == null) {
            System.out.println("Error: No player is set. Please ensure the game is properly initialized.");
            return;
        }
        
        // Display player status and available information at the start of their turn - ONCE
        displayPlayerInfo();
        displayLocation();
        displayPossibleDestinations();
        
        // Display available roles only if the player doesn't have a role and is at a set
        if (currentPlayer.getCurrentRole() == null) {
            displayAvailableRoles();
        }
    
        System.out.print("> ");
        String input = scanner.nextLine().trim();
        
        // Split by first space to get command and then the rest as argument
        String command;
        String argument = "";
        
        int firstSpaceIndex = input.indexOf(' ');
        if (firstSpaceIndex > 0) {
            command = input.substring(0, firstSpaceIndex).toLowerCase();
            argument = input.substring(firstSpaceIndex + 1).trim();
        } else {
            command = input.toLowerCase();
        }
    
        switch (command) {
            case "who":
                displayPlayerInfo();
                break;    
                
            case "where":
                displayLocation();
                break;
                
            case "move":
                if (argument.isEmpty()) {
                    System.out.println("Please specify a destination.");
                    displayPossibleDestinations();
                    return;
                }
                
                System.out.println("Attempting to move to: " + argument);
                boolean success = currentPlayer.inputMove(argument, gameBoard);
                
                if (success) {
                    // Display roles at new location only if player doesn't have a role
                    if (currentPlayer.getCurrentRole() == null) {
                        displayAvailableRoles();
                    }
                    
                    // Ask the player if they want to end their turn after moving
                    System.out.print("Move completed. ");
                    gameBoard.endTurn();
                }
                break;
                
            case "work":
                if (argument.isEmpty()) {
                    System.out.println("Please specify a role.");
                    displayAvailableRoles();
                    return;
                }
                boolean roleSuccess = currentPlayer.inputRole(argument);

                if(roleSuccess) {
                    gameBoard.endTurn();
                }

                break;
                
            case "roles":
                displayAvailableRoles();
                break;
                
            case "scene":
                displaySceneInformation();
                break;
                
            case "act":
                currentPlayer.inputAttemptScene();
                
                gameBoard.endTurn();

                break;
                
            case "rehearse":
                boolean rehearseSucess = currentPlayer.inputRehearse();

                if(rehearseSucess) {
                    gameBoard.endTurn();
                }

                break;
                
            case "upgrade":
                if (argument.isEmpty()) {
                    System.out.println("Please specify rank and payment type (format: <rank> <cash/credit>)");
                    return;
                }
                
                String[] parts = argument.split("\\s+");
                if (parts.length < 2) {
                    System.out.println("Please specify both rank and payment type.");
                    return;
                }
                
                try {
                    int rank = Integer.parseInt(parts[0]);
                    String paymentType = parts[1].toLowerCase();
                    
                    if (!paymentType.equals("cash") && !paymentType.equals("credit")) {
                        System.out.println("Payment type must be 'cash' or 'credit'");
                        return;
                    }
                    
                    currentPlayer.inputUpgrade(rank, paymentType);

                    gameBoard.endTurn();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid rank number. Please specify a number.");
                }
                break;
                
            case "end":
                System.out.println("Turn ended.");
                gameBoard.endTurn();
                break;
                
            case "quit":
                System.out.println("Game ended.");
                System.exit(0);
                break;

            case "costs":
                Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
                if (currentRoom instanceof CastingOffice) {
                    ((CastingOffice) currentRoom).displayUpgradeCosts();
                } else {
                    System.out.println("You must be in the Casting Office to view upgrade costs.");
                }
                break;
                
            case "help":
                displayHelp();
                break;
                
            case "debug":
                debugRoomInfo();
                break;
                
            default:
                System.out.println("Invalid command. Type 'help' for available commands.");
        }
    }
    

    private void displayAvailableRoles() {
        // Only show roles if player doesn't have one
        if (currentPlayer.getCurrentRole() != null) {
            System.out.println("You're already working as: " + currentPlayer.getCurrentRole());
            return;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        Set currentSet = currentRoom.getSet();
        
        // Check if there's an active set in this room
        if (currentSet == null) {
            System.out.println("No active set in this room.");
            return;
        }
        
        if (!currentSet.isActive()) {
            System.out.println("The scene in this room has wrapped. No roles available.");
            return;
        }
        
        List<RoleCard.Role> availableRoles = currentPlayer.getAvailableRoles();
        
        if (availableRoles == null || availableRoles.isEmpty()) {
            System.out.println("No roles available for your rank at this location.");
            return;
        }
        
        // Get the scene budget for starring roles and the extra role budget
        int sceneBudget = 0;
        int extraRoleBudget = 0;
        
        if (currentSet.getRoleCard() != null) {
            sceneBudget = currentSet.getRoleCard().getSceneBudget();
        }
        extraRoleBudget = currentSet.getExtraRoleBudget();
        
        // Display scene roles and extra roles separately
        System.out.println("\nAvailable roles at this location:");
        
        // Print scene roles first (starring roles)
        System.out.println("Starring Roles (Scene Budget: $" + sceneBudget + "):");
        boolean hasStarringRoles = false;
        
        if (currentSet.getRoleCard() != null) {
            for (RoleCard.Role role : availableRoles) {
                // Check if this is a starring role (from the scene card)
                boolean isStarringRole = false;
                for (RoleCard.Role sceneRole : currentSet.getRoleCard().getSceneRoles()) {
                    if (role.getName().equals(sceneRole.getName())) {
                        isStarringRole = true;
                        break;
                    }
                }
                
                if (isStarringRole) {
                    hasStarringRoles = true;
                    System.out.printf("  - %s (Rank %d): \"%s\"%n", 
                        role.getName(), 
                        role.getLevel(),
                        role.getLine());
                }
            }
        }
        
        if (!hasStarringRoles) {
            System.out.println("  (None available for your rank)");
        }
        
        // Print extra roles (from the board XML)
        System.out.println("Extra Roles (Budget: " + extraRoleBudget + "):");
        boolean hasExtraRoles = false;
        
        if (currentSet.getExtraRolesCard() != null) {
            for (RoleCard.Role role : availableRoles) {
                // Check if this is an extra role
                boolean isExtraRole = false;
                for (RoleCard.Role extraRole : currentSet.getExtraRolesCard().getSceneRoles()) {
                    if (role.getName().equals(extraRole.getName())) {
                        isExtraRole = true;
                        break;
                    }
                }
                
                if (isExtraRole) {
                    hasExtraRoles = true;
                    System.out.printf("  - %s (Rank %d): \"%s\"%n", 
                        role.getName(), 
                        role.getLevel(),
                        role.getLine());
                }
            }
        }
        
        if (!hasExtraRoles) {
            System.out.println("  (None available for your rank)");
        }
        
        System.out.println("\nNote: Both starring and extra roles use their own budget for success rolls.");
        System.out.println("      Every role (starring or extra) decrements the shot counter on success.");
        System.out.println("Use 'work <role name>' to take a role.");
    }

    // Updated displaySceneInformation method
    private void displaySceneInformation() {
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        Set currentSet = null;
        
        if (currentRoom != null) {
            currentSet = currentRoom.getSet();
        }
        
        if (currentSet == null || !currentSet.isActive()) {
            System.out.println("No active scene at this location.");
            return;
        }
        
        System.out.println("\nCurrent Scene Information:");
        
        // Main scene card info
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard != null) {
            System.out.printf("Scene %d: \"%s\" (Budget: $%d)\n",
                roleCard.getSceneID(),
                roleCard.getSceneName(),
                roleCard.getSceneBudget());
            System.out.printf("Description: %s\n", roleCard.getSceneDescription());
            System.out.printf("Shot Counter: %d\n", currentSet.getShotCounter());
                
            System.out.println("\nStarring Roles (Scene Budget: $" + roleCard.getSceneBudget() + "):");
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                String status = "";
                if (currentSet.isRoleTaken(role.getName())) {
                    status = " [TAKEN]";
                } else if (role.getLevel() > currentPlayer.getCurrentRank()) {
                    status = " [RANK TOO LOW]";
                }
                
                System.out.printf("  - %s (Rank %d)%s: \"%s\"\n", 
                    role.getName(), 
                    role.getLevel(),
                    status,
                    role.getLine());
            }
        }
        
        // Extra roles info
        RoleCard extraRolesCard = currentSet.getExtraRolesCard();
        if (extraRolesCard != null) {
            System.out.println("\nExtra Roles (Budget: " + currentSet.getExtraRoleBudget() + "):");
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                String status = "";
                if (currentSet.isRoleTaken(role.getName())) {
                    status = " [TAKEN]";
                } else if (role.getLevel() > currentPlayer.getCurrentRank()) {
                    status = " [RANK TOO LOW]";
                }
                
                System.out.printf("  - %s (Rank %d)%s: \"%s\"\n", 
                    role.getName(), 
                    role.getLevel(),
                    status,
                    role.getLine());
            }
        }
    }

    // Updated displayPlayerInfo method
    private void displayPlayerInfo() {
        PointTracker points = currentPlayer.getPoints();
        String role = currentPlayer.getCurrentRole();
        System.out.printf("Player %d ($%d, %dcr) Rank: %d%s%n", 
            currentPlayer.getPlayerID(),
            points.getPlayerCash(),
            points.getPlayerCredit(),
            currentPlayer.getCurrentRank(),
            role != null ? " Role: " + role : ""
        );
        
        // If the player has a role, display additional information
        if (role != null) {
            Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
            if (currentRoom != null) {
                Set currentSet = currentRoom.getSet();
                if (currentSet != null) {
                    RoleCard.Role playerRole = currentSet.getRole(role);
                    boolean isExtraRole = currentSet.isExtraRole(role);
                    
                    if (playerRole != null) {
                        int budget = isExtraRole ? currentSet.getExtraRoleBudget() : 
                                    (currentSet.getRoleCard() != null ? 
                                    currentSet.getRoleCard().getSceneBudget() : 0);
                        
                        int rehearsalBonus = currentPlayer.getPoints().getRehearsalBonus();
                        
                        System.out.printf("Current Role: %s (Rank %d, %s)\n", 
                            playerRole.getName(),
                            playerRole.getLevel(),
                            isExtraRole ? "Extra" : "Starring");
                        System.out.printf("Role Line: \"%s\"\n", playerRole.getLine());
                        System.out.printf("Rehearsal Bonus: +%d\n", rehearsalBonus);
                        System.out.printf("Success Requirement: Roll >= %d (Budget)\n", budget);
                    }
                }
            }
        }
    }
    
 
    private void displayPossibleDestinations() {
        PlayerLocation location = currentPlayer.getLocation();
        Room currentRoom = location.getCurrentRoom();
        
        if (currentRoom == null) {
            System.out.println("Error: Player is not in a valid room.");
            return;
        }
        
        List<String> adjacentRooms = currentRoom.getAdjacentRooms();
        
        if (adjacentRooms == null || adjacentRooms.isEmpty()) {
            System.out.println("No adjacent rooms to move to.");
            return;
        }
        
        System.out.println("Possible destinations:");
        for (String roomName : adjacentRooms) {
            System.out.println("  - " + roomName);
        }
    }
    
    private void displayLocation() {
        PlayerLocation location = currentPlayer.getLocation();
        Room currentRoom = location.getCurrentRoom();
    
        if (currentRoom == null) {
            System.out.println("Error: Player is not in a valid room.");
            return;
        }
    
        Set currentSet = currentRoom.getSet();
    
        if (currentSet != null && currentSet.isActive()) {
            System.out.printf("Location: %s shooting Scene %d (Shots remaining: %d)\n",
                currentRoom.getRoomID(),
                currentSet.getRoleCard() != null ? currentSet.getRoleCard().getSceneID() : 0,
                currentSet.getShotCounter());
        } else {
            System.out.println("Location: " + currentRoom.getRoomID());
        }
    }

    private void debugRoomInfo() {
        System.out.println("=== DEBUG ROOM INFO ===");
        
        // Get all room names
        List<String> allRooms = gameBoard.getAllRoomNames();
        System.out.println("All rooms in game: " + allRooms);
        
        // Get current player location
        PlayerLocation location = currentPlayer.getLocation();
        Room currentRoom = location.getCurrentRoom();
        
        if (currentRoom == null) {
            System.out.println("Current room is NULL!");
            return;
        }
        
        System.out.println("Current room ID: " + currentRoom.getRoomID());
        System.out.println("Adjacent rooms: " + currentRoom.getAdjacentRooms());
        
        // Show set information if it exists
        Set currentSet = currentRoom.getSet();
        if (currentSet != null) {
            System.out.println("\nCurrent Set Information:");
            System.out.println("  Shot Counter: " + currentSet.getShotCounter());
            System.out.println("  Is Active: " + currentSet.isActive());
            
            // Show scene card info
            if (currentSet.getRoleCard() != null) {
                System.out.println("  Scene: " + currentSet.getRoleCard().getSceneName());
                System.out.println("  Budget: $" + currentSet.getRoleCard().getSceneBudget());
                System.out.println("  Scene Roles: ");
                for (RoleCard.Role role : currentSet.getRoleCard().getSceneRoles()) {
                    System.out.println("    - " + role.getName() + " (Rank " + role.getLevel() + ")");
                    System.out.println("      Line: \"" + role.getLine() + "\"");
                    System.out.println("      Status: " + (currentSet.isRoleTaken(role.getName()) ? "Taken" : "Available"));
                }
            }
            
            // Show extra roles info
            if (currentSet.getExtraRolesCard() != null) {
                System.out.println("  Extra Roles: ");
                for (RoleCard.Role role : currentSet.getExtraRolesCard().getSceneRoles()) {
                    System.out.println("    - " + role.getName() + " (Rank " + role.getLevel() + ")");
                    System.out.println("      Line: \"" + role.getLine() + "\"");
                    System.out.println("      Status: " + (currentSet.isRoleTaken(role.getName()) ? "Taken" : "Available"));
                }
            }
        }
        
        // Check all rooms to make sure they can be retrieved
        System.out.println("\nRoom retrieval test:");
        for (String roomName : allRooms) {
            Room room = gameBoard.getRoomByID(roomName);
            System.out.println("  " + roomName + " -> " + (room != null ? "Found" : "NOT FOUND"));
        }
        
        System.out.println("=== END DEBUG INFO ===");
    }
    
   
    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  who        - Display your player information");
        System.out.println("  where      - Show your current location");
        System.out.println("  move [room]- Move to an adjacent room");
        System.out.println("  roles      - Show available roles at your location");
        System.out.println("  scene      - Show information about the current scene");
        System.out.println("  work [role]- Take a role in the current set");
        System.out.println("  act        - Attempt to act in your current role");
        System.out.println("  rehearse   - Rehearse for your current role");
        System.out.println("  upgrade [rank] [cash/credit] - Upgrade your rank at the Casting Office");
        System.out.println("  end        - End your turn");
        System.out.println("  quit       - Exit the game");
        System.out.println("  help       - Display this help information");
        System.out.println("  debug      - Show debug room information");
        System.out.println("  prices      - Show available rank upgrade costs (in Casting Office)");
    }
}