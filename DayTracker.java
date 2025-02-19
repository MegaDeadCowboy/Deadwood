
//Model, determines end of game, starts new day
public class DayTracker {
    private int currentDay;
    private int maxDay;

    public DayTracker(int maxDay) {
        this.currentDay = 1;
        this.maxDay = maxDay;
    }

    public void updateDay() {
        if (currentDay < maxDay) {
            currentDay++;
        }
    }

    public boolean gameEnd() {
        return currentDay >= maxDay;
    }
}