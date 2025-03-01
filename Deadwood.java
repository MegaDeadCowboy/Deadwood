
// Main class for entry into game
public class Deadwood {
    public static void main(String[] args) {
        System.out.println("Welcome to Deadwood! Initializing game...");

        if (args.length != 1) {
            System.out.println("Usage: java Deadwood <numPlayers>");
            return;
        }

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