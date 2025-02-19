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
        System.out.print("> ");
        String input = scanner.nextLine().toLowerCase().trim();
        String[] parts = input.split("\\s+");
        String command = parts[0];
        
        switch (command) {
            case "who":
                displayPlayerInfo();
                break;
                
            case "where":
                displayLocation();
                break;
                
            case "move":
                if (parts.length < 2) {
                    System.out.println("Please specify a destination.");
                    return;
                }
                String destination = String.join(" ", parts[1]);
                currentPlayer.inputMove(destination);
                break;
                
            case "work":
                if (parts.length < 2) {
                    System.out.println("Please specify a role.");
                    return;
                }
                String role = String.join(" ", parts[1]);
                currentPlayer.inputRole(role);
                break;
                
            case "act":
                currentPlayer.inputAttemptScene();
                break;
                
            case "rehearse":
                currentPlayer.inputRehearse();
                break;
                
            case "upgrade":
                if (parts.length < 3) {
                    System.out.println("Please specify rank and payment type (credits/cash).");
                    return;
                }
                try {
                    int rank = Integer.parseInt(parts[1]);
                    String paymentType = parts[2];
                    currentPlayer.inputUpgrade(rank, paymentType);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid rank number.");
                }
                break;
                
            case "end":
                System.out.println("Turn ended.");
                break;
                
            case "quit":
                System.out.println("Game ended.");
                System.exit(0);
                break;
                
            default:
                System.out.println("Invalid command. Available commands: who, where, move, work, act, rehearse, upgrade, end, quit");
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
        Set currentSet = currentRoom.getSet();
        
        if (currentSet != null && currentSet.isActive()) {
            System.out.printf("%s shooting Scene %d%n", 
                currentRoom.getRoomID(),
                currentSet.getRoleCard().getSceneID()
            );
        } else {
            System.out.println(currentRoom.getRoomID());
        }
    }
}