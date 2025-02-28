import java.util.ArrayList;
import java.util.List;

/**
 * Starting location for players
 */
public class Trailer extends BasicRoom {
    
    public Trailer() {
        super("Trailer", new ArrayList<>());
    }
    
    public Trailer(List<String> adjacentRooms) {
        super("Trailer", adjacentRooms);
        
    }
    
    /**
     * Reset all player locations to the Trailer at the start of a day
     */
    public void resetPlayerLocations(List<Actor> players, GameBoard gameBoard) {
        Room trailerRoom = gameBoard.getRoomByID("Trailer");
    
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