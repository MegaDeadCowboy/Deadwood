import java.util.List;

//Model, contains set, player
public class Room {
    private String roomID;
    private List<String> adjacentRooms;
    private int scenesCompleted;
    private Set set;
    
    public boolean isValidRoom() {
        // Implementation
        return false;
    }
    
    public boolean endDay() {
        // Implementation
        return false;
    }


    //getters
    public Set getSet(){
        return set;
    }
}