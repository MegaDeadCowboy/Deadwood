import java.util.List;

public class TurnTracker {
    private int currentPlayerIndex;
    private List<Actor> players;
    private DayTracker dayTracker;
    private Trailer trailer;
    private GameBoard gameBoard; 

    public TurnTracker(List<Actor> players, DayTracker dayTracker, Trailer trailer, GameBoard gameBoard) {
        this.players = players;
        this.dayTracker = dayTracker;
        this.trailer = trailer;
        this.gameBoard = gameBoard; 
        this.currentPlayerIndex = 0;
    }

    public void endTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        if (currentPlayerIndex == 0) {
            initiateNewDay();
        } else {
            System.out.println("Now it's Player " + players.get(currentPlayerIndex).getPlayerID() + "'s turn.");
        }
    }

    private void initiateNewDay() {
        dayTracker.updateDay(players, trailer, gameBoard); 
        if (dayTracker.gameEnd()) {
            System.out.println("Game Over! Calculating final scores...");
            return;
        }

        trailer.resetPlayerLocations(players, gameBoard); 

        currentPlayerIndex = 0;
        System.out.println("A new day has begun. Player " + players.get(currentPlayerIndex).getPlayerID() + " starts.");
    }

    public Actor getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
}

