
//Model, holds information about a set in a room
public class Set {
    private RoleCard roleCard;
    private int shotCounter;
    private int extraRoles;
    private boolean isActive;

    public Set(RoleCard roleCard, int shotCounter, int extraRoles) {
        this.roleCard = roleCard;
        this.shotCounter = shotCounter;
        this.extraRoles = extraRoles;
        this.isActive = true; // Default to active when created
    }

    public void decrementShots() {
        --shotCounter;
        if (shotCounter == 0) {
            awardSceneBonus();
            isActive = false; // Scene completed
        }
    }

    private void awardSceneBonus() {
        System.out.println("Scene bonus awarded!");
    }

    //getters
    public RoleCard getRoleCard(){
        return roleCard;
    }

    public int getShotCounter(){
        return shotCounter;
    }

    public int getExtraRoles(){
        return extraRoles;
    }

    public boolean isActive(){
        return isActive;
    }
}



