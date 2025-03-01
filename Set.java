import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


 // Model class that holds information about a set in a room.
public class Set {
    private RoleCard roleCard;
    private int shotCounter;
    private int extraRoles;
    private boolean isActive;
    private Map<String, String> takenRoles; 
    private HashSet<String> actedRoles; 
    
 
    public Set(RoleCard roleCard, int shotCounter, int extraRoles) {
        this.roleCard = roleCard;
        this.shotCounter = shotCounter;
        this.extraRoles = extraRoles;
        this.isActive = true; 
        this.takenRoles = new HashMap<>();
        this.actedRoles = new HashSet<>();
    }

    public boolean decrementShots() {
        --shotCounter;
        System.out.println("Shot completed! " + shotCounter + " shots remaining.");
        
        if (shotCounter <= 0) {
            return true;
        }
        return false;
    }
     
    public void markRoleAsActed(String roleName) {
        actedRoles.add(roleName);
    }
    
    public boolean hasRoleBeenActed(String roleName) {
        return actedRoles.contains(roleName);
    }
    

    public boolean isRoleAvailable(String roleName) {
        // First, check if the role exists
        boolean roleExists = false;
        
        return roleExists && !takenRoles.containsKey(roleName);
    }
    
    
    public boolean isRoleTaken(String roleName) {
        // Check if the role exists in the takenRoles map
        return takenRoles.containsKey(roleName);
    }
    
    
    public boolean assignRole(String roleName, String playerID) {
        if (isRoleAvailable(roleName)) {
            takenRoles.put(roleName, playerID);
            return true;
        }
        return false;
    }
    
    // If a player leaves a role
    public void releaseRole(String roleName) {
        takenRoles.remove(roleName);
    }
    
    
    public List<String> getAvailableRoles() {
        List<String> availableRoles = new ArrayList<>();
        
        return availableRoles;
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