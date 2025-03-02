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
        
        // Set default upgrade prices for cash - index is target rank - 2
        upgradePriceCash.add(4);  // Rank 2 costs $4
        upgradePriceCash.add(10); // Rank 3 costs $10
        upgradePriceCash.add(18); // Rank 4 costs $18
        upgradePriceCash.add(28); // Rank 5 costs $28
        upgradePriceCash.add(40); // Rank 6 costs $40
        
        // Set default upgrade prices for credit - index is target rank - 2
        upgradePriceCredit.add(5);  // Rank 2 costs 5 credits
        upgradePriceCredit.add(10); // Rank 3 costs 10 credits
        upgradePriceCredit.add(15); // Rank 4 costs 15 credits
        upgradePriceCredit.add(20); // Rank 5 costs 20 credits
        upgradePriceCredit.add(25); // Rank 6 costs 25 credits
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
        // Can't downgrade or stay at same rank
        if (targetRank <= currentRank) {
            return false; 
        }

        // Can't upgrade beyond rank 6
        if (targetRank > 6) {
            return false;
        }
        
        // Calculate price - index is target rank - 2
        int index = targetRank - 2;  
        if (index < 0 || index >= upgradePriceCash.size()) {
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
        // Can't downgrade or stay at same rank
        if (targetRank <= currentRank) {
            System.out.println("Invalid upgrade selection. New rank must be higher than current rank.");
            return;
        }

        // Can't upgrade beyond rank 6
        if (targetRank > 6) {
            System.out.println("Invalid upgrade selection. Maximum rank is 6.");
            return;
        }
        
        // Calculate price - index is target rank - 2
        int index = targetRank - 2;
        if (index < 0 || index >= upgradePriceCash.size()) {
            System.out.println("Invalid target rank.");
            return;
        }
        
        int price;
        if (paymentType.equals("cash")) {
            price = upgradePriceCash.get(index);
            if (cost.makePayment(price, false)) {
                System.out.println("Paid $" + price + " to upgrade to rank " + targetRank);
            } else {
                System.out.println("Not enough cash for this upgrade.");
                return;
            }
        } else {
            price = upgradePriceCredit.get(index);
            if (cost.makePayment(price, true)) {
                System.out.println("Paid " + price + " credits to upgrade to rank " + targetRank);
            } else {
                System.out.println("Not enough credits for this upgrade.");
                return;
            }
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