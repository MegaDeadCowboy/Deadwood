package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import deadwood.model.*;
import deadwood.controller.GameBoard;


public class GameControlPanel extends JPanel {
    private GameBoard gameBoard;
    private GameView parentView;
    private JButton actButton;
    private JButton rehearseButton;
    private JButton moveButton;
    private JButton takeRoleButton;
    private JButton endTurnButton;
    private JButton viewSceneButton;
    private JButton helpButton;
    
    public GameControlPanel(GameBoard gameBoard, GameView parentView) {
        this.gameBoard = gameBoard;
        this.parentView = parentView;
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Game Controls",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new GridLayout(7, 1, 0, 5));
        setPreferredSize(new Dimension(230, 250));
        
        // Initialize components
        initializeComponents();
        
        // Update initial button states
        updateButtonStates();
    }
    
    /**
     * Creates and adds all UI components
     */
    private void initializeComponents() {
        // Create action buttons
        actButton = createButton("Act", "Act in your current role", e -> handleAct());
        rehearseButton = createButton("Rehearse", "Rehearse for your current role", e -> handleRehearse());
        moveButton = createButton("Move", "Move to another room", e -> handleMove());
        takeRoleButton = createButton("Take Role", "Take a role on the current set", e -> handleTakeRole());
        endTurnButton = createButton("End Turn", "End your turn", e -> handleEndTurn());
        
        // Create information buttons
        viewSceneButton = createButton("View Scene", "View details about the current scene", e -> handleViewScene());
        helpButton = createButton("Help", "Display game rules and information", e -> handleHelp());
        
        // Add buttons to panel
        add(actButton);
        add(rehearseButton);
        add(moveButton);
        add(takeRoleButton);
        add(viewSceneButton);
        add(helpButton);
        add(endTurnButton);
    }
    
    /**
     * Helper method to create a button with tooltip and action listener
     */
    private JButton createButton(String text, String tooltip, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(listener);
        
        // Try to load icon if available
        try {
            String iconPath = "resources/images/" + text.toLowerCase().replace(" ", "_") + ".png";
            ImageIcon icon = new ImageIcon(iconPath);
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (Exception e) {
            // Proceed without icon
        }
        
        return button;
    }
    
    /**
     * Updates button enabled states based on the current game state
     */
    public void updateButtonStates() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        if (currentPlayer == null) {
            return;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        String currentRole = currentPlayer.getCurrentRole();
        
        // ACT - only enabled if player has a role
        actButton.setEnabled(currentRole != null);
        
        // REHEARSE - only enabled if player has a role
        rehearseButton.setEnabled(currentRole != null);
        
        // MOVE - only enabled if player does NOT have a role
        moveButton.setEnabled(currentRole == null);
        
        // TAKE ROLE - enabled if there are available roles for this player
        boolean canTakeRole = false;
        if (currentRoom != null && currentRole == null) {
            deadwood.model.Set currentSet = currentRoom.getSet();
            if (currentSet != null && currentSet.isActive()) {
                // Check if there are roles available for this player's rank
                canTakeRole = hasAvailableRoles(currentPlayer, currentSet);
            }
        }
        takeRoleButton.setEnabled(canTakeRole);
        
        // VIEW SCENE - enabled if there's an active scene in the current room
        boolean hasActiveScene = false;
        if (currentRoom != null) {
            deadwood.model.Set currentSet = currentRoom.getSet();
            hasActiveScene = (currentSet != null && currentSet.isActive());
        }
        viewSceneButton.setEnabled(hasActiveScene);
        
        // HELP - always enabled
        helpButton.setEnabled(true);
        
        // END TURN - always enabled
        endTurnButton.setEnabled(true);
    }
    
    /**
     * Checks if there are roles available for a player in a set
     */
    private boolean hasAvailableRoles(Actor player, deadwood.model.Set set) {
        int playerRank = player.getCurrentRank();
        
        // Check starring roles
        if (set.getRoleCard() != null) {
            for (RoleCard.Role role : set.getRoleCard().getSceneRoles()) {
                if (role.getLevel() <= playerRank && 
                    !set.isRoleTaken(role.getName()) &&
                    !set.hasRoleBeenActed(role.getName())) {
                    return true;
                }
            }
        }
        
        // Check extra roles
        if (set.getExtraRolesCard() != null) {
            for (RoleCard.Role role : set.getExtraRolesCard().getSceneRoles()) {
                if (role.getLevel() <= playerRank && 
                    !set.isRoleTaken(role.getName()) &&
                    !set.hasRoleBeenActed(role.getName())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Handles the ACT button click
     */
    private void handleAct() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        // Check if player has a role
        if (currentPlayer.getCurrentRole() == null) {
            JOptionPane.showMessageDialog(parentView, 
                "You must have a role before you can act.",
                "Cannot Act", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Attempt to act in the current scene
        boolean success = currentPlayer.inputAttemptScene(gameBoard);
        
        // Provide feedback based on result
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        deadwood.model.Set currentSet = (currentRoom != null) ? currentRoom.getSet() : null;
        
        if (currentSet != null && !currentSet.isActive()) {
            // Scene wrapped
            JOptionPane.showMessageDialog(parentView,
                "The scene has wrapped! All shots completed.",
                "Scene Wrapped",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Update the UI
        parentView.updateGameState();
        
        // End turn after acting
        handleEndTurn();
    }
    
    /**
     * Handles the REHEARSE button click
     */
    private void handleRehearse() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        // Check if player has a role
        if (currentPlayer.getCurrentRole() == null) {
            JOptionPane.showMessageDialog(parentView, 
                "You must have a role before you can rehearse.",
                "Cannot Rehearse", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get rehearsal bonus before attempting
        int oldBonus = currentPlayer.getPoints().getRehearsalBonus();
        
        // Attempt to rehearse
        boolean success = currentPlayer.inputRehearse();
        
        if (success) {
            // Get new bonus
            int newBonus = currentPlayer.getPoints().getRehearsalBonus();
            
            JOptionPane.showMessageDialog(parentView,
                "Rehearsal successful! Your bonus increased from +" + oldBonus + " to +" + newBonus + ".",
                "Rehearsal Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Update the UI
            parentView.updateGameState();
            
            // End turn after rehearsing
            handleEndTurn();
        } else {
            JOptionPane.showMessageDialog(parentView,
                "You've already reached the maximum rehearsal bonus for this role.",
                "Cannot Rehearse",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Handles the MOVE button click
     */
    private void handleMove() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        
        if (currentRoom == null) {
            return;
        }
        
        java.util.List<String> adjacentRooms = currentRoom.getAdjacentRooms();
        
        if (adjacentRooms.isEmpty()) {
            JOptionPane.showMessageDialog(parentView, 
                "No adjacent rooms to move to.",
                "Cannot Move", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create an array of formatted room names for the dropdown
        String[] roomOptions = new String[adjacentRooms.size()];
        for (int i = 0; i < adjacentRooms.size(); i++) {
            roomOptions[i] = adjacentRooms.get(i);
        }
        
        // Show dialog for selecting destination
        String destination = (String) JOptionPane.showInputDialog(
            parentView, 
            "Choose a room to move to:",
            "Move", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            roomOptions, 
            roomOptions[0]);
        
        if (destination != null) {
            // Attempt to move to the selected room
            boolean success = gameBoard.validatePlayerMove(currentRoom.getRoomID(), destination) &&
                             currentPlayer.inputMove(destination, gameBoard);
            
            if (success) {
                // Update the UI - No dialog displayed here anymore
                parentView.updateGameState();
                
                // End turn after moving
                handleEndTurn();
            } else {
                JOptionPane.showMessageDialog(parentView,
                    "Failed to move to " + destination + ".",
                    "Move Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handles the TAKE ROLE button click
     */
    private void handleTakeRole() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        
        if (currentRoom == null) {
            return;
        }
        
        // Check if player already has a role
        if (currentPlayer.getCurrentRole() != null) {
            int response = JOptionPane.showConfirmDialog(
                parentView, 
                "You already have the role: " + currentPlayer.getCurrentRole() + 
                "\nDo you want to abandon it and take a new role?",
                "Abandon Current Role?",
                JOptionPane.YES_NO_OPTION
            );
            
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Abandon current role
            currentPlayer.abandonRole();
        }
        
        // Get current set
        deadwood.model.Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            JOptionPane.showMessageDialog(parentView, 
                "No active set in this room.",
                "Cannot Take Role", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get available roles for the player's rank
        java.util.List<String> availableRoles = new ArrayList<>();
        Map<String, String> roleDescriptions = new HashMap<>();
        
        // Check starring roles
        if (currentSet.getRoleCard() != null) {
            for (RoleCard.Role role : currentSet.getRoleCard().getSceneRoles()) {
                if (role.getLevel() <= currentPlayer.getCurrentRank() && 
                    !currentSet.isRoleTaken(role.getName()) &&
                    !currentSet.hasRoleBeenActed(role.getName())) {
                    
                    availableRoles.add(role.getName());
                    roleDescriptions.put(role.getName(), 
                        role.getName() + " (Rank " + role.getLevel() + ", Starring) - \"" + role.getLine() + "\"");
                }
            }
        }
        
        // Check extra roles
        if (currentSet.getExtraRolesCard() != null) {
            for (RoleCard.Role role : currentSet.getExtraRolesCard().getSceneRoles()) {
                if (role.getLevel() <= currentPlayer.getCurrentRank() && 
                    !currentSet.isRoleTaken(role.getName()) &&
                    !currentSet.hasRoleBeenActed(role.getName())) {
                    
                    availableRoles.add(role.getName());
                    roleDescriptions.put(role.getName(), 
                        role.getName() + " (Rank " + role.getLevel() + ", Extra) - \"" + role.getLine() + "\"");
                }
            }
        }
        
        // If no roles available
        if (availableRoles.isEmpty()) {
            JOptionPane.showMessageDialog(parentView, 
                "No roles available for your rank.",
                "Cannot Take Role", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create an array of role descriptions for the dropdown
        String[] roleOptions = new String[availableRoles.size()];
        for (int i = 0; i < availableRoles.size(); i++) {
            roleOptions[i] = roleDescriptions.get(availableRoles.get(i));
        }
        
        // Show role selection dialog
        String selectedDescription = (String) JOptionPane.showInputDialog(
            parentView,
            "Choose a role:",
            "Take Role",
            JOptionPane.QUESTION_MESSAGE,
            null,
            roleOptions,
            roleOptions[0]
        );
        
        // If a role was selected
        if (selectedDescription != null) {
            // Extract the role name from the description
            String selectedRole = selectedDescription.substring(0, selectedDescription.indexOf(" ("));
            
            boolean success = currentPlayer.inputRole(selectedRole);
            
            if (success) {
                // Update UI - No dialog displayed here anymore
                parentView.updateGameState();
                
                // End turn after taking a role
                handleEndTurn();
            }
        }
    }
    
    /**
     * Handles the VIEW SCENE button click
     */
    private void handleViewScene() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        
        if (currentRoom == null) {
            return;
        }
        
        deadwood.model.Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            JOptionPane.showMessageDialog(parentView, 
                "No active scene in this location.", 
                "No Scene Available", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get the scene information
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard == null) {
            JOptionPane.showMessageDialog(parentView, 
                "Scene information is not available.", 
                "Scene Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create detailed scene information
        StringBuilder sceneInfo = new StringBuilder();
        sceneInfo.append("Scene ").append(roleCard.getSceneID()).append(": ")
            .append(roleCard.getSceneName()).append("\n\n");
        sceneInfo.append("Budget: $").append(roleCard.getSceneBudget()).append("\n");
        sceneInfo.append("Shots Remaining: ").append(currentSet.getShotCounter()).append("\n\n");
        sceneInfo.append("Description: ").append(roleCard.getSceneDescription()).append("\n\n");
        
        // Add starring roles
        sceneInfo.append("Starring Roles:\n");
        for (RoleCard.Role role : roleCard.getSceneRoles()) {
            String status = "";
            if (currentSet.isRoleTaken(role.getName())) {
                status = " [TAKEN]";
            } else if (role.getLevel() > currentPlayer.getCurrentRank()) {
                status = " [RANK TOO LOW]";
            }
            
            sceneInfo.append("  - ").append(role.getName())
                .append(" (Rank ").append(role.getLevel()).append(")")
                .append(status).append("\n");
            sceneInfo.append("    Line: \"").append(role.getLine()).append("\"\n");
        }
        
        // Add extra roles if available
        RoleCard extraRolesCard = currentSet.getExtraRolesCard();
        if (extraRolesCard != null && !extraRolesCard.getSceneRoles().isEmpty()) {
            sceneInfo.append("\nExtra Roles:\n");
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                String status = "";
                if (currentSet.isRoleTaken(role.getName())) {
                    status = " [TAKEN]";
                } else if (role.getLevel() > currentPlayer.getCurrentRank()) {
                    status = " [RANK TOO LOW]";
                }
                
                sceneInfo.append("  - ").append(role.getName())
                    .append(" (Rank ").append(role.getLevel()).append(")")
                    .append(status).append("\n");
                sceneInfo.append("    Line: \"").append(role.getLine()).append("\"\n");
            }
        }
        
        // Show the scene information in a scrollable dialog
        JTextArea textArea = new JTextArea(sceneInfo.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(parentView, scrollPane, 
            "Scene Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Handles the HELP button click
     */
    private void handleHelp() {
        // Create help text with game rules and controls
        StringBuilder helpText = new StringBuilder();
        helpText.append("=== DEADWOOD GAME RULES ===\n\n");
        
        helpText.append("OBJECTIVE:\n");
        helpText.append("Earn the most points through a combination of dollars, credits, and rank.\n\n");
        
        helpText.append("GAME ACTIONS:\n");
        helpText.append("• MOVE: Go to an adjacent room (you must abandon your current role)\n");
        helpText.append("• TAKE ROLE: Take an available role on the current set\n");
        helpText.append("• ACT: Attempt to act in your current role\n");
        helpText.append("• REHEARSE: Increase your chances of successful acting\n");
        helpText.append("• UPGRADE: Increase your rank at the Casting Office\n\n");
        
        helpText.append("ACTING:\n");
        helpText.append("• Roll a die + rehearsal bonus\n");
        helpText.append("• If result >= budget: Success! Gain rewards and remove a shot marker\n");
        helpText.append("• If result < budget: Failure. No reward\n\n");
        
        helpText.append("REHEARSING:\n");
        helpText.append("• Each rehearsal adds +1 to future acting attempts\n");
        helpText.append("• Maximum rehearsal bonus = budget - 1\n\n");
        
        helpText.append("WRAPPING A SCENE:\n");
        helpText.append("• When all shots are complete, the scene wraps\n");
        helpText.append("• Players on roles receive bonus payments\n");
        helpText.append("• Starring roles divide dice equal to the budget\n");
        helpText.append("• Extra roles get dollars equal to their rank\n\n");
        
        helpText.append("GAME END:\n");
        helpText.append("• Game ends after a set number of days\n");
        helpText.append("• Final score = dollars + credits + (5 × rank)\n\n");
        
        helpText.append("CONTROL PANEL BUTTONS:\n");
        helpText.append("• ACT: Attempt to act in your current role\n");
        helpText.append("• REHEARSE: Practice for your current role (+1 bonus)\n");
        helpText.append("• MOVE: Travel to an adjacent room\n");
        helpText.append("• TAKE ROLE: Take an available role on the current set\n");
        helpText.append("• VIEW SCENE: See details about the current scene\n");
        helpText.append("• HELP: Display this help information\n");
        helpText.append("• END TURN: Finish your turn\n");
        
        // Show the help text in a scrollable dialog
        JTextArea textArea = new JTextArea(helpText.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(parentView, scrollPane, 
            "Deadwood Game Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Handles the END TURN button click
     */
    public void handleEndTurn() {
        // End the current player's turn
        gameBoard.endTurn();
        
        // Update the UI
        parentView.updateGameState();
    }
}