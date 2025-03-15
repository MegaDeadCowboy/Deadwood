package deadwood.view;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

import deadwood.controller.GameController;
import deadwood.model.PlayerLocation;
import deadwood.controller.GameController.BoardViewModel;
import deadwood.controller.GameController.PlayerTokenViewModel;
import deadwood.controller.GameController.ShotCounterViewModel;

/**
 * Panel for displaying the game board and player tokens.
 * Refactored to use the MVC pattern with GameController.
 * Game logic has been moved to the model (PlayerLocation).
 */
public class BoardPanel extends JPanel implements GameController.GameObserver {

    private GameController controller;
    private JLabel boardLabel;
    private Map<Integer, JLabel> playerTokens;
    private Map<String, JLabel> shotCounters;
    private double scaleFactor = 1.0; // Scale factor for the board
    
    public BoardPanel(GameController controller) {
        this.controller = controller;
        this.controller.registerObserver(this);
        this.playerTokens = new HashMap<>();
        this.shotCounters = new HashMap<>();
        
        setLayout(null);
        
        // Load the board image
        ImageIcon boardImage = new ImageIcon("resources/images/board.jpg");
        boardLabel = new JLabel(boardImage);
        boardLabel.setBounds(0, 0, boardImage.getIconWidth(), boardImage.getIconHeight());
        add(boardLabel);
        
        // Set the size of this panel
        setPreferredSize(new Dimension(boardImage.getIconWidth(), boardImage.getIconHeight()));
        
        // Initialize shot counters for all scene rooms
        initializeShotCounters();
        
        // Update the board with initial state
        updateBoard();
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
        
        // Resize the board image
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
        }
        
        // Update board state with new scale factor
        updateBoard();
    }
    
    /**
     * Initializes shot counter labels for all scene rooms
     */
    private void initializeShotCounters() {
        for (String roomID : PlayerLocation.getSceneRoomIDs()) {
            // Get position for this room
            Point position = PlayerLocation.getRoomPosition(roomID.toLowerCase());
            if (position == null) {
                System.out.println("Warning: No position found for room: " + roomID);
                continue;
            }
            
            // Create shot counter label
            JLabel shotCounter = new JLabel("0");
            shotCounter.setForeground(Color.RED);
            shotCounter.setFont(new Font("Arial", Font.BOLD, 16));
            shotCounter.setVisible(false); // Hide initially
            
            // Add to panel
            add(shotCounter);
            
            // Store for future reference
            shotCounters.put(roomID.toLowerCase(), shotCounter);
        }
    }
    
    /**
     * Updates the visual state of the board
     */
    public void updateBoard() {
        // Get board view model from controller
        BoardViewModel boardVM = controller.getBoardViewModel();
        
        // Update player tokens
        updatePlayerTokens(boardVM.getPlayerTokens());
        
        // Update shot counters
        updateShotCounters(boardVM.getShotCounters());
        
        // Ensure proper z-order for all components
        for (JLabel token : playerTokens.values()) {
            setComponentZOrder(token, 0);
        }
        
        for (JLabel counter : shotCounters.values()) {
            setComponentZOrder(counter, 1);
        }
        
        // Refresh display
        revalidate();
        repaint();
    }
    
    /**
     * Updates all player tokens based on view models
     */
    private void updatePlayerTokens(List<PlayerTokenViewModel> tokenViewModels) {
        // Track current tokens to remove old ones
        Set<Integer> updatedPlayerIds = new HashSet<>();
        
        for (PlayerTokenViewModel tokenVM : tokenViewModels) {
            updatedPlayerIds.add(tokenVM.getPlayerId());
            
            // Get or create the token label
            JLabel tokenLabel = playerTokens.get(tokenVM.getPlayerId());
            if (tokenLabel == null) {
                tokenLabel = createPlayerTokenLabel(tokenVM);
                playerTokens.put(tokenVM.getPlayerId(), tokenLabel);
            }
            
            // Update token position
            updatePlayerTokenPosition(tokenLabel, tokenVM);
            
            // Update token appearance
            if (tokenVM.isCurrentPlayer()) {
                int borderSize = Math.max(2, (int)(2 * scaleFactor));
                tokenLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, borderSize));
            } else {
                tokenLabel.setBorder(null);
            }
        }
        
        // Remove tokens for players that no longer exist
        List<Integer> tokensToRemove = new ArrayList<>();
        for (Integer playerId : playerTokens.keySet()) {
            if (!updatedPlayerIds.contains(playerId)) {
                tokensToRemove.add(playerId);
            }
        }
        
        for (Integer playerId : tokensToRemove) {
            JLabel tokenLabel = playerTokens.remove(playerId);
            if (tokenLabel != null) {
                remove(tokenLabel);
            }
        }
    }
    
    /**
     * Creates a new player token label
     */
    private JLabel createPlayerTokenLabel(PlayerTokenViewModel tokenVM) {
        // Load the dice image
        String diceImagePath = "resources/images/dice/" + tokenVM.getDiceColor() + "1.png";
        ImageIcon diceIcon = new ImageIcon(diceImagePath);
        
        // Debug output to check if image is loading
        if (diceIcon.getIconWidth() <= 0) {
            System.out.println("Warning: Failed to load dice image: " + diceImagePath);
            // Create a colored square as a fallback
            JLabel fallbackToken = new JLabel();
            fallbackToken.setPreferredSize(new Dimension(30, 30));
            fallbackToken.setOpaque(true);
            fallbackToken.setBackground(Color.RED); // Use a bright color to make it visible
            fallbackToken.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            fallbackToken.setText(String.valueOf(tokenVM.getPlayerId()));
            fallbackToken.setHorizontalAlignment(JLabel.CENTER);
            fallbackToken.setVisible(true);
            
            // Add to panel
            add(fallbackToken);
            setComponentZOrder(fallbackToken, 0); // Make sure it's on top
            return fallbackToken;
        }
        
        // Scale the dice image
        Image diceImage = diceIcon.getImage();
        int diceWidth = Math.max(20, (int)(diceIcon.getIconWidth() * scaleFactor));
        int diceHeight = Math.max(20, (int)(diceIcon.getIconHeight() * scaleFactor));
        Image scaledDice = diceImage.getScaledInstance(diceWidth, diceHeight, Image.SCALE_SMOOTH);
        diceIcon = new ImageIcon(scaledDice);
        
        // Create the player token label
        JLabel playerToken = new JLabel(diceIcon);
        playerToken.setVisible(true);
        playerToken.setBounds(0, 0, diceWidth, diceHeight); // Set explicit size
        
        // Add to panel
        add(playerToken);
        setComponentZOrder(playerToken, 0); // Make sure it's on top
        
        return playerToken;
    }

    /**
     * Updates a player token's position
     */
    private void updatePlayerTokenPosition(JLabel tokenLabel, PlayerTokenViewModel tokenVM) {
        if (tokenVM.getRoomId() == null) {
            tokenLabel.setVisible(false);
            return;
        }
        
        // Get the scaled position for the room from the PlayerLocation model
        Point roomPos = PlayerLocation.getScaledRoomPosition(tokenVM.getRoomId(), scaleFactor);
        if (roomPos == null) {
            System.out.println("Warning: No position found for room: " + tokenVM.getRoomId());
            tokenLabel.setVisible(false);
            return;
        }
        
        // Add offset based on player ID to prevent exact overlap
        int playerID = tokenVM.getPlayerId();
        int offsetX = (int)(((playerID - 1) % 3) * 20 * scaleFactor);
        int offsetY = (int)(((playerID - 1) / 3) * 20 * scaleFactor);
        
        // Debug output
        System.out.println("Positioning player " + playerID + " at: " + 
                        (roomPos.x + offsetX) + "," + (roomPos.y + offsetY) + 
                        " (Room: " + tokenVM.getRoomId() + ")");
        
        // Update token position with explicit size
        int tokenWidth = tokenLabel.getWidth();
        int tokenHeight = tokenLabel.getHeight();
        tokenLabel.setBounds(roomPos.x + offsetX, roomPos.y + offsetY, tokenWidth, tokenHeight);
        tokenLabel.setVisible(true);
    }
    
    /**
     * Updates shot counters based on view models
     */
    private void updateShotCounters(List<ShotCounterViewModel> counterViewModels) {
        for (ShotCounterViewModel counterVM : counterViewModels) {
            JLabel counterLabel = shotCounters.get(counterVM.getRoomId());
            if (counterLabel == null) {
                continue;
            }
            
            // Get the scaled position for the room from the PlayerLocation model
            Point roomPos = PlayerLocation.getScaledRoomPosition(counterVM.getRoomId(), scaleFactor);
            if (roomPos == null) {
                counterLabel.setVisible(false);
                continue;
            }
            
            // Update position and size
            counterLabel.setBounds(
                (int)(roomPos.x - 15 * scaleFactor),
                (int)(roomPos.y - 30 * scaleFactor),
                (int)(30 * scaleFactor),
                (int)(20 * scaleFactor)
            );
            
            // Update font size
            int fontSize = (int)(16 * scaleFactor);
            counterLabel.setFont(new Font("Arial", Font.BOLD, Math.max(fontSize, 10))); // Minimum font size of 10
            
            // Update text and visibility
            counterLabel.setText(String.valueOf(counterVM.getShotsRemaining()));
            counterLabel.setVisible(counterVM.isVisible());
        }
    }
    
    // GameObserver methods
    @Override
    public void onGameStateChanged() {
        updateBoard();
    }
    
    @Override
    public void onPlayerChanged(GameController.PlayerViewModel player) {
        updateBoard();
    }
    
    @Override
    public void onSceneChanged(GameController.SceneViewModel scene) {
        updateBoard();
    }
    
    @Override
    public void onBoardChanged() {
        updateBoard();
    }
}