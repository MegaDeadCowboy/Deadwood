import java.util.ArrayList;
import java.util.List;

/**
 * Location where players can upgrade their rank
 */
public class CastingOffice extends BasicRoom {
    private List<String> upgrades;
    private List<Integer> upgradePrice;
    
    public CastingOffice() {
        super("Casting Office", new ArrayList<>());
        initializeUpgrades();
    }
    
    public CastingOffice(List<String> adjacentRooms) {
        super("Casting Office", adjacentRooms);
        initializeUpgrades();
    }
    
    private void initializeUpgrades() {
        this.upgrades = new ArrayList<>();
        this.upgradePrice = new ArrayList<>();
        
        // Example upgrade prices
        upgradePrice.add(5);  // Rank 1 -> Rank 2
        upgradePrice.add(10); // Rank 2 -> Rank 3
        upgradePrice.add(15); // Rank 3 -> Rank 4
        upgradePrice.add(20); // Rank 4 -> Rank 5
    }
    
    /**
     * Validate if a player can upgrade their rank
     */
    public boolean validateUpgrade(int currentRank, int targetRank, String paymentType, PointTracker cost) {
        int rankDiff = targetRank - currentRank;
        
        if (rankDiff <= 0 || rankDiff > upgradePrice.size()) {
            return false; // Invalid upgrade
        }

        int price = upgradePrice.get(rankDiff - 1);

        return paymentType.equals("cash") 
            ? cost.getPlayerCash() >= price 
            : cost.getPlayerCredit() >= price;
    }

    /**
     * Process the payment for a rank upgrade
     */
    public void checkOut(int currentRank, PointTracker cost, int targetRank, String paymentType) {
        int rankDiff = targetRank - currentRank;

        if (rankDiff <= 0 || rankDiff > upgradePrice.size()) {
            System.out.println("Invalid upgrade selection.");
            return;
        }

        int price = upgradePrice.get(rankDiff - 1);

        if (paymentType.equals("cash")) {
            cost.makePayment(price, false);
        } else {
            cost.makePayment(price, true);
        }

        System.out.println("Upgrade successful! Now Rank: " + targetRank);
    }
}