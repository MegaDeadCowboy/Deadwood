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
    }

    public void endTurn() {
        // Store current player ID for logging
        int currentID = players.get(currentPlayerIndex).getPlayerID();
        
        // Reset any per-turn state for the current player if needed
        Actor currentPlayer = players.get(currentPlayerIndex);
        
        // Advance to the next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // If we've looped back to the first player, we've completed a round
        if (currentPlayerIndex == 0) {
            System.out.println("All players have taken their turn. Starting new round.");
        } 
        
    }

    private boolean checkAllScenesWrapped() {
        // Get all rooms that can have sets
        for (String roomID : gameBoard.getAllRoomNames()) {
            Room room = gameBoard.getRoomByID(roomID);
            
            // Skip rooms that can't have sets (like Trailer, Office)
            if (room instanceof Trailer || room instanceof CastingOffice) {
                continue;
            }
            
            // Check if this room has an active set
            Set set = room.getSet();
            if (set != null && set.isActive()) {
                return false; // Found at least one active set
            }
        }
        
        // If we get here, all sets are wrapped
        return true;
    }


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
    

    private void calculateFinalScores() {
        System.out.println("\n=== FINAL SCORES ===");
        
        for (Actor player : players) {
            int finalScore = player.getPoints().calcTotalPoints(player.getCurrentRank());
            System.out.println("Player " + player.getPlayerID() + ": " + finalScore + " points");
        }
        
        System.out.println("Thanks for playing Deadwood!");
        System.exit(0);
    }


    public Actor getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}