import java.util.ArrayList;
import java.util.List;


public class CastingOffice extends Room {
    private List<Integer> upgradePriceCash;   // Cash prices for each rank upgrade
    private List<Integer> upgradePriceCredit; // Credit prices for each rank upgrade
    
    public CastingOffice() {
        super("Casting Office", new ArrayList<>());
        initializeUpgrades();
    }
    
    public CastingOffice(List<String> adjacentRooms) {
        super("Casting Office", adjacentRooms);
        initializeUpgrades();
    }
    
    private void initializeUpgrades() {
        this.upgradePriceCash = new ArrayList<>();
        this.upgradePriceCredit = new ArrayList<>();
        
        // Set default upgrade prices for cash
        upgradePriceCash.add(4);  // Rank 1 -> Rank 2 (costs $4)
        upgradePriceCash.add(10); // Rank 2 -> Rank 3 (costs $10)
        upgradePriceCash.add(18); // Rank 3 -> Rank 4 (costs $18)
        upgradePriceCash.add(28); // Rank 4 -> Rank 5 (costs $28)
        upgradePriceCash.add(40); // Rank 5 -> Rank 6 (costs $40)
        
        // Set default upgrade prices for credit
        upgradePriceCredit.add(5);  // Rank 1 -> Rank 2 (costs 5 credits)
        upgradePriceCredit.add(10); // Rank 2 -> Rank 3 (costs 10 credits)
        upgradePriceCredit.add(15); // Rank 3 -> Rank 4 (costs 15 credits)
        upgradePriceCredit.add(20); // Rank 4 -> Rank 5 (costs 20 credits)
        upgradePriceCredit.add(25); // Rank 5 -> Rank 6 (costs 25 credits)
    }
    
    // Method to set the cash upgrade prices
    public void setUpgradePricesCash(List<Integer> prices) {
        if (prices != null && !prices.isEmpty()) {
            this.upgradePriceCash = new ArrayList<>(prices);
        }
    }
    
    // Method to set the credit upgrade prices
    public void setUpgradePricesCredit(List<Integer> prices) {
        if (prices != null && !prices.isEmpty()) {
            this.upgradePriceCredit = new ArrayList<>(prices);
        }
    }
    
    public boolean validateUpgrade(int currentRank, int targetRank, String paymentType, PointTracker cost) {
        int rankDiff = targetRank - currentRank;
        
        if (rankDiff <= 0 || rankDiff > 5) {
            // Invalid upgrade (no upgrades beyond rank 6)
            return false; 
        }

        int index = currentRank - 1;  // Array is zero-based, ranks are 1-based
        if (index < 0 || index >= 5) {
            // Invalid current rank
            return false;
        }
        
        int price;
        if (paymentType.equals("cash")) {
            price = upgradePriceCash.get(index);
            return cost.getPlayerCash() >= price;
        } else {
            price = upgradePriceCredit.get(index);
            return cost.getPlayerCredit() >= price;
        }
    }

    
    public void checkOut(int currentRank, PointTracker cost, int targetRank, String paymentType) {
        int rankDiff = targetRank - currentRank;

        if (rankDiff <= 0 || rankDiff > 5) {
            System.out.println("Invalid upgrade selection.");
            return;
        }

        int index = currentRank - 1;  // Array is zero-based, ranks are 1-based
        if (index < 0 || index >= 5) {
            System.out.println("Invalid current rank.");
            return;
        }
        
        int price;
        if (paymentType.equals("cash")) {
            price = upgradePriceCash.get(index);
            cost.makePayment(price, false);
            System.out.println("Paid $" + price + " to upgrade from rank " + currentRank + " to rank " + targetRank);
        } else {
            price = upgradePriceCredit.get(index);
            cost.makePayment(price, true);
            System.out.println("Paid " + price + " credits to upgrade from rank " + currentRank + " to rank " + targetRank);
        }

        System.out.println("Upgrade successful! Now Rank: " + targetRank);
    }
    
    // Method to display upgrade costs
    public void displayUpgradeCosts() {
        System.out.println("Available Upgrades at Casting Office:");
        System.out.println("-------------------------------------");
        System.out.println("Rank | Cash Price | Credit Price");
        System.out.println("-------------------------------------");
        
        for (int i = 0; i < upgradePriceCash.size(); i++) {
            int targetRank = i + 2;  // Ranks start at 2 (upgrading from rank 1)
            System.out.printf("%4d | $%9d | %11d credits%n", 
                targetRank, 
                upgradePriceCash.get(i), 
                upgradePriceCredit.get(i));
        }
        System.out.println("-------------------------------------");
        System.out.println("Use 'upgrade <rank> <cash/credit>' to upgrade your rank.");
    }
}