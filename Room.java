import java.util.ArrayList;
import java.util.List;

public abstract class Room {
    protected String roomID;
    protected List<String> adjacentRooms;
    protected Set set;
    
    public Room(String roomID, List<String> adjacentRooms) {
        this.roomID = roomID;
        this.adjacentRooms = adjacentRooms != null ? adjacentRooms : new ArrayList<>();
        this.set = null;
    }
    
    public String getRoomID() {
        return roomID;
    }
    
    public List<String> getAdjacentRooms() {
        return adjacentRooms;
    }
    
    public Set getSet() {
        return set;
    }
    
    public void assignSet(Set set) {
        this.set = set;
    }
    
    public boolean isValidRoom() {
        return roomID != null && !roomID.isEmpty();
    }
    
    public void completeScene() {
        if (set != null && set.isActive()) {
            set = null; // Remove set after scene is completed
        }
    }
}