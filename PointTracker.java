import java.util.Arrays;

public class PointTracker {

    // Player's current resources
    private int playerCash;
    private int playerCredit;
    private int rehearsalBonus;
    private int playerRank;

    // Constants for point rewards
    private static final int STARRING_ROLE_SUCCESS = 2;
    private static final int EXTRA_ROLE_SUCCESS = 1;
    private static final int MAX_REHEARSAL_BONUS = 6;

    
    //Constructor initializes a new point tracker with zero points
    public PointTracker() {
        this.playerCash = 0;
        this.playerCredit = 0;
        this.rehearsalBonus = 0;
        this.playerRank = 0;
    }

    // Sets the starting points based on game configuration
    public void setStartingPoints(int credits, int rank) {
        if (credits < 0) {
            throw new IllegalArgumentException("Starting points cannot be negative");
        }
        this.playerRank = rank;
        this.playerCredit = credits;
    }

    // Award a successful attempt on a role
    public void awardActingPoints(boolean success, boolean isExtra) {
        if (success) {
            if (isExtra) {
                // Extra roles get 1 cash and 1 credit on success
                playerCash += EXTRA_ROLE_SUCCESS;
                playerCredit += EXTRA_ROLE_SUCCESS;
                resetRehearsalBonus();
            }

            else {
                // Starring roles get 2 credits on success
                playerCredit += STARRING_ROLE_SUCCESS;
            }
        }
       
    }

  // Add rehearsal bonus
    public boolean addRehearsalToken() {
        if (rehearsalBonus >= MAX_REHEARSAL_BONUS) {
            return false;
        }
        rehearsalBonus++;
        return true;
    }

    public void resetRehearsalBonus() {
        rehearsalBonus = 0;
    }

    // Make payment in casting office
   boolean makePayment(int cost, boolean useCredits) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }

        if (useCredits) {
            if (playerCredit >= cost) {
                playerCredit -= cost;
                return true;
            }
        } else {
            if (playerCash >= cost) {
                playerCash -= cost;
                return true;
            }
        }
        return false;
    }

    // Award bonus when a scene is complete
    public void awardSceneBonus(int budget, boolean isStarringRole, int roleRank, int numStarringRoles) {
        if (isStarringRole) {
            //roll dice equal to budget
            int[] diceRolls = new int[budget];
            for (int i = 0; i < budget; i++) {
                diceRolls[i] = (int)(Math.random() * 6) + 1;
            }
            
            // Sort dice rolls in descending order
            Arrays.sort(diceRolls);
            for (int i = 0; i < diceRolls.length / 2; i++) {
                int temp = diceRolls[i];
                diceRolls[i] = diceRolls[diceRolls.length - 1 - i];
                diceRolls[diceRolls.length - 1 - i] = temp;
            }
            
            // Calculate how many dice this role gets
            int diceForThisRole = 0;
            
            // Loop through all dice
            for (int i = 0; i < diceRolls.length; i++) {
                // Calculate which role position this die goes to (1-based)
                int diePosition = (i % numStarringRoles) + 1;
                
                // If this die belongs to our role, add its value
                if (diePosition == roleRank) {
                    diceForThisRole += diceRolls[i];
                }
            }
            
            // Award the total of all dice assigned to this role
            playerCash += diceForThisRole;
            
        } else {
            // Extra roles get cash equal to their rank
            playerCash += roleRank;
        }
    }

   // Calculate total points for the end of the game
    public int calcTotalPoints(int rank) {
        return playerCash + playerCredit + (rank * 5);
    }

    // Getters
    public int getPlayerCash() {
        return playerCash;
    }

    public int getPlayerCredit() {
        return playerCredit;
    }

    public int getRehearsalBonus() {
        return rehearsalBonus;
    }
}