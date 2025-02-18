
//Model, tracks player locations
public class PlayerLocation {
    private int playerID;
    private int roomID;
    private boolean onRole;
    // private room currentRoom ? should it have a room? or just id to find it?

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

    public room getCurrentRoom(int ID){
        //find room based on ID
        return room;
    }

    //getters
    public int getPlayerID() {
        return playerID;
    }

    public int getRoomID() {
        return roomID;
    }

    public boolean getOnRole() {
        return onRole;
    }
}