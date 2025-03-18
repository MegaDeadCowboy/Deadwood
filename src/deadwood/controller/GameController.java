package deadwood.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import deadwood.model.*;

/**
 * The GameController class serves as the intermediary between the model and view components,
 * implementing the Observer pattern to notify views of changes to the game state.
 * It encapsulates all game logic that was previously contained in the view classes.
 */
public class GameController {
    private GameBoard gameBoard;
    private List<GameObserver> observers;
    
    /**
     * Constructor initializes a new game controller with a game board
     */
    public GameController(int numPlayers) {
        this.gameBoard = new GameBoard(numPlayers);
        this.observers = new ArrayList<>();
    }
    
    /**
     * Interface for objects that want to observe game state changes
     */
    public interface GameObserver {
        void onGameStateChanged();
        void onPlayerChanged(PlayerViewModel player);
        void onSceneChanged(SceneViewModel scene);
        void onBoardChanged();
    }
    
    /**
     * Register a new observer to receive game state change notifications
     */
    public void registerObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Unregister an observer so it no longer receives notifications
     */
    public void unregisterObserver(GameObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all registered observers of a game state change
     */
    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onGameStateChanged();
        }
    }
    
    /**
     * Notify all registered observers of a player change
     */
    private void notifyPlayerChanged(PlayerViewModel player) {
        for (GameObserver observer : observers) {
            observer.onPlayerChanged(player);
        }
    }
    
    /**
     * Notify all registered observers of a scene change
     */
    private void notifySceneChanged(SceneViewModel scene) {
        for (GameObserver observer : observers) {
            observer.onSceneChanged(scene);
        }
    }
    
    /**
     * Notify all registered observers of a board change
     */
    private void notifyBoardChanged() {
        for (GameObserver observer : observers) {
            observer.onBoardChanged();
        }
    }
    
    // ViewModel classes to provide data to the views without exposing model details
    
    /**
     * ViewModel for player information
     */
    public static class PlayerViewModel {
        private int playerId;
        private int cash;
        private int credits;
        private int rank;
        private String currentRole;
        private String currentLocation;
        private int rehearsalBonus;
        private boolean roleIsExtra;
        private String roleLine;
        private int roleRank;
        
        // Getters
        public int getPlayerId() { return playerId; }
        public int getCash() { return cash; }
        public int getCredits() { return credits; }
        public int getRank() { return rank; }
        public String getCurrentRole() { return currentRole; }
        public String getCurrentLocation() { return currentLocation; }
        public int getRehearsalBonus() { return rehearsalBonus; }
        public boolean isRoleExtra() { return roleIsExtra; }
        public String getRoleLine() { return roleLine; }
        public int getRoleRank() { return roleRank; }
    }
    
    /**
     * ViewModel for scene information
     */
    public static class SceneViewModel {
        private String sceneName;
        private int sceneId;
        private int budget;
        private int shotsRemaining;
        private String description;
        private boolean isActive;
        private List<RoleViewModel> starringRoles;
        private List<RoleViewModel> extraRoles;
        
        public SceneViewModel() {
            this.starringRoles = new ArrayList<>();
            this.extraRoles = new ArrayList<>();
        }
        
        // Getters
        public String getSceneName() { return sceneName; }
        public int getSceneId() { return sceneId; }
        public int getBudget() { return budget; }
        public int getShotsRemaining() { return shotsRemaining; }
        public String getDescription() { return description; }
        public boolean isActive() { return isActive; }
        public List<RoleViewModel> getStarringRoles() { return starringRoles; }
        public List<RoleViewModel> getExtraRoles() { return extraRoles; }
    }
    
    /**
     * ViewModel for role information
     */
    public static class RoleViewModel {
        private String name;
        private int rank;
        private String line;
        private boolean isTaken;
        private boolean isActed;
        
        // Getters
        public String getName() { return name; }
        public int getRank() { return rank; }
        public String getLine() { return line; }
        public boolean isTaken() { return isTaken; }
        public boolean isActed() { return isActed; }
    }
    
    /**
     * ViewModel for room information
     */
    public static class RoomViewModel {
        private String roomId;
        private List<String> adjacentRooms;
        private boolean hasActiveScene;
        
        public RoomViewModel() {
            this.adjacentRooms = new ArrayList<>();
        }
        
        // Getters
        public String getRoomId() { return roomId; }
        public List<String> getAdjacentRooms() { return adjacentRooms; }
        public boolean hasActiveScene() { return hasActiveScene; }
    }
    
    /**
     * ViewModel for upgrade options in the casting office
     */
    public static class UpgradeViewModel {
        private int targetRank;
        private int cashCost;
        private int creditCost;
        
        // Getters
        public int getTargetRank() { return targetRank; }
        public int getCashCost() { return cashCost; }
        public int getCreditCost() { return creditCost; }
    }
    /**
 * Add these inner classes to your GameController class right after the 
 * other ViewModel classes, before the methods.
 */

/**
 * ViewModel for a player token on the board
 */
public static class PlayerTokenViewModel {
    private int playerId;
    private String roomId;
    private String diceColor;
    private boolean isCurrentPlayer;
    
    // Getters
    public int getPlayerId() { return playerId; }
    public String getRoomId() { return roomId; }
    public String getDiceColor() { return diceColor; }
    public boolean isCurrentPlayer() { return isCurrentPlayer; }
}

/**
 * ViewModel for a shot counter on the board
 */
public static class ShotCounterViewModel {
    private String roomId;
    private int shotsRemaining;
    private boolean isVisible;
    
    // Getters
    public String getRoomId() { return roomId; }
    public int getShotsRemaining() { return shotsRemaining; }
    public boolean isVisible() { return isVisible; }
}

    /**
     * ViewModel for the game board
     */
    public static class BoardViewModel {
        private List<PlayerTokenViewModel> playerTokens;
        private List<ShotCounterViewModel> shotCounters;
        int currentPlayerId;
        
        public BoardViewModel() {
            this.playerTokens = new ArrayList<>();
            this.shotCounters = new ArrayList<>();
        }
        
        // Getters
        public List<PlayerTokenViewModel> getPlayerTokens() { return playerTokens; }
        public List<ShotCounterViewModel> getShotCounters() { return shotCounters; }
        public int getCurrentPlayerId() { return currentPlayerId; }
    }
    
    /**
     * Create a PlayerViewModel for the current player
     */
    public PlayerViewModel getCurrentPlayerViewModel() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return null;
        }
        
        PlayerViewModel viewModel = new PlayerViewModel();
        
        // Set basic player information
        viewModel.playerId = currentPlayer.getPlayerID();
        viewModel.cash = currentPlayer.getPoints().getPlayerCash();
        viewModel.credits = currentPlayer.getPoints().getPlayerCredit();
        viewModel.rank = currentPlayer.getCurrentRank();
        viewModel.currentRole = currentPlayer.getCurrentRole();
        viewModel.rehearsalBonus = currentPlayer.getPoints().getRehearsalBonus();
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom != null) {
            viewModel.currentLocation = currentRoom.getRoomID();
            
            // Get role details if player has a role
            if (currentPlayer.getCurrentRole() != null) {
                Set currentSet = currentRoom.getSet();
                if (currentSet != null) {
                    viewModel.roleIsExtra = currentSet.isExtraRole(currentPlayer.getCurrentRole());
                    
                    RoleCard.Role role = currentSet.getRole(currentPlayer.getCurrentRole());
                    if (role != null) {
                        viewModel.roleLine = role.getLine();
                        viewModel.roleRank = role.getLevel();
                    }
                }
            }
        }
        
        return viewModel;
    }
  
    public List<PlayerViewModel> getAllPlayersViewModels() {
        List<PlayerViewModel> playerViewModels = new ArrayList<>();
        
        // Get all players from the game board
        List<Actor> allPlayers = gameBoard.getAllPlayers();
        
        // Convert each player to a ViewModel
        for (Actor player : allPlayers) {
            PlayerViewModel viewModel = new PlayerViewModel();
            
            // Set basic player information
            viewModel.playerId = player.getPlayerID();
            viewModel.cash = player.getPoints().getPlayerCash();
            viewModel.credits = player.getPoints().getPlayerCredit();
            viewModel.rank = player.getCurrentRank();
            viewModel.currentRole = player.getCurrentRole();
            viewModel.rehearsalBonus = player.getPoints().getRehearsalBonus();
            
            Room currentRoom = player.getLocation().getCurrentRoom();
            if (currentRoom != null) {
                viewModel.currentLocation = currentRoom.getRoomID();
                
                // Get role details if player has a role
                if (player.getCurrentRole() != null) {
                    Set currentSet = currentRoom.getSet();
                    if (currentSet != null) {
                        viewModel.roleIsExtra = currentSet.isExtraRole(player.getCurrentRole());
                        
                        RoleCard.Role role = currentSet.getRole(player.getCurrentRole());
                        if (role != null) {
                            viewModel.roleLine = role.getLine();
                            viewModel.roleRank = role.getLevel();
                        }
                    }
                }
            }
            
            playerViewModels.add(viewModel);
        }
        
        return playerViewModels;
    }
    /**
     * Create a SceneViewModel for the current scene
     */
    public SceneViewModel getCurrentSceneViewModel() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return null;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            return null;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null) {
            return null;
        }
        
        SceneViewModel viewModel = new SceneViewModel();
        
        // Set scene information
        viewModel.isActive = currentSet.isActive();
        viewModel.shotsRemaining = currentSet.getShotCounter();
        
        RoleCard roleCard = currentSet.getRoleCard();
        if (roleCard != null) {
            viewModel.sceneName = roleCard.getSceneName();
            viewModel.sceneId = roleCard.getSceneID();
            viewModel.budget = roleCard.getSceneBudget();
            viewModel.description = roleCard.getSceneDescription();
            
            // Add starring roles
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                RoleViewModel roleVM = new RoleViewModel();
                roleVM.name = role.getName();
                roleVM.rank = role.getLevel();
                roleVM.line = role.getLine();
                roleVM.isTaken = currentSet.isRoleTaken(role.getName());
                roleVM.isActed = currentSet.hasRoleBeenActed(role.getName());
                viewModel.starringRoles.add(roleVM);
            }
        }
        
        // Add extra roles
        RoleCard extraRolesCard = currentSet.getExtraRolesCard();
        if (extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                RoleViewModel roleVM = new RoleViewModel();
                roleVM.name = role.getName();
                roleVM.rank = role.getLevel();
                roleVM.line = role.getLine();
                roleVM.isTaken = currentSet.isRoleTaken(role.getName());
                roleVM.isActed = currentSet.hasRoleBeenActed(role.getName());
                viewModel.extraRoles.add(roleVM);
            }
        }
        
        return viewModel;
    }
    
    /**
     * Get a list of adjacent rooms for the current player
     */
    public List<String> getAdjacentRooms() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return new ArrayList<>();
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            return new ArrayList<>();
        }
        
        return currentRoom.getAdjacentRooms();
    }
        /**
     * Get a ViewModel for the game board
     */
    public BoardViewModel getBoardViewModel() {
        BoardViewModel viewModel = new BoardViewModel();
        
        // Get all players
        List<Actor> allPlayers = gameBoard.getAllPlayers();
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        int currentPlayerId = currentPlayer.getPlayerID();
        viewModel.currentPlayerId = currentPlayerId;
        
        // Create player tokens
        String[] diceColors = {"b", "c", "g", "o", "p", "r", "v", "w", "y"};
        
        for (Actor player : allPlayers) {
            PlayerTokenViewModel tokenVM = new PlayerTokenViewModel();
            tokenVM.playerId = player.getPlayerID();
            
            Room playerRoom = player.getLocation().getCurrentRoom();
            if (playerRoom != null) {
                tokenVM.roomId = playerRoom.getRoomID().toLowerCase();
            }
            
            // Assign dice color
            int colorIndex = (player.getPlayerID() - 1) % diceColors.length;
            tokenVM.diceColor = diceColors[colorIndex];
            
            // Check if current player
            tokenVM.isCurrentPlayer = (player.getPlayerID() == currentPlayerId);
            
            viewModel.playerTokens.add(tokenVM);
        }
        
        // Create shot counters for scene rooms
        for (String roomId : PlayerLocation.getSceneRoomIDs()) {
            Room room = gameBoard.getRoomByID(roomId);
            if (room == null) {
                continue;
            }
            
            ShotCounterViewModel counterVM = new ShotCounterViewModel();
            counterVM.roomId = roomId.toLowerCase();
            
            Set set = room.getSet();
            if (set != null && set.isActive()) {
                counterVM.shotsRemaining = set.getShotCounter();
                counterVM.isVisible = true;
            } else {
                counterVM.shotsRemaining = 0;
                counterVM.isVisible = false;
            }
            
            viewModel.shotCounters.add(counterVM);
        }
        
        return viewModel;
    }
    
    /**
     * Move the current player to a new room
     * Only allows movement if player has no role or has a completed role
     */
    public boolean movePlayer(String destinationRoom) {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            return false;
        }
        
        // Check if player has an incomplete role - can't move
        if (currentPlayer.getCurrentRole() != null && !currentPlayer.isCurrentRoleCompleted()) {
            System.out.println("You cannot move while working on an incomplete role. You must act or rehearse.");
            return false;
        }
        
        // If player has a completed role, abandon it before moving
        if (currentPlayer.getCurrentRole() != null) {
            currentPlayer.abandonRole();
        }
        
        // Validate the move
        boolean isValid = gameBoard.validatePlayerMove(currentRoom.getRoomID(), destinationRoom);
        if (!isValid) {
            return false;
        }
        
        // Move the player
        boolean success = currentPlayer.inputMove(destinationRoom, gameBoard);
        
        if (success) {
            notifyObservers();
            notifyPlayerChanged(getCurrentPlayerViewModel());
            notifyBoardChanged();
        }
        
        return success;
    }
    /**
     * Have the current player take a role
     * Only allows taking a role if player has no role or has a completed role
     */
    public boolean takeRole(String roleName) {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }
        
        // Check if player has an incomplete role
        if (currentPlayer.getCurrentRole() != null && !currentPlayer.isCurrentRoleCompleted()) {
            System.out.println("You cannot take a new role while working on an incomplete role. You must act or rehearse.");
            return false;
        }
        
        // Get the current room and set
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            System.out.println("Error: Player is not in a valid room.");
            return false;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            System.out.println("There is no active set in this room.");
            return false;
        }
        
        // Check if this role has already been acted
        if (currentSet.hasRoleBeenActed(roleName)) {
            System.out.println("This role has already been completed and cannot be taken.");
            return false;
        }
        
        // If player has a completed role, abandon it first
        if (currentPlayer.getCurrentRole() != null) {
            currentPlayer.abandonRole();
        }
        
        boolean success = currentPlayer.inputRole(roleName);
        
        if (success) {
            notifyObservers();
            notifyPlayerChanged(getCurrentPlayerViewModel());
            notifySceneChanged(getCurrentSceneViewModel());
        }
        
        return success;
    }
   /**
     * Have the current player abandon their role
     */
    public boolean abandonRole() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }
        
        // Check if player has a role to abandon
        if (currentPlayer.getCurrentRole() == null) {
            return false;
        }
        
        boolean success = currentPlayer.abandonRole();
        
        if (success) {
            notifyObservers();
            notifyPlayerChanged(getCurrentPlayerViewModel());
            notifySceneChanged(getCurrentSceneViewModel());
        }
        
        return success;
    }
    
    /**
     * Have the current player act in their role
     */
    public boolean act() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }
        
        // Check if player has a role
        if (currentPlayer.getCurrentRole() == null) {
            System.out.println("You don't have a role to act in.");
            return false;
        }
        
        // Try to act and capture result
        boolean success = currentPlayer.inputAttemptScene(gameBoard);
        
        // Always notify observers, regardless of success/failure
        notifyObservers();
        notifyPlayerChanged(getCurrentPlayerViewModel());
        notifySceneChanged(getCurrentSceneViewModel());
        notifyBoardChanged();
        
        // Return success status without ending turn on failure
        return success;
    }
        
    /**
 * Have the current player rehearse for their role
    */
    public boolean rehearse() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }
        
        // Check if player has a role
        if (currentPlayer.getCurrentRole() == null) {
            System.out.println("You don't have a role to rehearse for.");
            return false;
        }
        
        // Try to rehearse and capture the result
        boolean success = currentPlayer.inputRehearse();
        
        // Always notify observers of any state changes, regardless of success/failure
        notifyObservers();
        notifyPlayerChanged(getCurrentPlayerViewModel());
        
        // Return success status without ending turn on failure
        return success;
    }
    
    /**
     * Upgrade the current player's rank
     */
    public boolean upgradeRank(int targetRank, String paymentType) {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }
        
        // Check if in casting office
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (!(currentRoom instanceof CastingOffice)) {
            System.out.println("You must be in the Casting Office to upgrade.");
            return false;
        }
        
        // Validate payment type before calling actor method
        if (paymentType == null || (!paymentType.equalsIgnoreCase("cash") && !paymentType.equalsIgnoreCase("credit"))) {
            System.out.println("Invalid payment type. Use 'cash' or 'credit'.");
            return false;
        }
        
        // Try to upgrade and capture the result
        boolean success = currentPlayer.inputUpgrade(targetRank, paymentType);
        
        // Always notify observers of any state changes, regardless of success/failure
        notifyObservers();
        notifyPlayerChanged(getCurrentPlayerViewModel());
        
        // Return success status without ending turn on failure
        return success;
    }
    
    /**
     * End the current player's turn
     */
    public void endTurn() {
        gameBoard.endTurn();
        notifyObservers();
        notifyPlayerChanged(getCurrentPlayerViewModel());
        notifyBoardChanged();
    }
    
    /**
     * Get upgrade options for the casting office
     */
    public List<UpgradeViewModel> getUpgradeOptions() {
        List<UpgradeViewModel> options = new ArrayList<>();
        
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return options;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (!(currentRoom instanceof CastingOffice)) {
            return options;
        }
        
        CastingOffice office = (CastingOffice) currentRoom;
        int currentRank = currentPlayer.getCurrentRank();
        
        // Create upgrade options for ranks 2-6
        for (int rank = 2; rank <= 6; rank++) {
            if (rank <= currentRank) {
                continue; // Skip ranks player already has
            }
            
            UpgradeViewModel option = new UpgradeViewModel();
            option.targetRank = rank;
            
            // For simplicity, we're using default upgrade costs here
            // In a complete implementation, you'd get these from the CastingOffice
            switch (rank) {
                case 2:
                    option.cashCost = 4;
                    option.creditCost = 5;
                    break;
                case 3:
                    option.cashCost = 10;
                    option.creditCost = 10;
                    break;
                case 4:
                    option.cashCost = 18;
                    option.creditCost = 15;
                    break;
                case 5:
                    option.cashCost = 28;
                    option.creditCost = 20;
                    break;
                case 6:
                    option.cashCost = 40;
                    option.creditCost = 25;
                    break;
            }
            
            options.add(option);
        }
        
        return options;
    }
    
    /**
     * Check if the current player can take roles in the current location
     */
    public boolean canTakeRoles() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.getCurrentRole() != null) {
            return false;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            return false;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null || !currentSet.isActive()) {
            return false;
        }
        
        // Check if there are roles available for the player's rank
        List<RoleCard.Role> availableRoles = currentPlayer.getAvailableRoles();
        return availableRoles != null && !availableRoles.isEmpty();
    }
    
    /**
     * Get available roles for the current player at their current location
     */
    public List<RoleViewModel> getAvailableRoles() {
        List<RoleViewModel> availableRoles = new ArrayList<>();
        
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        if (currentPlayer == null) {
            return availableRoles;
        }
        
        List<RoleCard.Role> roles = currentPlayer.getAvailableRoles();
        if (roles == null) {
            return availableRoles;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        if (currentRoom == null) {
            return availableRoles;
        }
        
        Set currentSet = currentRoom.getSet();
        if (currentSet == null) {
            return availableRoles;
        }
        
        // Convert model roles to view model roles
        for (RoleCard.Role role : roles) {
            RoleViewModel viewModel = new RoleViewModel();
            viewModel.name = role.getName();
            viewModel.rank = role.getLevel();
            viewModel.line = role.getLine();
            viewModel.isTaken = false; // Already filtered to available roles
            viewModel.isActed = currentSet.hasRoleBeenActed(role.getName());
            
            availableRoles.add(viewModel);
        }
        
        return availableRoles;
    }
    
    /**
     * Get the GameBoard for methods that still need direct access
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }
    
}