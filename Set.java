
//Model, holds information about a set in a room
public class Set {
    private RoleCard roleCard;
    private int shotCounter;
    private int extraRoles;
    private boolean isActive;

    public Set(){
        this.roleCard = roleCard;
        this.shotCounter = shotCounter;
        this.extraRoles = extraRoles;
        this.isActive = isActive;
    }

    public void decrementShots(){
        --shotCounter;
        if (shotCounter == 0 ) {
            awardSceneBonus();
        }
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



