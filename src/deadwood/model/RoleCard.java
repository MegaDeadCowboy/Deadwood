package deadwood.model;

import java.util.ArrayList;
import java.util.List;



// Object holding role information
public class RoleCard {
    private int sceneID;
    private String sceneName;
    private String sceneDescription;
    private String cardImage;
    private int sceneBudget;
    private int totalShots;
    
    // List of roles available on this card
    private List<Role> sceneRoles;
    
    public static class Role {
        private String name;
        private int level;
        private String line;
        
        public Role(String name, int level, String line) {
            this.name = name;
            this.level = level;
            this.line = line;
        }
        
        // Role getters
        public String getName() {
            return name;
        }
        
        public int getLevel() {
            return level;
        }
        
        public String getLine() {
            return line;
        }
        
        @Override
        public String toString() {
            return name + " (Level " + level + "): \"" + line + "\"";
        }
    }
    
    public RoleCard() {
        this.sceneRoles = new ArrayList<>();
    }
    
    public RoleCard(int sceneID, String sceneName, String sceneDescription, 
                    String cardImage, int sceneBudget, int totalShots) {
        this.sceneID = sceneID;
        this.sceneName = sceneName;
        this.sceneDescription = sceneDescription;
        this.cardImage = cardImage;
        this.sceneBudget = sceneBudget;
        this.totalShots = totalShots;
        this.sceneRoles = new ArrayList<>();
    }
    
    
    public void addRole(String name, int level, String line) {
        sceneRoles.add(new Role(name, level, line));
    }
    
    
    public boolean validateRole(String roleName, int currentRank, int roleRank) {
        // First check if the role exists on this card
        boolean roleExists = false;
        for (Role role : sceneRoles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                roleExists = true;
                break;
            }
        }
        
        if (!roleExists) {
            return false;
        }
        
        // Check if player rank is sufficient
        if (currentRank >= roleRank){
            return true;
        }
        else {
            return false;
        }
    }

    public Role getRole(String roleName) {
        for (Role role : sceneRoles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }
    
    
    public int getRoleRank(String roleName) {
        Role role = getRole(roleName);
        return (role != null) ? role.getLevel() : -1;
    }
    
    // Role Card getters and setters
    public int getSceneID() {
        return sceneID;
    }
    
    public void setSceneID(int sceneID) {
        this.sceneID = sceneID;
    }
    
    public String getSceneName() {
        return sceneName;
    }
    
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    
    public String getSceneDescription() {
        return sceneDescription;
    }
    
    public void setSceneDescription(String sceneDescription) {
        this.sceneDescription = sceneDescription;
    }
    
    public String getCardImage() {
        return cardImage;
    }
    
    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }
    
    public int getSceneBudget() {
        return sceneBudget;
    }
    
    public void setSceneBudget(int sceneBudget) {
        this.sceneBudget = sceneBudget;
    }
    
    public int getTotalShots() {
        return totalShots;
    }
    
    public void setTotalShots(int totalShots) {
        this.totalShots = totalShots;
    }
    
    public List<Role> getSceneRoles() {
        return sceneRoles;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scene ").append(sceneID).append(": ").append(sceneName);
        sb.append(" (Budget: $").append(sceneBudget).append(")\n");
        sb.append("Description: ").append(sceneDescription).append("\n");
        sb.append("Roles:\n");
        for (Role role : sceneRoles) {
            sb.append("- ").append(role).append("\n");
        }
        return sb.toString();
    }
}