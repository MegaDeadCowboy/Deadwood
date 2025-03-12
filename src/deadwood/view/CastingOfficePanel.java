package deadwood.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import deadwood.model.*;
import deadwood.controller.GameBoard;

public class CastingOfficePanel extends JPanel {

    private GameBoard gameBoard;
    private GameView parentView;
    private JTable upgradeTable;
    private JButton cashUpgradeButton;
    private JButton creditUpgradeButton;
    
    public CastingOfficePanel(GameBoard gameBoard, GameView parentView) {
        this.gameBoard = gameBoard;
        this.parentView = parentView;
        
        // Set panel properties
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Casting Office - Rank Upgrades",
            TitledBorder.CENTER,
            TitledBorder.TOP));
        
        setLayout(new BorderLayout(0, 10));
        setPreferredSize(new Dimension(230, 180));
        
        // Initialize components
        initializeComponents();
        
        // Update visibility based on initial location
        updateVisibility();
    }
    
 
    private void initializeComponents() {
        // Create upgrade table
        String[] columnNames = {"Rank", "Cash Cost", "Credit Cost"};
        Object[][] data = {
            {"2", "$4", "5 cr"},
            {"3", "$10", "10 cr"},
            {"4", "$18", "15 cr"},
            {"5", "$28", "20 cr"},
            {"6", "$40", "25 cr"}
        };
        
        upgradeTable = new JTable(data, columnNames);
        upgradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        upgradeTable.setRowSelectionAllowed(true);
        upgradeTable.setColumnSelectionAllowed(false);
        
        // Set column widths
        TableColumnModel columnModel = upgradeTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(70);
        columnModel.getColumn(2).setPreferredWidth(70);
        
        // Center-align text in cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < upgradeTable.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Create scroll pane for table
        JScrollPane tableScrollPane = new JScrollPane(upgradeTable);
        tableScrollPane.setPreferredSize(new Dimension(210, 100));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Create upgrade buttons
        cashUpgradeButton = new JButton("Pay Cash");
        creditUpgradeButton = new JButton("Pay Credits");
        
        // Add action listeners
        cashUpgradeButton.addActionListener(e -> handleUpgrade("cash"));
        creditUpgradeButton.addActionListener(e -> handleUpgrade("credit"));
        
        // Add buttons to panel
        buttonsPanel.add(cashUpgradeButton);
        buttonsPanel.add(creditUpgradeButton);
        
        // Add components to main panel
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Updates panel visibility based on player location
     */
    public void updateVisibility() {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        if (currentPlayer == null) {
            setVisible(false);
            return;
        }
        
        Room currentRoom = currentPlayer.getLocation().getCurrentRoom();
        
        // Only show when player is in the Casting Office
        boolean inCastingOffice = (currentRoom instanceof CastingOffice);
        setVisible(inCastingOffice);
        
        // Update button enabled state - only enable if player has no role
        boolean canUpgrade = inCastingOffice && (currentPlayer.getCurrentRole() == null);
        cashUpgradeButton.setEnabled(canUpgrade);
        creditUpgradeButton.setEnabled(canUpgrade);
    }
    
    /**
     * Handles upgrade button click
     */
    private void handleUpgrade(String paymentType) {
        Actor currentPlayer = gameBoard.getCurrentPlayer();
        
        // Get selected rank
        int selectedRow = upgradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentView, 
                "Please select a rank to upgrade to.", 
                "No Rank Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calculate target rank (row index + 2)
        int targetRank = selectedRow + 2;
        int currentRank = currentPlayer.getCurrentRank();
        
        // Can't downgrade or stay at same rank
        if (targetRank <= currentRank) {
            JOptionPane.showMessageDialog(parentView, 
                "You can only upgrade to a higher rank than your current rank (" + currentRank + ").", 
                "Invalid Upgrade", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Attempt to upgrade
        boolean success = currentPlayer.inputUpgrade(targetRank, paymentType);
        
        if (success) {
            JOptionPane.showMessageDialog(parentView, 
                "Successfully upgraded to rank " + targetRank + "!", 
                "Upgrade Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Update UI
            parentView.updateGameState();
            
            // End turn after successful upgrade
            // Access the control panel through the parent view to end the turn
            parentView.endTurn();
        } else {
            // Calculate required amount
            String costType = paymentType.equals("cash") ? "dollars" : "credits";
            String costCell = paymentType.equals("cash") ? 
                              (String)upgradeTable.getValueAt(selectedRow, 1) : 
                              (String)upgradeTable.getValueAt(selectedRow, 2);
            
            // Strip non-numeric characters
            String costValue = costCell.replaceAll("[^0-9]", "");
            
            JOptionPane.showMessageDialog(parentView, 
                "Upgrade failed! You need " + costValue + " " + costType + " for this upgrade.", 
                "Insufficient Funds", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}