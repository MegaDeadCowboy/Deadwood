package deadwood.model;

import java.util.ArrayList;
import java.util.List;

import deadwood.model.Actor;

import deadwood.controller.GameBoard;

/**
 * Starting room implemented as a Singleton
 */
public class Trailer extends Room {
    // Singleton instance
    private static Trailer instance = null;
    
    /**
     * Private constructor to prevent instantiation from outside
     */
    private Trailer() {
        super("Trailer", new ArrayList<>());
    }
    
    /**
     * Private constructor with adjacent rooms for initialization
     */
    private Trailer(List<String> adjacentRooms) {
        super("Trailer", adjacentRooms != null ? adjacentRooms : new ArrayList<>());
    }
    
    /**
     * Get the singleton instance of Trailer
     * @return The singleton Trailer instance
     */
    public static synchronized Trailer getInstance() {
        if (instance == null) {
            instance = new Trailer();
        }
        return instance;
    }
    
    /**
     * Get the singleton instance with initialized adjacent rooms
     * @param adjacentRooms List of adjacent room IDs
     * @return The singleton Trailer instance
     */
    public static synchronized Trailer getInstance(List<String> adjacentRooms) {
        if (instance == null) {
            instance = new Trailer(adjacentRooms);
        } else if (adjacentRooms != null && !adjacentRooms.isEmpty()) {
            // Update adjacent rooms if instance already exists
            instance.updateAdjacentRooms(adjacentRooms);
        }
        return instance;
    }
    
    /**
     * Update the adjacent rooms list
     * @param adjacentRooms New list of adjacent room IDs
     */
    private void updateAdjacentRooms(List<String> adjacentRooms) {
        this.adjacentRooms.clear();
        if (adjacentRooms != null) {
            this.adjacentRooms.addAll(adjacentRooms);
        }
    }
    
    /**
     * Reset the singleton instance (primarily for testing)
     */
    public static void resetInstance() {
        instance = null;
    }
    
    /**
     * Used to reset players at the end of a day
     */
    public void resetPlayerLocations(List<Actor> players, GameBoard gameBoard) {
        Room trailerRoom = gameBoard.getRoomByID("trailer");
    
        // Did we lose the trailer?
        if (trailerRoom == null) {
            System.out.println("Error: Trailer room not found.");
            return;
        }
    
        for (Actor player : players) {
            player.getLocation().updatePlayerLocation(trailerRoom);
        }
        System.out.println("All players have been reset to the Trailer.");
    }
}