
//Start of game
import java.util.ArrayList;
import java.util.List;

public class Trailer extends Room {
    public Trailer() {
        super("Trailer", new ArrayList<>());
    }

    /**
     * Resets all player locations to the Trailer.
     * This method should be called at the end of the day.
     *
     * @param players List of all Actors in the game.
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