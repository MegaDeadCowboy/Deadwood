package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.List;

import deadwood.controller.GameController;
import deadwood.controller.GameController.PlayerViewModel;
import deadwood.controller.GameController.RoleViewModel;

/**
 * Panel containing game control buttons.
 * Refactored to use the MVC pattern with GameController.
 */
public class GameControlPanel extends JPanel implements GameController.GameObserver {
    private GameController controller;
    private GameView parentView;
    private JButton actButton;
    private JButton rehearseButton;
    private JButton moveButton;
    private JButton takeRoleButton;
    private JButton endTurnButton;
    private JButton viewSceneButton;
    private JButton helpButton;
    private JButton abandonRoleButton; // New button for abandoning role
    
    public GameControlPanel(GameController controller, GameView parentView) {
        this.controller = controller;
        this.parentView = parentView;
        this.controller.registerObserver(this);
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Game Controls",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new GridLayout(8, 1, 0, 5)); // Increased row count for the new button
        setPreferredSize(new Dimension(260, 280)); // Increased height for the new button
        
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
        actButton = createButton("Act", e -> handleAct());
        rehearseButton = createButton("Rehearse", e -> handleRehearse());
        moveButton = createButton("Move", e -> handleMove());
        takeRoleButton = createButton("Take Role", e -> handleTakeRole());
        abandonRoleButton = createButton("Abandon Role", e -> handleAbandonRole()); // New button
        endTurnButton = createButton("End Turn", e -> handleEndTurn());
        
        // Create information buttons
        viewSceneButton = createButton("View Scene", e -> handleViewScene());
        helpButton = createButton("Help", e -> handleHelp());
        
        // Add buttons to panel
        add(actButton);
        add(rehearseButton);
        add(moveButton);
        add(takeRoleButton);
        add(abandonRoleButton); // Add the new button
        add(viewSceneButton);
        add(helpButton);
        add(endTurnButton);
    }
    
    /**
     * Helper method to create a button with action listener
     */
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
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
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        if (player == null) {
            return;
        }
        
        String currentRole = player.getCurrentRole();
        String currentLocation = player.getCurrentLocation();
        boolean isRoleCompleted = isCurrentRoleCompleted();
        
        // ACT - only enabled if player has a role and it's not completed
        actButton.setEnabled(currentRole != null && !isRoleCompleted);
        
        // REHEARSE - only enabled if player has a role and it's not completed
        rehearseButton.setEnabled(currentRole != null && !isRoleCompleted);
        
        // MOVE - only enabled if player does NOT have a role or has a completed role
        moveButton.setEnabled(currentRole == null || isRoleCompleted);
        
        // TAKE ROLE - enabled if there are available roles and player doesn't have a role 
        // or has a completed role
        boolean canTakeRole = controller.canTakeRoles();
        takeRoleButton.setEnabled((currentRole == null || isRoleCompleted) && canTakeRole);
        
        // VIEW SCENE - enabled if there's an active scene in the current room
        boolean hasActiveScene = controller.getCurrentSceneViewModel() != null && 
                            controller.getCurrentSceneViewModel().isActive();
        viewSceneButton.setEnabled(hasActiveScene);
        
        // HELP - always enabled
        helpButton.setEnabled(true);
        
        // END TURN - always enabled
        endTurnButton.setEnabled(true);
    }

    /**
     * Checks if the current player's role has been completed
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
     * Handles the ACT button click
     */
    private void handleAct() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        // Check if player has a role
        if (player.getCurrentRole() == null) {
            JOptionPane.showMessageDialog(parentView, 
                "You must have a role before you can act.",
                "Cannot Act", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if the scene has been wrapped
        GameController.SceneViewModel scene = controller.getCurrentSceneViewModel();
        if (scene == null || !scene.isActive()) {
            JOptionPane.showMessageDialog(parentView,
                "The scene has wrapped! Cannot act in this scene.",
                "Scene Wrapped",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if this role has already been successfully acted
        // This requires examining the scene's roles
        String currentRole = player.getCurrentRole();
        boolean isRoleActed = isCurrentRoleCompleted();
        
        if (isRoleActed) {
            JOptionPane.showMessageDialog(parentView,
                "You've already successfully completed this role! You cannot act in it again.\n" +
                "You may abandon this role to take a different role or move to another room.",
                "Role Completed",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Attempt to act in the current scene
        boolean success = controller.act();
        
        if (success) {
            // Check if scene wrapped after the action
            scene = controller.getCurrentSceneViewModel();
            if (scene == null || !scene.isActive()) {
                // Scene wrapped
                JOptionPane.showMessageDialog(parentView,
                    "The scene has wrapped! All shots completed.",
                    "Scene Wrapped",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Check if role is now completed
                if (isCurrentRoleCompleted()) {
                    JOptionPane.showMessageDialog(parentView,
                        "Acting success! You've successfully completed this role.\n" +
                        "You may abandon this role to take a different role or move to another room.",
                        "Acting Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentView,
                        "Acting success!",
                        "Acting Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            // End turn after successful acting
            handleEndTurn();
        } else {
            JOptionPane.showMessageDialog(parentView,
                "Acting attempt failed. You may try again on your next turn\n" +
                "or consider rehearsing to improve your chances.",
                "Acting Failed",
                JOptionPane.INFORMATION_MESSAGE);
            
            // End turn after failed acting
            handleEndTurn();
        }
    }
    
    /**
     * Handles the REHEARSE button click
     */
    private void handleRehearse() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        // Check if player has a role
        if (player.getCurrentRole() == null) {
            JOptionPane.showMessageDialog(parentView, 
                "You must have a role before you can rehearse.",
                "Cannot Rehearse", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get rehearsal bonus before attempting
        int oldBonus = player.getRehearsalBonus();
        
        // Check if the scene has been wrapped
        GameController.SceneViewModel scene = controller.getCurrentSceneViewModel();
        if (scene == null || !scene.isActive()) {
            JOptionPane.showMessageDialog(parentView,
                "The scene has wrapped! Cannot rehearse for this scene.",
                "Scene Wrapped",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if this role has already been successfully acted
        boolean isRoleActed = isCurrentRoleCompleted();
        
        if (isRoleActed) {
            JOptionPane.showMessageDialog(parentView,
                "You've already completed this role successfully. No need to rehearse.",
                "Role Completed",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Calculate max possible rehearsal bonus based on budget
        int budget = 0;
        boolean isExtraRole = true;
        
        // Determine the appropriate budget and role type
        for (GameController.RoleViewModel role : scene.getStarringRoles()) {
            if (role.getName().equals(player.getCurrentRole())) {
                budget = scene.getBudget();
                isExtraRole = false;
                break;
            }
        }
        
        if (isExtraRole) {
            // Set a default budget for extra roles
            budget = 3; // This is a simplification - ideally we'd get the actual extra role budget
        }
        
        // Check if already at maximum rehearsal bonus
        if (oldBonus >= budget - 1) {
            JOptionPane.showMessageDialog(parentView,
                "You already have the maximum rehearsal bonus (+" + oldBonus + ") for this scene.\n" +
                "You should try acting now!",
                "Maximum Bonus Reached",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Attempt to rehearse
        boolean success = controller.rehearse();
        
        if (success) {
            // Get new bonus
            int newBonus = controller.getCurrentPlayerViewModel().getRehearsalBonus();
            
            JOptionPane.showMessageDialog(parentView,
                "Rehearsal successful! Your bonus increased from +" + oldBonus + " to +" + newBonus + ".",
                "Rehearsal Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // End turn after successful rehearsing
            handleEndTurn();
        } else {
            JOptionPane.showMessageDialog(parentView,
                "Unable to rehearse. This may be because you've reached the maximum bonus\n" +
                "or the scene has been completed.",
                "Cannot Rehearse",
                JOptionPane.WARNING_MESSAGE);
            // Do not end turn on failed rehearsal
        }
    }
    
    /**
     * Handles the MOVE button click
     */
    private void handleMove() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        // Check if player has a role that isn't completed
        if (player.getCurrentRole() != null && !isCurrentRoleCompleted()) {
            int response = JOptionPane.showConfirmDialog(
                parentView,
                "Moving will cause you to abandon your current role. Continue?",
                "Abandon Role?",
                JOptionPane.YES_NO_OPTION
            );
            
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        List<String> adjacentRooms = controller.getAdjacentRooms();
        
        if (adjacentRooms.isEmpty()) {
            JOptionPane.showMessageDialog(parentView, 
                "No adjacent rooms to move to.",
                "Cannot Move", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create an array of formatted room names for the dropdown
        String[] roomOptions = adjacentRooms.toArray(new String[0]);
        
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
            boolean success = controller.movePlayer(destination);
            
            if (success) {
                JOptionPane.showMessageDialog(parentView,
                    "Successfully moved to " + destination + ".",
                    "Move Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // End turn after successful moving
                handleEndTurn();
            } else {
                JOptionPane.showMessageDialog(parentView,
                    "Failed to move to " + destination + ".",
                    "Move Failed",
                    JOptionPane.ERROR_MESSAGE);
                // Do not end turn on failed move
            }
        }
    }
    
    /**
     * Handles the TAKE ROLE button click
     */
    private void handleTakeRole() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        // Check if player already has a role
        if (player.getCurrentRole() != null) {
            // Check if the current role is completed
            boolean isRoleActed = isCurrentRoleCompleted();
            
            if (!isRoleActed) {
                int response = JOptionPane.showConfirmDialog(
                    parentView, 
                    "You already have the role: " + player.getCurrentRole() + 
                    "\nDo you want to abandon it and take a new role?",
                    "Abandon Current Role?",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            // If role is completed, allow taking a new role without confirmation
        }
        
        // Get available roles
        List<RoleViewModel> availableRoles = controller.getAvailableRoles();
        
        // If no roles available
        if (availableRoles.isEmpty()) {
            JOptionPane.showMessageDialog(parentView, 
                "No roles available for your rank in this location.",
                "Cannot Take Role", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create an array of role descriptions for the dropdown
        String[] roleOptions = new String[availableRoles.size()];
        for (int i = 0; i < availableRoles.size(); i++) {
            RoleViewModel role = availableRoles.get(i);
            String type = controller.getCurrentSceneViewModel().getStarringRoles().stream()
                .anyMatch(r -> r.getName().equals(role.getName())) ? "Starring" : "Extra";
            
            roleOptions[i] = role.getName() + " (Rank " + role.getRank() + ", " + type + ") - \"" + role.getLine() + "\"";
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
            
            boolean success = controller.takeRole(selectedRole);
            
            if (success) {
                JOptionPane.showMessageDialog(parentView,
                    "Successfully took role: " + selectedRole,
                    "Role Taken",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // End turn after successfully taking a role
                handleEndTurn();
            } else {
                JOptionPane.showMessageDialog(parentView,
                    "Failed to take role: " + selectedRole + "\n" +
                    "The role may have already been taken or completed.",
                    "Take Role Failed",
                    JOptionPane.ERROR_MESSAGE);
                // Do not end turn on failed role taking
            }
        }
    }
    
    /**
     * Handles the ABANDON ROLE button click
     */
    private void handleAbandonRole() {
        PlayerViewModel player = controller.getCurrentPlayerViewModel();
        
        if (player.getCurrentRole() == null) {
            JOptionPane.showMessageDialog(parentView,
                "You don't currently have a role to abandon.",
                "No Role",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String roleName = player.getCurrentRole();
        boolean isCompleted = isCurrentRoleCompleted();
        
        int response = JOptionPane.showConfirmDialog(
            parentView,
            "Do you want to abandon your role as " + roleName + "?" +
            (isCompleted ? "\n(This role is already completed)" : ""),
            "Abandon Role?",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Abandon the role using the controller
        // We need to add a dedicated method for this in GameController
        boolean success = abandonPlayerRole();
        
        if (success) {
            JOptionPane.showMessageDialog(parentView,
                "Successfully abandoned role: " + roleName,
                "Role Abandoned",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Update UI but don't end turn - abandoning a role is a free action
            updateButtonStates();
        } else {
            JOptionPane.showMessageDialog(parentView,
                "Failed to abandon role. This is likely a bug.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean abandonPlayerRole() {
        return controller.abandonRole();
    }
    /**
     * Handles the VIEW SCENE button click
     */
    private void handleViewScene() {
        GameController.SceneViewModel scene = controller.getCurrentSceneViewModel();
        
        if (scene == null || !scene.isActive()) {
            JOptionPane.showMessageDialog(parentView, 
                "No active scene in this location.", 
                "No Scene Available", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create detailed scene information
        StringBuilder sceneInfo = new StringBuilder();
        sceneInfo.append("Scene ").append(scene.getSceneId()).append(": ")
            .append(scene.getSceneName()).append("\n\n");
        sceneInfo.append("Budget: $").append(scene.getBudget()).append("\n");
        sceneInfo.append("Shots Remaining: ").append(scene.getShotsRemaining()).append("\n\n");
        sceneInfo.append("Description: ").append(scene.getDescription()).append("\n\n");
        
        // Add starring roles
        sceneInfo.append("Starring Roles:\n");
        for (RoleViewModel role : scene.getStarringRoles()) {
            String status = "";
            if (role.isActed()) {
                status = " [COMPLETED]";
            } else if (role.isTaken()) {
                status = " [TAKEN]";
            } else if (role.getRank() > controller.getCurrentPlayerViewModel().getRank()) {
                status = " [RANK TOO LOW]";
            }
            
            sceneInfo.append("  - ").append(role.getName())
                .append(" (Rank ").append(role.getRank()).append(")")
                .append(status).append("\n");
            sceneInfo.append("    Line: \"").append(role.getLine()).append("\"\n");
        }
        
        // Add extra roles if available
        if (!scene.getExtraRoles().isEmpty()) {
            sceneInfo.append("\nExtra Roles:\n");
            for (RoleViewModel role : scene.getExtraRoles()) {
                String status = "";
                if (role.isActed()) {
                    status = " [COMPLETED]";
                } else if (role.isTaken()) {
                    status = " [TAKEN]";
                } else if (role.getRank() > controller.getCurrentPlayerViewModel().getRank()) {
                    status = " [RANK TOO LOW]";
                }
                
                sceneInfo.append("  - ").append(role.getName())
                    .append(" (Rank ").append(role.getRank()).append(")")
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
        helpText.append("• ABANDON ROLE: Leave your current role without taking another action\n");
        helpText.append("• UPGRADE: Increase your rank at the Casting Office\n\n");
        
        helpText.append("ACTING:\n");
        helpText.append("• Roll a die + rehearsal bonus\n");
        helpText.append("• If result >= budget: Success! Gain rewards and remove a shot marker\n");
        helpText.append("• If result < budget: Failure. No reward\n");
        helpText.append("• Once a role has been successfully acted, it's completed and cannot be acted again\n\n");
        
        helpText.append("REHEARSING:\n");
        helpText.append("• Each rehearsal adds +1 to future acting attempts\n");
        helpText.append("• Maximum rehearsal bonus = budget - 1\n\n");
        
        helpText.append("WRAPPING A SCENE:\n");
        helpText.append("• When all shots are complete, the scene wraps\n");
        helpText.append("• Players on roles receive bonus payments\n");
        helpText.append("• Starring roles divide dice equal to the budget\n");
        helpText.append("• Extra roles get dollars equal to their rank\n\n");
        
        helpText.append("COMPLETED ROLES:\n");
        helpText.append("• After successfully acting in a role, it's marked as completed\n");
        helpText.append("• You can't act or rehearse in a completed role\n");
        helpText.append("• You must either abandon the role or end your turn\n\n");
        
        helpText.append("GAME END:\n");
        helpText.append("• Game ends after a set number of days\n");
        helpText.append("• Final score = dollars + credits + (5 × rank)\n\n");
        
        helpText.append("CONTROL PANEL BUTTONS:\n");
        helpText.append("• ACT: Attempt to act in your current role\n");
        helpText.append("• REHEARSE: Practice for your current role (+1 bonus)\n");
        helpText.append("• MOVE: Travel to an adjacent room\n");
        helpText.append("• TAKE ROLE: Take an available role on the current set\n");
        helpText.append("• ABANDON ROLE: Leave your current role without moving\n");
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
        controller.endTurn();
    }
    
    // GameObserver methods
    @Override
    public void onGameStateChanged() {
        updateButtonStates();
    }
    
    @Override
    public void onPlayerChanged(PlayerViewModel player) {
        updateButtonStates();
    }
    
    @Override
    public void onSceneChanged(GameController.SceneViewModel scene) {
        updateButtonStates();
    }
    
    @Override
    public void onBoardChanged() {
        updateButtonStates();
    }
}