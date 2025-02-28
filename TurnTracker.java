import java.util.List;

public class TurnTracker {
    private int currentPlayerIndex;
    private List<Actor> players;
    private DayTracker dayTracker;
    private Trailer trailer;
    private GameBoard gameBoard; 

    public TurnTracker(List<Actor> players, DayTracker dayTracker, Trailer trailer, GameBoard gameBoard) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty");
        }
        
        this.players = players;
        this.dayTracker = dayTracker;
        this.trailer = trailer;
        this.gameBoard = gameBoard; 
        this.currentPlayerIndex = 0;
        
        System.out.println("Turn tracker initialized. Starting with Player " + players.get(0).getPlayerID());
    }

    /**
     * End the current player's turn and advance to the next player
     */
    public void endTurn() {
        // Store current player ID for logging
        int currentID = players.get(currentPlayerIndex).getPlayerID();
        
        // Reset any per-turn state for the current player if needed
        Actor currentPlayer = players.get(currentPlayerIndex);
        
        // Advance to the next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // If we've looped back to the first player, it's a new day
        if (currentPlayerIndex == 0) {
            System.out.println("All players have taken their turn. Starting a new day.");
            initiateNewDay();
        } else {
            Actor nextPlayer = players.get(currentPlayerIndex);
            System.out.println("Turn ended for Player " + currentID + 
                              ". Starting turn for Player " + nextPlayer.getPlayerID());
        }
    }

    /**
     * Start a new day in the game
     */
    private void initiateNewDay() {
        // Use the day tracker to handle day transition
        dayTracker.updateDay(players, trailer, gameBoard); 
        
        // Check if game has ended
        if (dayTracker.gameEnd()) {
            System.out.println("Game Over! Calculating final scores...");
            calculateFinalScores();
            return;
        }

        // Reset player locations to trailer
        trailer.resetPlayerLocations(players, gameBoard); 

        // Restart with the first player
        currentPlayerIndex = 0;
        System.out.println("A new day has begun. Player " + players.get(currentPlayerIndex).getPlayerID() + " starts.");
    }
    
    /**
     * Calculate and display final scores for all players
     */
    private void calculateFinalScores() {
        System.out.println("\n=== FINAL SCORES ===");
        
        for (Actor player : players) {
            int finalScore = player.getPoints().calcTotalPoints(player.getCurrentRank());
            System.out.println("Player " + player.getPlayerID() + ": " + finalScore + " points");
        }
        
        System.out.println("Thanks for playing Deadwood!");
        System.exit(0);
    }

    /**
     * Get the current player whose turn it is
     * @return The current player Actor
     */
    public Actor getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}