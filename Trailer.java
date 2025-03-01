import java.util.ArrayList;
import java.util.List;

// Starting room
public class Trailer extends Room {
    
    public Trailer() {
        super("Trailer", new ArrayList<>());
    }
    
    public Trailer(List<String> adjacentRooms) {
        super("Trailer", adjacentRooms);
        
    }
    
   // Used to reset players at the end of a day
    public void resetPlayerLocations(List<Actor> players, GameBoard gameBoard) {
        Room trailerRoom = gameBoard.getRoomByID("Trailer");
    
        // Did we lose the trailer?
        if (trailerRoom == null) {
            System.out.println("Error: Trailer room not found.");
            return;
        }
    
        for (Actor player : players) {
            player.getLocation().updatePlayerLocation(trailerRoom);
        }
        System.out.println("All players have been reset to the Trailer.");
    }
}