import java.util.List;

//Model, determines end of day and next players turn
public class TurnTracker {
    private int currentPlayerIndex; // Tracks the current player's turn
    private List<Actor> players;
    private DayTracker dayTracker;
    private Trailer trailer;

    /**
     * Constructor initializes turn tracking with players and day system.
     *
     * @param players    List of all Actors
     * @param dayTracker Day tracking system
     * @param trailer    Trailer room for resetting locations
     */
    public TurnTracker(List<Actor> players, DayTracker dayTracker, Trailer trailer) {
        this.players = players;
        this.dayTracker = dayTracker;
        this.trailer = trailer;
        this.currentPlayerIndex = 0;
    }

    /**
     * Ends the current turn and switches to the next player.
     * If all players have acted, starts a new day.
     */
    public void endTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // If we cycle back to Player 0, the day has ended
        if (currentPlayerIndex == 0) {
            initiateNewDay();
        } else {
            System.out.println("Now it's Player " + players.get(currentPlayerIndex).getPlayerID() + "'s turn.");
        }
    }

    /**
     * Advances to a new day and resets game state.
     */
    private void initiateNewDay() {
        dayTracker.updateDay();
        if (dayTracker.gameEnd()) {
            System.out.println("Game Over! Calculating final scores...");
            return;
        }

        // Reset all players' locations to the Trailer
        trailer.resetPlayerLocations(players);

        // Reset turn order
        currentPlayerIndex = 0;
        System.out.println("A new day has begun. Player " + players.get(currentPlayerIndex).getPlayerID() + " starts.");
    }

    /**
     * @return The current player whose turn it is.
     */
    public Actor getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}
