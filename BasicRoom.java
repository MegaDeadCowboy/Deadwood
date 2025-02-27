import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of Room with common functionality
 */
public class BasicRoom implements Room {
    protected String roomID;
    protected List<String> adjacentRooms;
    protected Set set;
    
    public BasicRoom(String roomID, List<String> adjacentRooms) {
        this.roomID = roomID;
        this.adjacentRooms = adjacentRooms != null ? adjacentRooms : new ArrayList<>();
        this.set = null;
    }
    
    @Override
    public String getRoomID() {
        return roomID;
    }
    
    @Override
    public List<String> getAdjacentRooms() {
        return adjacentRooms;
    }
    
    @Override
    public Set getSet() {
        return set;
    }
    
    @Override
    public void assignSet(Set set) {
        this.set = set;
    }
    
    @Override
    public boolean isValidRoom() {
        return roomID != null && !roomID.isEmpty();
    }
    
    @Override
    public void completeScene() {
        if (set != null && set.isActive()) {
            set = null; // Remove set after scene is completed
        }
    }
}