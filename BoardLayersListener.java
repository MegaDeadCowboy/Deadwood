import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardLayersListener extends JFrame {
    private GameBoard gameBoard;
    private JLayeredPane bPane;
    private JLabel boardlabel;
    private JLabel currentPlayerLabel;
    private Map<Integer, JLabel> playerIcons;
    private String[] diceColors = {"b", "c", "g", "o", "p", "r", "v", "y"}; // Color codes for players
    
    // Buttons
    private JButton bAct, bRehearse, bMove, bEndTurn;
    
    public BoardLayersListener(int numPlayers) {
        super("Deadwood");
        this.gameBoard = new GameBoard(numPlayers);
        this.playerIcons = new HashMap<>();
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        bPane = getLayeredPane();
        
        // Load Board Background
        boardlabel = new JLabel(new ImageIcon("board.jpg"));
        boardlabel.setBounds(0, 0, boardlabel.getIcon().getIconWidth(), boardlabel.getIcon().getIconHeight());
        bPane.add(boardlabel, 0);
        
        setSize(boardlabel.getIcon().getIconWidth() + 200, boardlabel.getIcon().getIconHeight());
        
        // Initialize Player Icons in Trailer
        initializePlayerIcons();
        
        // Add Buttons
        addGameButtons();
        
        // Display current player info
        currentPlayerLabel = new JLabel("Current Player: P" + gameBoard.getCurrentPlayer().getPlayerID());
        currentPlayerLabel.setBounds(boardlabel.getIcon().getIconWidth() + 10, 200, 180, 30);
        bPane.add(currentPlayerLabel, 2);
    }
    
    private void initializePlayerIcons() {
        Room trailer = gameBoard.getRoomByID("Trailer");
        int x = 1000, y = 300; // Initial placement in the trailer
        for (Actor player : gameBoard.getAllPlayers()) {
            int playerIndex = (player.getPlayerID() - 1) % diceColors.length; // Cycle through colors
            String diceImage = diceColors[playerIndex] + "1.png"; // Assign color with side 1
    
            System.out.println("Loading image: " + diceImage + " for Player " + player.getPlayerID());
    
            ImageIcon icon = new ImageIcon(diceImage);
            if (icon.getIconWidth() == -1) {
                System.out.println("Error: Could not load " + diceImage);
            } else {
                System.out.println("Successfully loaded " + diceImage);
            }
    
            JLabel playerLabel = new JLabel(icon);
            playerLabel.setBounds(x, y, 46, 46); // Set position
    
            System.out.println("Placing Player " + player.getPlayerID() + " at X: " + x + ", Y: " + y);
    
            bPane.add(playerLabel, Integer.valueOf(3)); // Make sure it's above background
            playerIcons.put(player.getPlayerID(), playerLabel);
    
            x += 50; // Move right for next dice
        }
    
        bPane.revalidate();
        bPane.repaint();
    }
    
    
    private Point getRoomPosition(String roomID) {
        Map<String, Point> roomPositions = new HashMap<>();
        roomPositions.put("Trailer", new Point(1000, 300));
        roomPositions.put("Train Station", new Point(100, 200)); 
        roomPositions.put("Saloon", new Point(700, 300));
        roomPositions.put("Bank", new Point(700, 400));
        roomPositions.put("Casting Office", new Point(50, 500));
        roomPositions.put("Main Street", new Point(1000, 50));
        roomPositions.put("Ranch", new Point(300, 600));
        roomPositions.put("Secret Hideout", new Point(50, 750));
        roomPositions.put("Church", new Point(700, 750));
        roomPositions.put("Hotel", new Point(1000, 750));
        roomPositions.put("General Store", new Point(400, 300));
        roomPositions.put("Jail", new Point(400, 50));
        return roomPositions.getOrDefault(roomID, new Point(100, 100));
    }
    
    //implement
    private void handleAct() {
        System.out.println("Acting...");
    }
    
    //implement
    private void handleRehearse() {
        System.out.println("Rehearsing...");
    }
    
    private void handleMove() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        List<String> adjacentRooms = currentRoom.getAdjacentRooms();
    
        System.out.println("Player " + currentPlayer.getPlayerID() + " is in " + currentRoom.getRoomID());
        System.out.println("Available move options: " + adjacentRooms);
    
        String destination = (String) JOptionPane.showInputDialog(this, "Choose a room:", "Move", 
            JOptionPane.QUESTION_MESSAGE, null, adjacentRooms.toArray(), adjacentRooms.get(0));
    
        if (destination != null && gameBoard.validatePlayerMove(currentRoom.getRoomID(), destination)) {
            currentPlayer.inputMove(destination, gameBoard);
            updateGameState(); // Move dice when the player moves
        }
    }

    //implement
    private void handleEndTurn() {
        System.out.println("Ending turn...");
    }
    
    private void updatePlayerPosition(Actor player) {
        Room currentRoom = player.getLocation().getCurrentRoom();
        if (currentRoom != null) {
            JLabel playerLabel = playerIcons.get(player.getPlayerID());
            if (playerLabel != null) {
                Point newPosition = getRoomPosition(currentRoom.getRoomID()); // Get new location
                System.out.println("Moving Player " + player.getPlayerID() + " to X: " + newPosition.x + ", Y: " + newPosition.y);
                playerLabel.setLocation(newPosition);
            }
        }
    }
    
    
    private void updateGameState() {
        for (Actor player : gameBoard.getAllPlayers()) {
            updatePlayerPosition(player);
        }
        currentPlayerLabel.setText("Current Player: P" + gameBoard.getCurrentPlayer().getPlayerID());
    }
    
    private void addGameButtons() {
        bAct = new JButton("ACT");
        bAct.setBounds(boardlabel.getIcon().getIconWidth() + 10, 30, 100, 30);
        bAct.addActionListener(e -> handleAct());
        
        bRehearse = new JButton("REHEARSE");
        bRehearse.setBounds(boardlabel.getIcon().getIconWidth() + 10, 70, 100, 30);
        bRehearse.addActionListener(e -> handleRehearse());
        
        bMove = new JButton("MOVE");
        bMove.setBounds(boardlabel.getIcon().getIconWidth() + 10, 110, 100, 30);
        bMove.addActionListener(e -> handleMove());
        
        bEndTurn = new JButton("END TURN");
        bEndTurn.setBounds(boardlabel.getIcon().getIconWidth() + 10, 150, 100, 30);
        bEndTurn.addActionListener(e -> handleEndTurn());
        
        bPane.add(bAct, 2);
        bPane.add(bRehearse, 2);
        bPane.add(bMove, 2);
        bPane.add(bEndTurn, 2);
    }
    
    public static void main(String[] args) {
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 8) {
            String input = JOptionPane.showInputDialog("Enter number of players (2-8):");
            try {
                numPlayers = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number between 2 and 8.");
            }
        }
        
        BoardLayersListener board = new BoardLayersListener(numPlayers);
        board.setVisible(true);
    }
}




