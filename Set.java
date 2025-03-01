import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class that holds information about a set in a room.
 * A set contains a RoleCard with scene information and available roles.
 */
public class Set {
    private RoleCard roleCard;
    private int shotCounter;
    private int extraRoles;
    private boolean isActive;
    private Map<String, String> takenRoles; // Maps role name to player ID
    
    /**
     * Constructor for a new Set
     * @param roleCard The RoleCard containing scene and role information
     * @param shotCounter Number of shots needed to complete the scene
     * @param extraRoles Number of extra (off-card) roles available
     */
    public Set(RoleCard roleCard, int shotCounter, int extraRoles) {
        this.roleCard = roleCard;
        this.shotCounter = shotCounter;
        this.extraRoles = extraRoles;
        this.isActive = true; // Default to active when created
        this.takenRoles = new HashMap<>();
    }
    
    /**
     * Decrements the shot counter. If it reaches zero, the scene is wrapped.
     */
    /**
     * Decrements the shot counter. If it reaches zero, the scene is wrapped.
     * @return true if the scene is now wrapped (shots = 0), false otherwise
     */
    public boolean decrementShots() {
        --shotCounter;
        System.out.println("Shot completed! " + shotCounter + " shots remaining.");
        
        if (shotCounter <= 0) {
            wrapScene();
            return true;
        }
        
        return false;
    }
    
    /**
     * Completes the scene, sets it to inactive, and awards bonuses
     */
    private void wrapScene() {
        System.out.println("That's a wrap! Scene " + roleCard.getSceneID() + " completed.");
        awardSceneBonus();
        isActive = false; // Mark scene as completed
    }
    
    /**
     * Awards bonuses to players when a scene wraps
     */
    private void awardSceneBonus() {
        System.out.println("Scene bonus awarded!");
        // This would typically involve:
        // 1. Rolling dice equal to the scene budget
        // 2. Distributing bonuses to players in starring roles
        // 3. Awarding smaller bonuses to players in extra roles
    }
    
    /**
     * Checks if a role is available to be taken
     * @param roleName Name of the role to check
     * @return true if the role is available, false otherwise
     */
    public boolean isRoleAvailable(String roleName) {
        // First, check if the role exists
        boolean roleExists = false;
        
        // Look through scene roles (check your RoleCard implementation)
        // This is a placeholder - adapt to your actual RoleCard implementation
        
        // Then check if it's already taken
        return roleExists && !takenRoles.containsKey(roleName);
    }
    
    /**
     * Assigns a role to a player
     * @param roleName Name of the role
     * @param playerID ID of the player taking the role
     * @return true if successful, false otherwise
     */
    public boolean assignRole(String roleName, String playerID) {
        if (isRoleAvailable(roleName)) {
            takenRoles.put(roleName, playerID);
            return true;
        }
        return false;
    }
    
    /**
     * Releases a role (e.g., when a player leaves)
     * @param roleName Name of the role to release
     */
    public void releaseRole(String roleName) {
        takenRoles.remove(roleName);
    }
    
    /**
     * Gets a list of all available roles
     * @return List of role names that can be taken
     */
    public List<String> getAvailableRoles() {
        List<String> availableRoles = new ArrayList<>();
        
        // Again, this depends on your RoleCard implementation
        // This is a placeholder - adapt to your actual RoleCard implementation
        
        return availableRoles;
    }

    // Add this method to the Set class

    /**
     * Check if a role is already taken by a player
     * @param roleName The name of the role to check
     * @return true if the role is taken, false otherwise
     */
    public boolean isRoleTaken(String roleName) {
        // Check if the role exists in the takenRoles map
        return takenRoles.containsKey(roleName);
    }

    
    
    // Getters
    public RoleCard getRoleCard() {
        return roleCard;
    }

    public int getShotCounter() {
        return shotCounter;
    }

    public int getExtraRoles() {
        return extraRoles;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public Map<String, String> getTakenRoles() {
        return takenRoles;
    }
    
    /**
     * Returns a string representation of the set
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Set for Scene ").append(roleCard.getSceneID());
        sb.append(" (").append(shotCounter).append(" shots remaining)\n");
        sb.append("Scene: ").append(roleCard.getSceneName()).append("\n");
        sb.append("Budget: $").append(roleCard.getSceneBudget()).append("\n");
        
        // List roles - this depends on your RoleCard implementation
        sb.append("Roles:\n");
        
        // Taken roles
        if (!takenRoles.isEmpty()) {
            sb.append("Taken Roles:\n");
            for (Map.Entry<String, String> entry : takenRoles.entrySet()) {
                sb.append("  - ").append(entry.getKey());
                sb.append(" (taken by Player ").append(entry.getValue()).append(")\n");
            }
        }
        
        return sb.toString();
    }
}