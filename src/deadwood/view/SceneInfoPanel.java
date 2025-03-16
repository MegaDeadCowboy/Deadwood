package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import deadwood.controller.GameController;
import deadwood.controller.GameController.SceneViewModel;
import deadwood.controller.GameController.RoleViewModel;

/**
 * Panel for displaying information about the current scene.
 * Refactored to use the MVC pattern with GameController.
 */
public class SceneInfoPanel extends JPanel implements GameController.GameObserver {

    private GameController controller;
    private JLabel sceneNameLabel;
    private JLabel sceneBudgetLabel;
    private JLabel sceneShotsLabel;
    private JTextArea sceneDescriptionArea;
    private JLabel roleStatusLabel;

    public SceneInfoPanel(GameController controller) {
        this.controller = controller;
        this.controller.registerObserver(this);
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Current Scene",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(260, 200));  // Increased height to accommodate role status
        
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
        
        // Role status label
        roleStatusLabel = new JLabel("Available roles: 0");
        roleStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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
        add(roleStatusLabel);
        add(Box.createVerticalStrut(5));
        add(scrollPane);
    }
    
    public void updateSceneInfo() {
        // Get current scene information from the controller
        SceneViewModel scene = controller.getCurrentSceneViewModel();
        
        if (scene == null || !scene.isActive()) {
            setNoSceneInfo();
            return;
        }
        
        // Update scene information
        sceneNameLabel.setText("Scene " + scene.getSceneId() + ": " + scene.getSceneName());
        sceneBudgetLabel.setText("Budget: $" + scene.getBudget());
        sceneShotsLabel.setText("Shots remaining: " + scene.getShotsRemaining());
        sceneDescriptionArea.setText(scene.getDescription());
        
        // Calculate and display role statistics
        int totalRoles = scene.getStarringRoles().size() + scene.getExtraRoles().size();
        int availableRoles = 0;
        int takenRoles = 0;
        int completedRoles = 0;
        
        // Count starring roles status
        for (RoleViewModel role : scene.getStarringRoles()) {
            if (role.isActed()) {
                completedRoles++;
            } else if (role.isTaken()) {
                takenRoles++;
            } else {
                availableRoles++;
            }
        }
        
        // Count extra roles status
        for (RoleViewModel role : scene.getExtraRoles()) {
            if (role.isActed()) {
                completedRoles++;
            } else if (role.isTaken()) {
                takenRoles++;
            } else {
                availableRoles++;
            }
        }
        
        roleStatusLabel.setText(String.format("Roles: %d available, %d taken, %d completed", 
            availableRoles, takenRoles, completedRoles));
        
        // Make sure all components are visible
        sceneNameLabel.setVisible(true);
        sceneBudgetLabel.setVisible(true);
        sceneShotsLabel.setVisible(true);
        roleStatusLabel.setVisible(true);
        sceneDescriptionArea.setVisible(true);
    }

    private void setNoSceneInfo() {
        sceneNameLabel.setText("No active scene");
        sceneBudgetLabel.setText("Budget: $0");
        sceneShotsLabel.setText("Shots remaining: 0");
        roleStatusLabel.setText("Roles: none");
        sceneDescriptionArea.setText("Visit a set with an active scene to see details.");
    }
    
    // GameObserver methods
    @Override
    public void onGameStateChanged() {
        updateSceneInfo();
    }
    
    @Override
    public void onPlayerChanged(GameController.PlayerViewModel player) {
        // Not directly relevant - maybe player moved to a new room
        updateSceneInfo();
    }
    
    @Override
    public void onSceneChanged(SceneViewModel scene) {
        updateSceneInfo();
    }
    
    @Override
    public void onBoardChanged() {
        updateSceneInfo();
    }
}