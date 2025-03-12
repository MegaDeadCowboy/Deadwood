package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import deadwood.model.*;
import deadwood.controller.GameBoard;


//Panel for displaying information about the current scene.
public class SceneInfoPanel extends JPanel {

    private GameBoard gameBoard;
    private JLabel sceneNameLabel;
    private JLabel sceneBudgetLabel;
    private JLabel sceneShotsLabel;
    private JTextArea sceneDescriptionArea;

    public SceneInfoPanel(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Current Scene",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(230, 180));
        
        // Initialize components
        initializeComponents();
        
        // Update with initial scene data
        updateSceneInfo();
    }
    

    private void initializeComponents() {
        // Scene name label
        sceneNameLabel = new JLabel("No active scene");
        sceneNameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        sceneNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Scene budget label
        sceneBudgetLabel = new JLabel("Budget: $0");
        sceneBudgetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Scene shots label
        sceneShotsLabel = new JLabel("Shots remaining: 0");
        sceneShotsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Scene description area
        sceneDescriptionArea = new JTextArea(4, 20);
        sceneDescriptionArea.setEditable(false);
        sceneDescriptionArea.setLineWrap(true);
        sceneDescriptionArea.setWrapStyleWord(true);
        sceneDescriptionArea.setBackground(getBackground());
        JScrollPane scrollPane = new JScrollPane(sceneDescriptionArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Add components with spacing
        add(Box.createVerticalStrut(5));
        add(sceneNameLabel);
        add(Box.createVerticalStrut(5));
        add(sceneBudgetLabel);
        add(Box.createVerticalStrut(5));
        add(sceneShotsLabel);
        add(Box.createVerticalStrut(5));
        add(scrollPane);
    }
    

    public void updateSceneInfo() {
        // Get current player and room
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        if (currentPlayer == null) {
            return;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        
        if (currentRoom == null) {
            setNoSceneInfo();
            return;
        }
        
        // Check if there's an active set in this room
        Set currentSet = currentRoom.getSet();
        
        if (currentSet == null || !currentSet.isActive()) {
            setNoSceneInfo();
            return;
        }
        
        // Get the scene card
        RoleCard roleCard = currentSet.getRoleCard();
        
        if (roleCard == null) {
            setNoSceneInfo();
            return;
        }
        
        // Update scene information
        sceneNameLabel.setText("Scene " + roleCard.getSceneID() + ": " + roleCard.getSceneName());
        sceneBudgetLabel.setText("Budget: $" + roleCard.getSceneBudget());
        sceneShotsLabel.setText("Shots remaining: " + currentSet.getShotCounter());
        sceneDescriptionArea.setText(roleCard.getSceneDescription());
        
        // Make sure all components are visible
        sceneNameLabel.setVisible(true);
        sceneBudgetLabel.setVisible(true);
        sceneShotsLabel.setVisible(true);
        sceneDescriptionArea.setVisible(true);
    }

    private void setNoSceneInfo() {
        sceneNameLabel.setText("No active scene");
        sceneBudgetLabel.setText("Budget: $0");
        sceneShotsLabel.setText("Shots remaining: 0");
        sceneDescriptionArea.setText("Visit a set with an active scene to see details.");
    }
}