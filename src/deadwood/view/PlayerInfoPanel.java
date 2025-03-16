package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import deadwood.controller.GameController;
import deadwood.controller.GameController.PlayerViewModel;
import deadwood.controller.GameController.SceneViewModel;
import deadwood.controller.GameController.RoleViewModel;

/**
 * Panel for displaying current player information.
 * Uses MVC pattern with GameController providing data through PlayerViewModel.
 */
public class PlayerInfoPanel extends JPanel implements GameController.GameObserver {

    private GameController controller;
    private JLabel playerIdLabel;
    private JLabel playerStatsLabel;
    private JLabel playerRoleLabel;
    private JLabel rehearsalBonusLabel;
    private JLabel roleStatusLabel;

    public PlayerInfoPanel(GameController controller) {
        this.controller = controller;
        this.controller.registerObserver(this);
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Player Information",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(260, 170));  // Increased height for role status
        
        // Initialize components
        initializeComponents();
        
        // Update with initial data
        updatePlayerInfo();
    }
    
    private void initializeComponents() {
        // Player ID label
        playerIdLabel = new JLabel("Current Player: P1");
        playerIdLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        playerIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player stats label
        playerStatsLabel = new JLabel("$0, 0 credits, Rank 1");
        playerStatsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player role label
        playerRoleLabel = new JLabel("Role: None");
        playerRoleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Role status label (new)
        roleStatusLabel = new JLabel("");
        roleStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleStatusLabel.setForeground(new Color(0, 100, 0)); // Dark green
        
        // Rehearsal bonus label
        rehearsalBonusLabel = new JLabel("Rehearsal Bonus: 0");
        rehearsalBonusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components with spacing
        add(Box.createVerticalStrut(5));
        add(playerIdLabel);
        add(Box.createVerticalStrut(10));
        add(playerStatsLabel);
        add(Box.createVerticalStrut(10));
        add(playerRoleLabel);
        add(Box.createVerticalStrut(5));
        add(roleStatusLabel);
        add(Box.createVerticalStrut(5));
        add(rehearsalBonusLabel);
        add(Box.createVerticalStrut(5));
    }
    
    /**
     * Updates the player information display using the PlayerViewModel
     * from the controller
     */
    public void updatePlayerInfo() {
        // Get current player information from the controller
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        if (player == null) {
            return;
        }
        
        // Update player ID label
        playerIdLabel.setText("Current Player: P" + player.getPlayerId());
        
        // Update player stats (cash, credits, rank)
        playerStatsLabel.setText(String.format("$%d, %d credits, Rank %d", 
                                             player.getCash(), 
                                             player.getCredits(), 
                                             player.getRank()));
        
        // Update role information
        updateRoleDisplay(player);
    }
    
    /**
     * Updates the role display with information from PlayerViewModel
     */
    private void updateRoleDisplay(PlayerViewModel player) {
        String playerRole = player.getCurrentRole();
        
        if (playerRole != null) {
            // Format the role description
            StringBuilder roleDescription = new StringBuilder(playerRole);
            
            // Add role rank if available
            if (player.getRoleRank() > 0) {
                roleDescription.append(" (Rank ").append(player.getRoleRank()).append(")");
            }
            
            // Add role type
            roleDescription.append(player.isRoleExtra() ? " [Extra]" : " [Starring]");
            
            // Format role and line information
            String roleLine = player.getRoleLine() != null ? player.getRoleLine() : "";
            playerRoleLabel.setText("<html>Role: " + roleDescription + "<br>\"" + roleLine + "\"</html>");
            
            // Update rehearsal bonus
            rehearsalBonusLabel.setText("Rehearsal Bonus: +" + player.getRehearsalBonus());
            rehearsalBonusLabel.setVisible(true);
            
            // Check if role has been acted
            boolean roleActed = isRoleActed(playerRole);
            if (roleActed) {
                roleStatusLabel.setText("Status: COMPLETED - Cannot act again");
                roleStatusLabel.setForeground(new Color(139, 0, 0));  // Dark red
            } else {
                roleStatusLabel.setText("Status: Active - Ready to act");
                roleStatusLabel.setForeground(new Color(0, 100, 0));  // Dark green
            }
            roleStatusLabel.setVisible(true);
        } else {
            playerRoleLabel.setText("Role: None");
            rehearsalBonusLabel.setText("Rehearsal Bonus: 0");
            rehearsalBonusLabel.setVisible(false);
            roleStatusLabel.setText("");
            roleStatusLabel.setVisible(false);
        }
    }
    
    /**
     * Helper method to check if a role has been acted (completed)
     */
    private boolean isRoleActed(String roleName) {
        SceneViewModel scene = controller.getCurrentSceneViewModel();
        if (scene == null || !scene.isActive() || roleName == null) {
            return false;
        }
        
        // Check in starring roles
        for (RoleViewModel role : scene.getStarringRoles()) {
            if (role.getName().equals(roleName) && role.isActed()) {
                return true;
            }
        }
        
        // Check in extra roles
        for (RoleViewModel role : scene.getExtraRoles()) {
            if (role.getName().equals(roleName) && role.isActed()) {
                return true;
            }
        }
        
        return false;
    }
    
    // GameObserver methods
    @Override
    public void onGameStateChanged() {
        updatePlayerInfo();
    }
    
    @Override
    public void onPlayerChanged(PlayerViewModel player) {
        updatePlayerInfo();
    }
    
    @Override
    public void onSceneChanged(GameController.SceneViewModel scene) {
        // Update to reflect any role status changes
        updatePlayerInfo();
    }
    
    @Override
    public void onBoardChanged() {
        // Not directly relevant for player info
    }
}