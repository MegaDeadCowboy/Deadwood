import java.util.List;

//Model, determines end of game, starts new day
public class DayTracker {
    private int currentDay;
    private int maxDay;

    public DayTracker(int maxDay) {
        this.currentDay = 1;
        this.maxDay = maxDay;
    }

    public void updateDay(List<Actor> players, Trailer trailer, GameBoard gameBoard) {
        currentDay++;
        
        System.out.println("\n==================================");
        System.out.println("        DAY " + currentDay + " BEGINS");
        System.out.println("==================================\n");
    
        if (gameEnd()) {
            System.out.println("Game Over! Calculating final scores...");
            for (Actor player : players) {
                System.out.println("Player " + player.getPlayerID() + " Score: " 
                    + player.getPoints().calcTotalPoints(player.getCurrentRank()));
            }
            System.out.println("Game has ended. Thank you for playing!");
            System.exit(0);
            return;
        }
    
        // Reset player locations to trailer
        trailer.resetPlayerLocations(players, gameBoard);
        
        // Reset player roles
        for (Actor player : players) {
            if (player.getCurrentRole() != null) {
                System.out.println("Player " + player.getPlayerID() + " reset from role: " + player.getCurrentRole());
                // Use reflection to set currentRole to null - this is a workaround for a private field
                try {
                    java.lang.reflect.Field field = Actor.class.getDeclaredField("currentRole");
                    field.setAccessible(true);
                    field.set(player, null);
                } catch (Exception e) {
                    System.out.println("Warning: Could not reset player role: " + e.getMessage());
                }
            }
        }
        
        // Reload and redistribute card scenes
        System.out.println("Redistributing scene cards for the new day...");
        gameBoard.reloadAndDistributeCards();
    
        System.out.println("New day begins! All players reset to the Trailer.");
    }

    public boolean gameEnd() {
        return currentDay >= maxDay;
    }
    
    public int getCurrentDay() {
        return currentDay;
    }
}