import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


 // Model class that holds information about a set in a room.
public class Set {
    private RoleCard roleCard;            // Main scene card
    private RoleCard extraRolesCard;      // Extra roles card (for parts in the set)
    private int shotCounter;
    private int extraRoles;
    private boolean isActive;
    private Map<String, String> takenRoles; 
    private HashSet<String> actedRoles; 
    private int extraRoleBudget;          // Budget for extra roles
    
    public Set(RoleCard roleCard, int shotCounter, int extraRoles) {
        this.roleCard = roleCard;
        this.shotCounter = shotCounter;
        this.extraRoles = extraRoles;
        this.isActive = true; 
        this.takenRoles = new HashMap<>();
        this.actedRoles = new HashSet<>();
        this.extraRoleBudget = 1;         // Default budget for extra roles
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
        // First, check if the role exists (either in the main RoleCard or in extraRolesCard)
        boolean roleExists = false;
        
        // Check in main roleCard
        if (roleCard != null) {
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    roleExists = true;
                    break;
                }
            }
        }
        
        // If not found, check in extraRolesCard
        if (!roleExists && extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    roleExists = true;
                    break;
                }
            }
        }
        
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
        
        // Get roles from main roleCard
        if (roleCard != null) {
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                if (!takenRoles.containsKey(role.getName())) {
                    availableRoles.add(role.getName());
                }
            }
        }
        
        // Get roles from extraRolesCard
        if (extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                if (!takenRoles.containsKey(role.getName())) {
                    availableRoles.add(role.getName());
                }
            }
        }
        
        return availableRoles;
    }
    
    // Set the extra roles card
    public void setExtraRolesCard(RoleCard extraRolesCard) {
        this.extraRolesCard = extraRolesCard;
        
        // Set the extra role budget based on the level of the roles
        if (extraRolesCard != null && !extraRolesCard.getSceneRoles().isEmpty()) {
            // Calculate average rank of extra roles, use that as budget
            int totalRank = 0;
            int count = 0;
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                totalRank += role.getLevel();
                count++;
            }
            
            if (count > 0) {
                this.extraRoleBudget = Math.max(1, totalRank / count); // At least 1
            }
        }
    }
    
    // Get the extra roles card
    public RoleCard getExtraRolesCard() {
        return extraRolesCard;
    }
    
    // Get all roles (both scene and extra)
    public List<RoleCard.Role> getAllRoles() {
        List<RoleCard.Role> allRoles = new ArrayList<>();
        
        // Add roles from the main scene card
        if (roleCard != null) {
            allRoles.addAll(roleCard.getSceneRoles());
        }
        
        // Add roles from the extra roles card
        if (extraRolesCard != null) {
            allRoles.addAll(extraRolesCard.getSceneRoles());
        }
        
        return allRoles;
    }
    
    // Return role information
    public RoleCard.Role getRole(String roleName) {
        // Check in main roleCard first
        if (roleCard != null) {
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    return role;
                }
            }
        }
        
        // If not found, check in extraRolesCard
        if (extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    return role;
                }
            }
        }
        
        return null;
    }
    
    // Check if a role is an extra role
    public boolean isExtraRole(String roleName) {
        if (extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Get the budget for extra roles
    public int getExtraRoleBudget() {
        return extraRoleBudget;
    }
    
    // Set the budget for extra roles
    public void setExtraRoleBudget(int budget) {
        if (budget > 0) {
            this.extraRoleBudget = budget;
        }
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
        
        if (roleCard != null) {
            sb.append("Set for Scene ").append(roleCard.getSceneID());
            sb.append(" (").append(shotCounter).append(" shots remaining)\n");
            sb.append("Scene: ").append(roleCard.getSceneName()).append("\n");
            sb.append("Budget: $").append(roleCard.getSceneBudget()).append("\n");
        } else {
            sb.append("Set (").append(shotCounter).append(" shots)\n");
        }
        
        // List roles
        sb.append("Scene Roles:\n");
        if (roleCard != null) {
            for (RoleCard.Role role : roleCard.getSceneRoles()) {
                sb.append("  - ").append(role.getName());
                sb.append(" (Rank ").append(role.getLevel()).append("): \"");
                sb.append(role.getLine()).append("\"\n");
            }
        }
        
        // List extra roles
        sb.append("Extra Roles (Budget: ").append(extraRoleBudget).append("):\n");
        if (extraRolesCard != null) {
            for (RoleCard.Role role : extraRolesCard.getSceneRoles()) {
                sb.append("  - ").append(role.getName());
                sb.append(" (Rank ").append(role.getLevel()).append("): \"");
                sb.append(role.getLine()).append("\"\n");
            }
        }
        
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