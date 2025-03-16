package deadwood.view;

import java.awt.*;
import javax.swing.*;

import deadwood.controller.GameController;
import deadwood.controller.GameController.PlayerViewModel;

/**
 * Main container class for the Deadwood GUI.
 * Refactored to use the MVC pattern with GameController.
 */
public class GameView extends JFrame implements GameController.GameObserver {

    private GameController controller;
    private BoardPanel boardPanel;
    private PlayerInfoPanel playerInfoPanel;
    private GameControlPanel controlPanel;
    private SceneInfoPanel sceneInfoPanel;
    private CastingOfficePanel castingOfficePanel;
    private JPanel sidebarPanel;
    private JLabel gameStatusLabel;
    
    // Constants for layout dimensions
    private static final int SIDEBAR_WIDTH = 260;
    

    public GameView(int numPlayers) {
        super("Deadwood - Board Game");
        
        // Initialize controller
        this.controller = new GameController(numPlayers);
        
        // Register as observer
        this.controller.registerObserver(this);
        
        // Setup frame properties
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 0)); // Add some gap between center and east
        
        // Initialize components
        initializeComponents();
        
        // Pack and display
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Initializes all UI components
     */
    private void initializeComponents() {
        // Create board panel with scaled down size
        boardPanel = new BoardPanel(controller);
        
        // Scale down the board to 85% of its original size
        Dimension originalSize = boardPanel.getPreferredSize();
        int scaledWidth = (int)(originalSize.width * 0.75);
        int scaledHeight = (int)(originalSize.height * 0.75);
        boardPanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));
        boardPanel.setMinimumSize(new Dimension(scaledWidth - 50, scaledHeight - 50)); // Allow further compression if needed
        
        // Create sidebar panels
        controlPanel = new GameControlPanel(controller, this);
        playerInfoPanel = new PlayerInfoPanel(controller);
        sceneInfoPanel = new SceneInfoPanel(controller);
        castingOfficePanel = new CastingOfficePanel(controller, this);
        
        // Create game status label
        gameStatusLabel = new JLabel("Game started with " + getPlayerCount() + " players");
        gameStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameStatusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Create sidebar container
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, boardPanel.getPreferredSize().height));
        
        // Add panels to sidebar in order
        sidebarPanel.add(controlPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(playerInfoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(castingOfficePanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(sceneInfoPanel);
        sidebarPanel.add(Box.createVerticalGlue()); // Add glue to push components to the top
        sidebarPanel.add(gameStatusLabel);
        
        // Use a scroll pane for the sidebar in case it gets too tall
        JScrollPane sidebarScrollPane = new JScrollPane(sidebarPanel);
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.setBorder(null);
        
        // Create a JSplitPane to allow resizing between board and sidebar
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            true, // Continuous layout while dragging
            boardPanel,
            sidebarScrollPane
        );
        
        // Set divider properties - make sure the sidebar gets enough space
        splitPane.setDividerLocation(scaledWidth);
        splitPane.setResizeWeight(0.65); // Give more space to the sidebar
        splitPane.setDividerSize(8); // Make the divider easier to grab
        splitPane.setOneTouchExpandable(true); // Add expand/collapse buttons to the divider
        
        // Add components to frame
        add(splitPane, BorderLayout.CENTER);
        
        // Set minimum frame size to ensure all components are visible
        setMinimumSize(new Dimension(
            boardPanel.getPreferredSize().width + SIDEBAR_WIDTH + 60, // Add more buffer
            boardPanel.getPreferredSize().height + 80
        ));
        
        // Set initial size slightly larger than minimum to provide comfortable viewing
        setSize(new Dimension(
            boardPanel.getPreferredSize().width + SIDEBAR_WIDTH + 80,
            boardPanel.getPreferredSize().height + 100
        ));
        
        // Update frame title with initial game information
        updateFrameTitle();
    }
    
    /**
     * Updates the frame title with current game information
     */
    private void updateFrameTitle() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        if (player != null) {
            String roleInfo = "";
            if (player.getCurrentRole() != null) {
                roleInfo = " - Role: " + player.getCurrentRole();
                
                // Check if role is completed
                boolean isRoleCompleted = isCurrentRoleCompleted();
                if (isRoleCompleted) {
                    roleInfo += " [COMPLETED]";
                }
            }
            
            setTitle("Deadwood - Player " + player.getPlayerId() + "'s Turn - Rank " + player.getRank() + roleInfo);
        }
    }
    
    /**
     * Check if the current player's role has been completed
     */
    private boolean isCurrentRoleCompleted() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        if (player == null || player.getCurrentRole() == null) {
            return false;
        }
        
        String roleName = player.getCurrentRole();
        GameController.SceneViewModel scene = controller.getCurrentSceneViewModel();
        
        if (scene == null || !scene.isActive()) {
            return false;
        }
        
        // Check in starring roles
        for (GameController.RoleViewModel role : scene.getStarringRoles()) {
            if (role.getName().equals(roleName) && role.isActed()) {
                return true;
            }
        }
        
        // Check in extra roles
        for (GameController.RoleViewModel role : scene.getExtraRoles()) {
            if (role.getName().equals(roleName) && role.isActed()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the number of players in the game
     */
    private int getPlayerCount() {
        return controller.getAllPlayersViewModels().size();
    }
    
    /**
     * Update the game status label
     */
    private void updateGameStatus() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        if (player != null) {
            gameStatusLabel.setText("Player " + player.getPlayerId() + "'s Turn - Waiting for action...");
        }
    }
    
    /**
     * GameObserver methods implementation
     */
    @Override
    public void onGameStateChanged() {
        updateFrameTitle();
        updateGameStatus();
        revalidate();
        repaint();
    }
    
    @Override
    public void onPlayerChanged(GameController.PlayerViewModel player) {
        updateFrameTitle();
        updateGameStatus();
    }
    
    @Override
    public void onSceneChanged(GameController.SceneViewModel scene) {
        // Update the frame title if the scene change affects a role's status
        if (isCurrentRoleCompleted()) {
            updateFrameTitle();
        }
    }
    
    @Override
    public void onBoardChanged() {
        // Not directly relevant for main frame, BoardPanel will handle this
    }
    
    /**
     * Entry point for the GUI version of the game
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Prompt for number of players
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 8) {
            String input = JOptionPane.showInputDialog(
                null,
                "Enter number of players (2-8):",
                "Deadwood - Player Setup",
                JOptionPane.QUESTION_MESSAGE);
            
            // Handle cancel
            if (input == null) {
                System.exit(0);
            }
            
            try {
                numPlayers = Integer.parseInt(input);
                if (numPlayers < 2 || numPlayers > 8) {
                    JOptionPane.showMessageDialog(
                        null, 
                        "Please enter a number between 2 and 8.",
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    null, 
                    "Please enter a valid number.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Create the game view
        final int finalNumPlayers = numPlayers;
        SwingUtilities.invokeLater(() -> new GameView(finalNumPlayers));
    }
}