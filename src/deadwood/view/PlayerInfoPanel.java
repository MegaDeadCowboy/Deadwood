package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import deadwood.model.*;
import deadwood.controller.GameBoard;

//Panel for displaying information about the current player.
public class PlayerInfoPanel extends JPanel {

    private GameBoard gameBoard;
    private JLabel playerIdLabel;
    private JLabel playerStatsLabel;
    private JLabel playerRoleLabel;
    private JLabel rehearsalBonusLabel;

    public PlayerInfoPanel(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Player Information",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(230, 150));
        
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
        add(rehearsalBonusLabel);
        add(Box.createVerticalStrut(5));
    }
    
    public void updatePlayerInfo() {
        // Get current player information
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        if (currentPlayer == null) {
            return;
        }
        
        // Update player ID label
        playerIdLabel.setText("Current Player: P" + currentPlayer.getPlayerID());
        
        // Update player stats (cash, credits, rank)
        int cash = currentPlayer.getPoints().getPlayerCash();
        int credits = currentPlayer.getPoints().getPlayerCredit();
        int rank = currentPlayer.getCurrentRank();
        playerStatsLabel.setText(String.format("$%d, %d credits, Rank %d", cash, credits, rank));
        
        // Update role information
        String playerRole = currentPlayer.getCurrentRole();
        
        if (playerRole != null) {
            Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
            Set currentSet = (currentRoom != null) ? currentRoom.getSet() : null;
            
            String roleDescription = "Unknown";
            String roleLine = "";
            
            if (currentSet != null) {
                RoleCard.Role role = currentSet.getRole(playerRole);
                if (role != null) {
                    roleDescription = role.getName() + " (Rank " + role.getLevel() + ")";
                    roleLine = "\"" + role.getLine() + "\"";
                }
            }
            
            // Update role and line information
            playerRoleLabel.setText("<html>Role: " + roleDescription + "<br>" + roleLine + "</html>");
            
            // Update rehearsal bonus
            int rehearsalBonus = currentPlayer.getPoints().getRehearsalBonus();
            rehearsalBonusLabel.setText("Rehearsal Bonus: +" + rehearsalBonus);
            rehearsalBonusLabel.setVisible(true);
        } else {
            playerRoleLabel.setText("Role: None");
            rehearsalBonusLabel.setText("Rehearsal Bonus: 0");
            rehearsalBonusLabel.setVisible(false);
        }
    }
}