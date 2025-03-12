package deadwood.view;

import deadwood.view.*;
import java.awt.*;
import javax.swing.*;
import deadwood.controller.GameBoard;

//Main container class for the Deadwood GUI.
public class GameView extends JFrame {

    private GameBoard gameBoard;
    private BoardPanel boardPanel;
    private PlayerInfoPanel playerInfoPanel;
    private GameControlPanel controlPanel;
    private SceneInfoPanel sceneInfoPanel;
    private CastingOfficePanel castingOfficePanel;
    private JPanel sidebarPanel;
    

    public GameView(int numPlayers) {
        super("Deadwood - Board Game");
        
        // Initialize game controller
        this.gameBoard = new GameBoard(numPlayers);
        
        // Setup frame properties
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
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
        // Create board panel
        boardPanel = new BoardPanel(gameBoard);
        
        // Create sidebar panels
        controlPanel = new GameControlPanel(gameBoard, this);
        playerInfoPanel = new PlayerInfoPanel(gameBoard);
        sceneInfoPanel = new SceneInfoPanel(gameBoard);
        castingOfficePanel = new CastingOfficePanel(gameBoard, this);
        
        // Create sidebar container
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add panels to sidebar in order
        sidebarPanel.add(controlPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(playerInfoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(castingOfficePanel);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(sceneInfoPanel);
        
        // Use a scroll pane for the sidebar in case it gets too tall
        JScrollPane sidebarScrollPane = new JScrollPane(sidebarPanel);
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.setBorder(null);
        
        // Add components to frame
        add(boardPanel, BorderLayout.CENTER);
        add(sidebarScrollPane, BorderLayout.EAST);
        
        // Update all panels with initial state
        updateGameState();
    }
    
    /**
     * Updates all components to reflect current game state
     */
    public void updateGameState() {
        boardPanel.updateBoard();
        playerInfoPanel.updatePlayerInfo();
        sceneInfoPanel.updateSceneInfo();
        controlPanel.updateButtonStates();
        castingOfficePanel.updateVisibility();
        
        // Repaint the frame
        revalidate();
        repaint();
    }
    
    /**
     * Ends the current player's turn
     * Called by components that need to end the turn
     */
    public void endTurn() {
        gameBoard.endTurn();
        updateGameState();
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