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
    private double scaleFactor = 1.0; // Scale factor for the board
    
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
    
    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        
        // Calculate scale factor based on the preferred size
        Dimension originalSize = boardLabel != null ? 
                new Dimension(boardLabel.getIcon().getIconWidth(), boardLabel.getIcon().getIconHeight()) : 
                new Dimension(1200, 900); // Fallback size
        
        this.scaleFactor = Math.min(
            (double) preferredSize.width / originalSize.width,
            (double) preferredSize.height / originalSize.height
        );
        
        // Resize the board image if it exists
        if (boardLabel != null) {
            // Scale the board image
            ImageIcon originalIcon = (ImageIcon) boardLabel.getIcon();
            Image originalImage = originalIcon.getImage();
            int newWidth = (int) (originalIcon.getIconWidth() * scaleFactor);
            int newHeight = (int) (originalIcon.getIconHeight() * scaleFactor);
            
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            boardLabel.setIcon(scaledIcon);
            boardLabel.setBounds(0, 0, newWidth, newHeight);
            
            // Update room positions based on scale factor
            updateRoomPositions();
            
            // Update player tokens and shot counters
            updateTokensAndCounters();
        }
    }
    
    private void updateTokensAndCounters() {
        // Update player token positions
        for (Actor player : gameBoard.getAllPlayers()) {
            updatePlayerPosition(player);
        }
        
        // Update shot counter positions
        for (String roomID : shotCounters.keySet()) {
            Point roomPos = roomPositions.get(roomID.toLowerCase());
            JLabel counter = shotCounters.get(roomID);
            
            if (roomPos != null && counter != null) {
                counter.setBounds(
                    (int) (roomPos.x - 15 * scaleFactor), 
                    (int) (roomPos.y - 30 * scaleFactor), 
                    (int) (30 * scaleFactor), 
                    (int) (20 * scaleFactor)
                );
                
                // Scale font size
                int fontSize = (int) (16 * scaleFactor);
                counter.setFont(new Font("Arial", Font.BOLD, Math.max(fontSize, 10))); // Minimum font size of 10
            }
        }
    }
    
    private void updateRoomPositions() {
        Map<String, Point> originalPositions = new HashMap<>();
        originalPositions.put("trailer", new Point(991, 248));
        originalPositions.put("office", new Point(991, 452));
        originalPositions.put("train station", new Point(114, 69));
        originalPositions.put("secret hideout", new Point(116, 432));
        originalPositions.put("church", new Point(639, 432));
        originalPositions.put("hotel", new Point(908, 432));
        originalPositions.put("main street", new Point(637, 69));
        originalPositions.put("jail", new Point(909, 69));
        originalPositions.put("general store", new Point(239, 165));
        originalPositions.put("ranch", new Point(372, 249));
        originalPositions.put("bank", new Point(495, 249));
        originalPositions.put("saloon", new Point(628, 249));
        
        // Scale all positions
        roomPositions.clear();
        for (String roomID : originalPositions.keySet()) {
            Point originalPoint = originalPositions.get(roomID);
            roomPositions.put(roomID, new Point(
                (int) (originalPoint.x * scaleFactor),
                (int) (originalPoint.y * scaleFactor)
            ));
        }
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
            
            // Scale the dice image
            Image diceImage = diceIcon.getImage();
            int diceWidth = (int)(diceIcon.getIconWidth() * scaleFactor);
            int diceHeight = (int)(diceIcon.getIconHeight() * scaleFactor);
            Image scaledDice = diceImage.getScaledInstance(diceWidth, diceHeight, Image.SCALE_SMOOTH);
            diceIcon = new ImageIcon(scaledDice);
            
            // Create the player token label
            JLabel playerToken = new JLabel(diceIcon);
            
            // Position with slight offset to prevent exact overlap
            int offsetX = (int)(((playerID - 1) % 3) * 20 * scaleFactor);
            int offsetY = (int)(((playerID - 1) / 3) * 20 * scaleFactor);
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
            shotCounter.setBounds(position.x - (int)(15 * scaleFactor), 
                                 position.y - (int)(30 * scaleFactor), 
                                 (int)(30 * scaleFactor), 
                                 (int)(20 * scaleFactor));
            shotCounter.setForeground(Color.RED);
            shotCounter.setFont(new Font("Arial", Font.BOLD, (int)(16 * scaleFactor)));
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
        int offsetX = (int)(((playerID - 1) % 3) * 20 * scaleFactor);
        int offsetY = (int)(((playerID - 1) / 3) * 20 * scaleFactor);
        
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
            int borderSize = Math.max(2, (int)(2 * scaleFactor));
            currentToken.setBorder(BorderFactory.createLineBorder(Color.YELLOW, borderSize));
        }
    }
}