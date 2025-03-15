package deadwood.view;

import java.awt.*;
import javax.swing.*;

import deadwood.controller.GameController;

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
        int scaledWidth = (int)(originalSize.width * 0.85);
        int scaledHeight = (int)(originalSize.height * 0.85);
        boardPanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));
        boardPanel.setMinimumSize(new Dimension(scaledWidth - 50, scaledHeight - 50)); // Allow further compression if needed
        
        // Create sidebar panels
        controlPanel = new GameControlPanel(controller, this);
        playerInfoPanel = new PlayerInfoPanel(controller);
        sceneInfoPanel = new SceneInfoPanel(controller);
        castingOfficePanel = new CastingOfficePanel(controller, this);
        
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
    }
    
    /**
     * Updates the frame title with current game information
     */
    private void updateFrameTitle() {
        GameController.PlayerViewModel player = controller.getCurrentPlayerViewModel();
        if (player != null) {
            setTitle("Deadwood - Player " + player.getPlayerId() + "'s Turn - Rank " + player.getRank());
        }
    }
    
    /**
     * GameObserver methods implementation
     */
    @Override
    public void onGameStateChanged() {
        updateFrameTitle();
        revalidate();
        repaint();
    }
    
    @Override
    public void onPlayerChanged(GameController.PlayerViewModel player) {
        updateFrameTitle();
    }
    
    @Override
    public void onSceneChanged(GameController.SceneViewModel scene) {
        // Not directly relevant for main frame, individual panels will handle this
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