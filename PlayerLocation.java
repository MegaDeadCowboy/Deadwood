
//Model, tracks player locations
public class PlayerLocation {
    private int playerID;
    private int roomID;
    private boolean onRole;
    private Room currentRoom;

    //constructor
    public PlayerLocation(int ID) {
        playerID = this.playerID;
        roomID = this.roomID;
        onRole = this.onRole;

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