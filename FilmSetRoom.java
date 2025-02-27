import java.util.List;

/**
 * Standard room that can contain a film set
 */
public class FilmSetRoom extends BasicRoom {
    private int scenesCompleted;
    
    public FilmSetRoom(String roomID, List<String> adjacentRooms) {
        super(roomID, adjacentRooms);
        this.scenesCompleted = 0;
    }
    
    @Override
    public void completeScene() {
        super.completeScene();
        scenesCompleted++;
        System.out.println("Scene wrapped in " + roomID);
    }
    
    /**
     * Check if enough scenes have been completed to end the day
     */
    public boolean endDay() {
        return scenesCompleted >= 3; // Example threshold
    }
}