import java.util.List;

//Model, determines end of game, starts new day
public class DayTracker {
    private int currentDay;
    private int maxDay;

    public DayTracker(int maxDay) {
        this.currentDay = 1;
        this.maxDay = maxDay;
    }

    public void updateDay(List<Actor> players, Trailer trailer) {
        currentDay++;
        if (gameEnd()) {
            System.out.println("Game Over! Calculating final scores...");
            for (Actor player : players) {
                System.out.println("Player " + player.getPlayerID() + " Score: " + player.getPoints().calcTotalPoints(player.getCurrentRank()));
            }
            return;
        }

        // Reset players to the trailer
        trailer.resetPlayerLocations(players);
        System.out.println("New day begins!");
    }

    public boolean gameEnd() {
        if (currentDay == maxDay) {
            return true;
        }
        else {
            return false;
        }
    }
}