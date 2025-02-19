
//Start of game
import java.util.List;

public class Trailer extends Room {
    public Trailer() {
        super("Trailer"); // Inherit roomID from Room
    }

    /**
     * Resets all player locations to the Trailer.
     * This method should be called at the end of the day.
     *
     * @param players List of all Actors in the game.
     */
    public void resetPlayerLocations(List<Actor> players) {
        for (Actor player : players) {
            player.getLocation().updatePlayerLocation(this); // Move player to Trailer
        }
        System.out.println("All players have been reset to the Trailer.");
    }
}