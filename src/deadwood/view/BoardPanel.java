package deadwood.view;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import deadwood.model.*;
import deadwood.controller.GameBoard;


public class BoardPanel extends JPanel {

    private GameBoard gameBoard;
    private JLabel boardLabel;
    private Map<Integer, JLabel> playerTokens;
    private Map<String, JLabel> shotCounters;
    private Map<String, Point> roomPositions;
    
    public BoardPanel(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.playerTokens = new HashMap<>();
        this.shotCounters = new HashMap<>();
        

        setLayout(null);
        initializeRoomPositions();
        
        // Load the board image
        ImageIcon boardImage = new ImageIcon("resources/images/board.jpg");
        boardLabel = new JLabel(boardImage);
        boardLabel.setBounds(0, 0, boardImage.getIconWidth(), boardImage.getIconHeight());
        add(boardLabel);
        
        // Set the size of this panel
        setPreferredSize(new Dimension(boardImage.getIconWidth(), boardImage.getIconHeight()));
        
        // Initialize player tokens
        initializePlayerTokens();
        
        // Initialize shot counters
        initializeShotCounters();
    }
    
    
    //Initializes the predefined positions for each room on the board
    private void initializeRoomPositions() {
        roomPositions = new HashMap<>();
        roomPositions.put("trailer", new Point(991, 248));
        roomPositions.put("office", new Point(991, 452));
        roomPositions.put("train station", new Point(114, 69));
        roomPositions.put("secret hideout", new Point(116, 432));
        roomPositions.put("church", new Point(639, 432));
        roomPositions.put("hotel", new Point(908, 432));
        roomPositions.put("main street", new Point(637, 69));
        roomPositions.put("jail", new Point(909, 69));
        roomPositions.put("general store", new Point(239, 165));
        roomPositions.put("ranch", new Point(372, 249));
        roomPositions.put("bank", new Point(495, 249));
        roomPositions.put("saloon", new Point(628, 249));
    }
    
    private void initializePlayerTokens() {
        String[] diceColors = {"b", "c", "g", "o", "p", "r", "v", "w", "y"};
        Room trailer = gameBoard.getRoomByID("trailer");
        
        // Starting position in the trailer
        int baseX = roomPositions.get("trailer").x;
        int baseY = roomPositions.get("trailer").y;
        
        for (Actor player : gameBoard.getAllPlayers()) {
            int playerID = player.getPlayerID();
            
            // Cycle through available dice colors
            int colorIndex = (playerID - 1) % diceColors.length;
            String diceColor = diceColors[colorIndex];
            
            // Load the dice image
            String diceImagePath = "resources/images/dice/" + diceColor + "1.png";
            ImageIcon diceIcon = new ImageIcon(diceImagePath);
            
            // Create the player token label
            JLabel playerToken = new JLabel(diceIcon);
            
            // Position with slight offset to prevent exact overlap
            int offsetX = ((playerID - 1) % 3) * 20;
            int offsetY = ((playerID - 1) / 3) * 20;
            playerToken.setBounds(baseX + offsetX, baseY + offsetY, 
                                 diceIcon.getIconWidth(), diceIcon.getIconHeight());
            
            // Make sure the token is visible
            playerToken.setVisible(true);
            
            // Add the token to the panel - add it after the board but make sure it's visible
            add(playerToken);
            // Make sure player tokens appear on top of the board
            setComponentZOrder(playerToken, 0);
            
            // Store for future reference
            playerTokens.put(playerID, playerToken);
        }
    }
    
    private void initializeShotCounters() {
        // Get all rooms that can have sets
        for (String roomID : gameBoard.getAllRoomNames()) {
            Room room = gameBoard.getRoomByID(roomID);
            
            // Skip rooms that can't have sets (like Trailer, Office)
            if (room instanceof Trailer || room instanceof CastingOffice) {
                continue;
            }
            
            // Get position for this room
            Point position = roomPositions.get(roomID.toLowerCase());
            if (position == null) {
                System.out.println("Warning: No position found for room: " + roomID);
                continue;
            }
            
            // Create shot counter label
            JLabel shotCounter = new JLabel("0");
            shotCounter.setBounds(position.x - 15, position.y - 30, 30, 20);
            shotCounter.setForeground(Color.RED);
            shotCounter.setFont(new Font("Arial", Font.BOLD, 16));
            shotCounter.setVisible(false); // Hide initially
            
            // Add to panel
            add(shotCounter);
            // Set shot counters to appear above the board but below player tokens
            setComponentZOrder(shotCounter, 1);
            
            // Store for future reference
            shotCounters.put(roomID.toLowerCase(), shotCounter);
        }
    }
    
    //Updates the visual state of the board
    public void updateBoard() {
        for (Actor player : gameBoard.getAllPlayers()) {
            updatePlayerPosition(player);
        }
        
        // Update shot counters
        updateShotCounters();
        
        // Highlight current player
        highlightCurrentPlayer();
        
        // Ensure proper z-order for all components
        for (JLabel token : playerTokens.values()) {
            setComponentZOrder(token, 0);
        }
        
        // Refresh display
        revalidate();
        repaint();
    }
    
    //Updates the position of a player token based on their current room
    private void updatePlayerPosition(Actor player) {
        Room currentRoom = player.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            return;
        }
        
        JLabel playerToken = playerTokens.get(player.getPlayerID());
        if (playerToken == null) {
            return;
        }
        
        // Get the base position for the room
        String roomID = currentRoom.getRoomID().toLowerCase();
        Point roomPos = roomPositions.get(roomID);
        
        if (roomPos == null) {
            System.out.println("Warning: No position found for room: " + roomID);
            return;
        }
        
        // Add offset based on player ID to prevent exact overlap
        int playerID = player.getPlayerID();
        int offsetX = ((playerID - 1) % 3) * 20;
        int offsetY = ((playerID - 1) / 3) * 20;
        
        // Update token position
        playerToken.setLocation(roomPos.x + offsetX, roomPos.y + offsetY);
        playerToken.setVisible(true);
    }
    
    private void updateShotCounters() {
        for (String roomID : gameBoard.getAllRoomNames()) {
            Room room = gameBoard.getRoomByID(roomID);
            
            // Skip rooms that can't have sets
            if (room instanceof Trailer || room instanceof CastingOffice) {
                continue;
            }
            
            deadwood.model.Set currentSet = room.getSet();
            JLabel shotCounter = shotCounters.get(roomID.toLowerCase());
            
            if (shotCounter != null) {
                if (currentSet != null && currentSet.isActive()) {
                    shotCounter.setText(String.valueOf(currentSet.getShotCounter()));
                    shotCounter.setVisible(true);
                } else {
                    shotCounter.setVisible(false);
                }
            }
        }
    }
    
    /**
     * Highlights the current player's token
     */
    private void highlightCurrentPlayer() {
        // Remove highlight from all tokens
        for (JLabel token : playerTokens.values()) {
            token.setBorder(null);
        }
        
        // Highlight current player's token
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        JLabel currentToken = playerTokens.get(currentPlayer.getPlayerID());
        
        if (currentToken != null) {
            currentToken.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        }
    }
}