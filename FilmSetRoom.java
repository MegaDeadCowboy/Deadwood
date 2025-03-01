import java.util.List;

// Room child that can contain a set
public class FilmSetRoom extends Room {
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
   
    public boolean endDay() {
        return scenesCompleted >= 10;
    }
}