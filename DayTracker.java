
//Model, determines end of game, starts new day
public class DayTracker {
    private int currentDay;
    private int maxDay;

    public DayTracker(int maxDay) {
        this.currentDay = 1;
        this.maxDay = maxDay;
    }

    public void updateDay() {
        currentDay++;
        if (gameEnd()){
            for (i = 1 to playerCount) {
                //player(i).calcTotalPoints;
                //determine winner
            }
        }
        // first, reset player location to trailer
        // reset role cards 
        // start turn tracker 

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