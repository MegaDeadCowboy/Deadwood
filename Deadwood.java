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

        // Fix: Get the current player and check if it's null
        Actor currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == null) {
            System.out.println("Fatal Error: No player is set. Exiting game.");
            return;
        }
        playerInterface.setCurrentPlayer(currentPlayer);

        System.out.println("Game started with " + numPlayers + " players!");

        // Start the game loop
        while (true) {
            playerInterface.processCommand();
        }
    }
}


