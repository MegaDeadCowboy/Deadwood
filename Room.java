import java.util.List;

public interface Room {
    String getRoomID();
    List<String> getAdjacentRooms();
    Set getSet();
    void assignSet(Set set);
    boolean isValidRoom();
    void completeScene();
}