import java.util.List;

//Room containing upgrade
public class CastingOffice extends Room {
    private List<String> upgrades;
    private List<Integer> upgradePrice;
    
    public boolean validateUpgrade(int currentRank, int targetRank, String paymentType, PointTracker cost) {
        // Implementation
        return false;
    }
    
    public void checkOut(PointTracker cost, int targetRank, String paymentType) {
        // Implementation
    }
}