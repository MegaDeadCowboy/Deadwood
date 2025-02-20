
//Model, tracks player locations
// Fixing constructor in PlayerLocation.java
public class PlayerLocation {
    private int playerID;
    private int roomID;
    private boolean onRole;
    private Room currentRoom;

    public PlayerLocation(int ID) {
        this.playerID = ID;
        this.roomID = -1; // Default to an invalid room until assigned
        this.onRole = false;
        this.currentRoom = null;
    }
    
    public boolean validatePlayerMove(String roomID) {
        // Implementation
        return false;
    }
    
    public void updatePlayerLocation(String roomID) {
        // Implementation
    }

    public Room getCurrentRoom(){
        return currentRoom;
    }

    //getters
    public int getPlayerID() {
        return playerID;
    }

    public int getRoomID() {
        return roomID;
    }

    public boolean getOnRole() {
        if (!onRole) {
            return false;
        }
        else{
            return true;
        }
         
    }
}