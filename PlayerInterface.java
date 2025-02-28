import java.util.List;
import java.util.Scanner;

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
        
        // Display player status and available destinations at the start of their turn
        displayPlayerInfo();
        displayLocation();
        displayPossibleDestinations();
    
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
                    displayLocation();
                    displayPossibleDestinations();
                    
                    // Ask the player if they want to end their turn after moving
                    System.out.print("Move completed. Turn Ended.");
                    gameBoard.endTurn();
                    
                }
                break;
                
            case "work":
                if (argument.isEmpty()) {
                    System.out.println("Please specify a role.");
                    return;
                }
                currentPlayer.inputRole(argument);
                break;
                
            case "act":
                currentPlayer.inputAttemptScene();
                break;
                
            case "rehearse":
                currentPlayer.inputRehearse();
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
    
    /**
     * Display a list of possible destinations the player can move to
     */
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
            System.out.printf("Location: %s shooting Scene %d%n",
                currentRoom.getRoomID(),
                currentSet.getRoleCard().getSceneID()
            );
        } else {
            System.out.println("Location: " + currentRoom.getRoomID());
        }
    }
    
    /**
     * Display debug information about rooms
     */
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
        
        // Check all rooms to make sure they can be retrieved
        System.out.println("\nRoom retrieval test:");
        for (String roomName : allRooms) {
            Room room = gameBoard.getRoomByID(roomName);
            System.out.println("  " + roomName + " -> " + (room != null ? "Found" : "NOT FOUND"));
        }
        
        System.out.println("=== END DEBUG INFO ===");
    }
    
    /**
     * Display help information with available commands
     */
    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  who        - Display your player information");
        System.out.println("  where      - Show your current location");
        System.out.println("  move [room]- Move to an adjacent room");
        System.out.println("  work [role]- Take a role in the current set");
        System.out.println("  act        - Attempt to act in your current role");
        System.out.println("  rehearse   - Rehearse for your current role");
        System.out.println("  upgrade [rank] [cash/credit] - Upgrade your rank at the Casting Office");
        System.out.println("  end        - End your turn");
        System.out.println("  quit       - Exit the game");
        System.out.println("  help       - Display this help information");
        System.out.println("  debug      - Show debug room information");
    }
}