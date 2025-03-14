package deadwood.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import deadwood.controller.GameController.PlayerViewModel;
import deadwood.controller.GameController.SceneViewModel;
import deadwood.controller.GameController.RoleViewModel;
import deadwood.controller.GameController.RoomViewModel;
import deadwood.controller.GameController.UpgradeViewModel;
import deadwood.controller.GameController.GameObserver;

/**
 * Adapter class to help views transition from direct model access to using the controller.
 * This class implements GameObserver and can be used by views to receive updates.
 */
public class ViewModelAdapter implements GameController.GameObserver {
    private GameController controller;
    private List<ViewUpdateListener> listeners;
    
    /**
     * Interface for views to receive updates
     */
    public interface ViewUpdateListener {
        void onViewModelUpdated();
    }
    
    public ViewModelAdapter(GameController controller) {
        this.controller = controller;
        this.listeners = new ArrayList<>();
        this.controller.registerObserver(this);
    }
    
    /**
     * Register a view to receive update notifications
     */
    public void registerListener(ViewUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Unregister a view from receiving updates
     */
    public void unregisterListener(ViewUpdateListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all registered listeners of an update
     */
    private void notifyListeners() {
        for (ViewUpdateListener listener : listeners) {
            listener.onViewModelUpdated();
        }
    }
    
    // GameObserver Implementation
    
    @Override
    public void onGameStateChanged() {
        notifyListeners();
    }
    
    @Override
    public void onPlayerChanged(PlayerViewModel player) {
        notifyListeners();
    }
    
    @Override
    public void onSceneChanged(SceneViewModel scene) {
        notifyListeners();
    }
    
    @Override
    public void onBoardChanged() {
        notifyListeners();
    }
    
    // Helper methods for views
    
    /**
     * Get current player information
     */
    public PlayerViewModel getCurrentPlayer() {
        return controller.getCurrentPlayerViewModel();
    }
    
    /**
     * Get current scene information
     */
    public SceneViewModel getCurrentScene() {
        return controller.getCurrentSceneViewModel();
    }
    
    /**
     * Get adjacent rooms for the current player
     */
    public List<String> getAdjacentRooms() {
        return controller.getAdjacentRooms();
    }
    
    /**
     * Get available roles for the current player
     */
    public List<RoleViewModel> getAvailableRoles() {
        return controller.getAvailableRoles();
    }
    
    /**
     * Get upgrade options for the current player
     */
    public List<UpgradeViewModel> getUpgradeOptions() {
        return controller.getUpgradeOptions();
    }
    
    /**
     * Move the current player to a new room
     */
    public boolean movePlayer(String destinationRoom) {
        return controller.movePlayer(destinationRoom);
    }
    
    /**
     * Have the current player take a role
     */
    public boolean takeRole(String roleName) {
        return controller.takeRole(roleName);
    }
    
    /**
     * Have the current player act in their role
     */
    public boolean act() {
        return controller.act();
    }
    
    /**
     * Have the current player rehearse for their role
     */
    public boolean rehearse() {
        return controller.rehearse();
    }
    
    /**
     * Upgrade the current player's rank
     */
    public boolean upgradeRank(int targetRank, String paymentType) {
        return controller.upgradeRank(targetRank, paymentType);
    }
    
    /**
     * End the current player's turn
     */
    public void endTurn() {
        controller.endTurn();
    }
    
    /**
     * Check if the current player can take roles
     */
    public boolean canTakeRoles() {
        return controller.canTakeRoles();
    }
}