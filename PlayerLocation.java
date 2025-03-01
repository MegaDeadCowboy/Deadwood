
// Model, tracks player locations
import java.util.List;

public class PlayerLocation {
    private int playerID;
    private int roomID;
    private boolean onRole;
    private Room currentRoom;

    public PlayerLocation(int ID) {
        this.playerID = ID;
        this.roomID = -1;
        this.onRole = false;
        this.currentRoom = null;
    }
    
    // Methods
    public boolean validatePlayerMove(String destinationRoomID) {
        if (currentRoom == null) {
            System.out.println("Error: Player is not in a valid room.");
            return false;
        }

        List<String> adjacentRooms = currentRoom.getAdjacentRooms();
        if (adjacentRooms == null || !adjacentRooms.contains(destinationRoomID)) {
            System.out.println("Invalid move. You can only move to adjacent rooms.");
            return false;
        }
        return true;
    }
    
    public void updatePlayerLocation(Room newRoom) {
        if (newRoom == null) {
            System.out.println("Error: Cannot move to a null room.");
            return;
        }
        this.currentRoom = newRoom;
    }
    
    public Room getCurrentRoom(){
        return currentRoom;
    }

    // Getters
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
