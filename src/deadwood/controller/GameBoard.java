package deadwood.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

import deadwood.model.Actor;
import deadwood.model.Room;
import deadwood.model.DayTracker;
import deadwood.model.RoleCard;
import deadwood.model.Trailer;
import deadwood.model.CastingOffice;
import deadwood.model.Set;

import deadwood.util.BoardXMLParser;
import deadwood.util.CardXMLParser;
import deadwood.util.ParseXML;

// Initializes the game
public class GameBoard {
    private Map<String, Room> rooms;
    private List<Actor> players;
    private int playerCount;
    private TurnTracker turnTracker;
    private DayTracker dayTracker;
    private List<RoleCard> cards;
    
    public GameBoard(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 8) {
            throw new IllegalArgumentException("Game requires 2-8 players");
        }
        this.playerCount = numPlayers;
        this.players = new ArrayList<>();
        this.rooms = new HashMap<>();
        this.cards = new ArrayList<>();
        initiateBoardState();
    }

    public void initiateBoardState() {
        // Create players with appropriate starting conditions based on player count
        for (int i = 1; i <= playerCount; i++) {
            // Default starting values
            int startingRank = 1;
            int startingCredits = 0;
            
            // Set player stats based on number of players
            if (playerCount == 5) {
                // 5 players: start with 2 credits each
                startingCredits = 2;
            } else if (playerCount == 6) {
                // 6 players: start with 4 credits each
                startingCredits = 4;
            } else if (playerCount >= 7) {
                // 7-8 players: start at rank 2
                startingRank = 2;
            }
            
            // Create player with appropriate starting values
            Actor player = new Actor(i, startingRank);
            
            // Set starting credits if needed
            if (startingCredits > 0) {
                player.getPoints().setStartingPoints(startingCredits, startingRank);
            }
            
            players.add(player);
        }

        // Initialize rooms
        createRooms();
        
        // Parse cards and distribute them to sets
        loadCardsAndDistribute();

        // Create dayTracker with days based on player count
        int maxDays = 4; 
        if (playerCount <= 3) {
            maxDays = 3; 
        }
        this.dayTracker = new DayTracker(maxDays);
        
        // Create turnTracker after players and dayTracker are initialized
        this.turnTracker = new TurnTracker(players, dayTracker, (Trailer) rooms.get("trailer"), this);

        // Set initial player locations
        resetPlayerLocations();
        
        // Display game setup information
        System.out.println("\n==================================");
        System.out.println("       GAME CONFIGURATION        ");
        System.out.println("==================================");
        System.out.println("Number of players: " + playerCount);
        System.out.println("Number of days: " + maxDays);
        
        if (playerCount == 5) {
            System.out.println("Starting bonus: 2 credits per player");
        } else if (playerCount == 6) {
            System.out.println("Starting bonus: 4 credits per player");
        } else if (playerCount >= 7) {
            System.out.println("Starting bonus: All players begin at Rank 2");
        }
        System.out.println("==================================\n");
    }
        
    
    private void loadCardsAndDistribute() {
        try {
            // Parse cards XML
            ParseXML parser = new ParseXML();
            Document cardDoc = parser.getDocFromFile("cards.xml");  // Just pass the filename
            
            // Use CardXMLParser to extract card information
            CardXMLParser cardParser = new CardXMLParser(cardDoc);
            this.cards = cardParser.parseCards();
            
            // Distribute cards to sets
            distributeCards(cards);
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to load cards - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createRooms() {
        BoardXMLParser boardParser = null;
    
        try {
            ParseXML parser = new ParseXML();
            Document doc = parser.getDocFromFile("board.xml");
            boardParser = new BoardXMLParser(doc);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to parse board.xml - " + e.getMessage());
            e.printStackTrace();
            return;
        }
    
        Map<String, Room> parsedRooms = boardParser.parseRooms();
    
        // Store all parsed rooms in GameBoard
        for (Map.Entry<String, Room> entry : parsedRooms.entrySet()) {
            rooms.put(entry.getKey(), entry.getValue());
        }
        
    }

    public void resetPlayerLocations() {
        Room trailer = rooms.get("trailer");
    
        if (trailer == null) {
            System.out.println("Error: Trailer room not found.");
            return;
        }
    
        for (Actor player : players) {
            player.getLocation().updatePlayerLocation(trailer);
        }
    }
    
    public Actor getCurrentPlayer() {
        if (players.isEmpty()) {
            System.out.println("Error: No players found in game.");
            return null;
        }
        return turnTracker.getCurrentPlayer();
    }
    
    public Room getRoom(String roomID) {
        return rooms.get(roomID);
    }
    
    public boolean validatePlayerMove(String fromRoom, String toRoom) {
        Room current = rooms.get(fromRoom.toLowerCase());
    
        if (current == null) {
            System.out.println("Error: Current room '" + fromRoom + "' not found.");
            return false;
        }
    
        // Convert all neighbor names to lowercase 
        List<String> normalizedNeighbors = new ArrayList<>();
        for (String neighbor : current.getAdjacentRooms()) {
            normalizedNeighbors.add(neighbor.toLowerCase());
        }
    
        boolean validMove = normalizedNeighbors.contains(toRoom.toLowerCase());
    
        if (!validMove) {
            System.out.println("Move failed: " + toRoom + " is not adjacent to " + fromRoom + ".");
        }
    
        return validMove;
    }
    
    
    public int endTurn() {
        System.out.println("Ending turn for Player " + getCurrentPlayer().getPlayerID());
        
        // Use turnTracker to advance to the next player
        turnTracker.endTurn();
        
        // Get the new current player ID
        int newPlayerID = getCurrentPlayer().getPlayerID();
        
        System.out.println("Starting turn for Player " + newPlayerID);
        
        return newPlayerID;
    }
    
    
    public void reloadAndDistributeCards() {
        // First, clear all existing sets from rooms
        for (Room room : rooms.values()) {
            // Remove any existing set
            room.completeScene(); 
        }
        
        // Then load and distribute new cards
        loadCardsAndDistribute();
    }
    
    public Room getRoomByID(String roomID) {
        return rooms.get(roomID);
    }
    
    
    private void distributeCards(List<RoleCard> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("ERROR: No cards available to distribute!");
            return;
        }
        
        // Make a copy of the cards we can modify
        List<RoleCard> availableCards = new ArrayList<>(cards);
        
        // Shuffle the cards to randomize distribution
        Collections.shuffle(availableCards);
        
        // Find all rooms that can have sets (exclude trailer and office)
        List<Room> setRooms = new ArrayList<>();
        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();
            if (!(room instanceof Trailer) && !(room instanceof CastingOffice)) {
                setRooms.add(room);
            }
        }
        
        // Distribute cards to rooms
        int cardIndex = 0;
        for (Room room : setRooms) {
            if (cardIndex < availableCards.size()) {
                // Get the next card
                RoleCard card = availableCards.get(cardIndex++);
                
                // Set shots to default
                int shots = 3; 
                
                // Check if the room already has a set with extra roles
                Set existingSet = room.getSet();
                RoleCard extraRolesCard = null;
                if (existingSet != null) {
                    extraRolesCard = existingSet.getExtraRolesCard();
                    shots = existingSet.getShotCounter();
                }
                
                // Create a new set for this room with the card, preserving extra roles
                Set set = new Set(card, shots, extraRolesCard != null ? extraRolesCard.getSceneRoles().size() : 0);
                
                // If we had extra roles, keep them
                if (extraRolesCard != null) {
                    set.setExtraRolesCard(extraRolesCard);
                }
                
                // Assign the set to the room
                room.assignSet(set);
            }
        }
    }
    
    public void awardSceneBonusesToPlayers(Room room, Set set, Actor currentPlayer, String completedRole) {
        int budget = 0;
        if (set.getRoleCard() != null) {
            budget = set.getRoleCard().getSceneBudget();
        }
        
        int numStarringRoles = 0;
        if (set.getRoleCard() != null) {
            numStarringRoles = set.getRoleCard().getSceneRoles().size();
        }
        
        System.out.println("Scene wrapped! Awarding bonuses to players on the set...");
        boolean bonusAwarded = false;
        
        // First, award bonus to the current player who completed the scene
        if (currentPlayer.getLocation().getCurrentRoom() == room && completedRole != null) {
            boolean isExtraRole = set.isExtraRole(completedRole);
            int roleRank = 0;
            
            RoleCard.Role roleObj = set.getRole(completedRole);
            if (roleObj != null) {
                roleRank = roleObj.getLevel();
            }
            
            currentPlayer.getPoints().awardSceneBonus(budget, isExtraRole, roleRank, numStarringRoles);
            System.out.println("Player " + currentPlayer.getPlayerID() + " got a scene bonus for role: " + completedRole);
            bonusAwarded = true;
        }
        
        // Then check for other players with roles in the same room
        for (Actor player : players) {
            // Skip the current player who already got their bonus
            if (player == currentPlayer) {
                continue;
            }
            
            if (player.getLocation().getCurrentRoom() == room) {
                String role = player.getCurrentRole();
                
                // Only award bonuses to players with roles
                if (role != null) {
                    boolean isExtraRole = set.isExtraRole(role);
                    int roleRank = 0;
                    
                    RoleCard.Role roleObj = set.getRole(role);
                    if (roleObj != null) {
                        roleRank = roleObj.getLevel();
                    }
                    
                    player.getPoints().awardSceneBonus(budget, isExtraRole, roleRank, numStarringRoles);
                    System.out.println("Player " + player.getPlayerID() + " got a scene bonus for role: " + role);
                    bonusAwarded = true;
                }
            }
        }
        
        // Mark all roles as acted so players can't take them anymore
        set.markAllRolesAsActed();
        
        if (!bonusAwarded) {
            System.out.println("No players received a scene bonus.");
        }
}
    public List<String> getAllRoomNames() {
        return new ArrayList<>(rooms.keySet());
    }
    
    
    public List<Actor> getAllPlayers() {
        return players;
    }
}