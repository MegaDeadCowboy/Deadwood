package deadwood.model;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class that tracks player locations and provides information about room positions.
 */
public class PlayerLocation {
    private int playerID;
    private int roomID;
    private boolean onRole;
    private Room currentRoom;
    
    // Store room positions
    private static final Map<String, Point> ROOM_POSITIONS;
    
    // Store rooms that can have scenes
    private static final String[] SCENE_ROOMS = {
        "main street", "saloon", "bank", "church", "hotel", 
        "ranch", "general store", "jail", "train station", "secret hideout"
    };
    
    // Initialize the room positions
    static {
        Map<String, Point> positions = new HashMap<>();
        positions.put("trailer", new Point(991, 248));
        positions.put("office", new Point(991, 452));
        positions.put("train station", new Point(114, 69));
        positions.put("secret hideout", new Point(116, 432));
        positions.put("church", new Point(639, 432));
        positions.put("hotel", new Point(908, 432));
        positions.put("main street", new Point(637, 69));
        positions.put("jail", new Point(909, 69));
        positions.put("general store", new Point(239, 165));
        positions.put("ranch", new Point(372, 249));
        positions.put("bank", new Point(495, 249));
        positions.put("saloon", new Point(628, 249));
        ROOM_POSITIONS = Collections.unmodifiableMap(positions);
    }

    public PlayerLocation(int ID) {
        this.playerID = ID;
        this.roomID = -1;
        this.onRole = false;
        this.currentRoom = null;
    }
    
    /**
     * Validates if a player can move to a destination room
     * 
     * @param destinationRoomID The ID of the destination room
     * @return true if the move is valid, false otherwise
     */
    public boolean validatePlayerMove(String destinationRoomID) {
        if (currentRoom == null) {
            System.out.println("Error: Player is not in a valid room.");
            return false;
        }

        List<String> adjacentRooms = currentRoom.getAdjacentRooms();
        if (adjacentRooms == null || !adjacentRooms.contains(destinationRoomID)) {
            System.out.println("Invalid move. You can only move to adjacent rooms.");
            return false;
        }
        return true;
    }
    
    /**
     * Updates the player's location to a new room
     * 
     * @param newRoom The new room to move to
     */
    public void updatePlayerLocation(Room newRoom) {
        if (newRoom == null) {
            System.out.println("Error: Cannot move to a null room.");
            return;
        }
        this.currentRoom = newRoom;
    }
    
    /**
     * Gets the room position for the specified room ID
     * 
     * @param roomID The room ID to get the position for (case insensitive)
     * @return The position of the room or null if not found
     */
    public static Point getRoomPosition(String roomID) {
        if (roomID == null) {
            return null;
        }
        
        Point position = ROOM_POSITIONS.get(roomID.toLowerCase());
        if (position == null) {
            return null;
        }
        
        // Return a copy to prevent modification of the original
        return new Point(position);
    }
    
    /**
     * Gets a scaled room position for the specified room ID
     * 
     * @param roomID The room ID to get the position for (case insensitive)
     * @param scaleFactor The scale factor to apply
     * @return The scaled position of the room or null if not found
     */
    public static Point getScaledRoomPosition(String roomID, double scaleFactor) {
        Point originalPosition = getRoomPosition(roomID);
        if (originalPosition == null) {
            return null;
        }
        
        return new Point(
            (int)(originalPosition.x * scaleFactor),
            (int)(originalPosition.y * scaleFactor)
        );
    }
    
    /**
     * Gets all room positions as a map
     * 
     * @return An unmodifiable map of room IDs to positions
     */
    public static Map<String, Point> getAllRoomPositions() {
        return ROOM_POSITIONS;
    }
    
    /**
     * Gets the IDs of rooms that can have scenes
     * 
     * @return An array of room IDs that can have scenes
     */
    public static String[] getSceneRoomIDs() {
        return SCENE_ROOMS.clone(); // Return a copy to prevent modification
    }
    
    /**
     * Gets the current room
     * 
     * @return The current room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Gets the player ID
     * 
     * @return The player ID
     */
    public int getPlayerID() {
        return playerID;
    }
    
    /**
     * Gets the room ID
     * 
     * @return The room ID
     */
    public int getRoomID() {
        return roomID;
    }
    
    /**
     * Checks if the player is on a role
     * 
     * @return true if the player is on a role, false otherwise
     */
    public boolean getOnRole() {
        return onRole;
    }
}