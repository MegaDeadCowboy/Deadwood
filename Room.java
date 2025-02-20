import java.util.List;

// Model, contains set, player
public class Room {
    private List<String> adjacentRooms;
    private int scenesCompleted;
    private Set set;
    protected String roomID;

    public Room(String roomID, List<String> adjacentRooms) {
        this.roomID = roomID;
        this.adjacentRooms = adjacentRooms;
        this.scenesCompleted = 0;
        this.set = null; // Set will be assigned later if applicable
    }
    
    public boolean isValidRoom() {
        return roomID != null && !roomID.isEmpty();
    }
    
    public boolean endDay() {
        return scenesCompleted >= 3; // Example: End day when 3 scenes are completed
    }
    
    public void completeScene() {
        if (set != null && set.isActive()) {
            scenesCompleted++;
            set = null; // Reset set after scene is completed
        }
    }

    // Set a new scene in the room
    public void assignSet(Set newSet) {
        this.set = newSet;
    }

    // Getters
    public Set getSet() {
        return set;
    }

    public List<String> getAdjacentRooms() {
        return adjacentRooms;
    }

    public String getRoomID() {
        return roomID;
    }
}
