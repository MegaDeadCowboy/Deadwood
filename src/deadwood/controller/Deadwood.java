package deadwood.controller;

import deadwood.model.Actor;
import deadwood.view.GameView;
import deadwood.view.PlayerInterface;

public class Deadwood {
    
    /**
     * Main method to start the game
     * 
     * @param args Command line arguments:
     *             args[0] = number of players (2-8)
     *             args[1] = interface mode (optional, "gui" or "text", defaults to "gui")
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Deadwood! Initializing game...");

        // Parse number of players argument
        if (args.length < 1) {
            System.out.println("Usage: java Deadwood <numPlayers> [gui/text]");
            return;
        }

        // Parse player count
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(args[0]);
            if (numPlayers < 2 || numPlayers > 8) {
                System.out.println("Error: Number of players must be between 2 and 8.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number of players.");
            return;
        }

        // Determine interface mode (GUI or text)
        boolean useGUI = true; // Default to GUI
        if (args.length >= 2) {
            String interfaceMode = args[1].toLowerCase();
            if (interfaceMode.equals("text")) {
                useGUI = false;
            } else if (!interfaceMode.equals("gui")) {
                System.out.println("Warning: Unrecognized interface mode. Using GUI by default.");
            }
        }

        if (useGUI) {
            // Start the GUI version
            System.out.println("Starting Deadwood with GUI interface...");
            javax.swing.SwingUtilities.invokeLater(() -> {
                GameView gameView = new GameView(numPlayers);
            });
        } else {
            // Start the text-based version
            System.out.println("Starting Deadwood with text-based interface...");
            GameBoard game = new GameBoard(numPlayers);
            PlayerInterface playerInterface = new PlayerInterface(game);

            // Get the current player and check if it's null
            Actor currentPlayer = game.getCurrentPlayer();
            if (currentPlayer == null) {
                System.out.println("Fatal Error: No player is set. Exiting game.");
                return;
            }
            playerInterface.setCurrentPlayer(currentPlayer);

            System.out.println("Game started with " + numPlayers + " players!");
            System.out.println("Type 'help' for available commands.");

            // Start the game loop
            while (true) {
                // Get the current player (may have changed after a turn ends)
                currentPlayer = game.getCurrentPlayer();
                playerInterface.setCurrentPlayer(currentPlayer);
                
                // Display a turn delimiter for clarity
                System.out.println("\n====== Player " + currentPlayer.getPlayerID() + "'s Turn ======");
                
                // Process the current player's command
                playerInterface.processCommand();
            }
        }
    }
}