import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardXMLParser {
    private Document doc;
    
    public BoardXMLParser(Document doc) {
        this.doc = doc;
    }
    
    public Map<String, Room> parseRooms() {
        Map<String, Room> rooms = new HashMap<>();
        Element root = doc.getDocumentElement();
        
        // Parse regular sets
        NodeList sets = root.getElementsByTagName("set");
        for (int i = 0; i < sets.getLength(); i++) {
            Element setElement = (Element) sets.item(i);
            String setName = setElement.getAttribute("name");
            List<String> adjacentRooms = parseNeighbors(setElement);
            
            // Create a new Room for each set
            Room room = new Room(setName, adjacentRooms);
            rooms.put(setName.toLowerCase(), room);
            
            // Parse takes for the set
            int takes = parseTakes(setElement);
            
            // Parse parts for the set
            List<Map<String, Object>> parts = parseParts(setElement);
            
            // Create a RoleCard for the set
            // You'll need to implement this part based on your RoleCard class
            RoleCard roleCard = new RoleCard();
            // Set up the RoleCard with parts info
            
            // Create a Set for the room
            Set set = new Set(roleCard, takes, parts.size());
            room.assignSet(set);
            
            System.out.println("Parsed set: " + setName + " with " + adjacentRooms.size() + 
                    " neighbors and " + parts.size() + " parts.");
        }
        
        // Parse special locations: trailer
        Element trailerElement = (Element) root.getElementsByTagName("trailer").item(0);
        if (trailerElement != null) {
            List<String> trailerNeighbors = parseNeighbors(trailerElement);
            Trailer trailer = new Trailer();
            // Add neighbors to trailer
            for (String neighbor : trailerNeighbors) {
                trailer.getAdjacentRooms().add(neighbor.toLowerCase());
            }
            rooms.put("trailer", trailer);
            System.out.println("Parsed trailer with " + trailerNeighbors.size() + " neighbors.");
        }
        
        // Parse special locations: office
        Element officeElement = (Element) root.getElementsByTagName("office").item(0);
        if (officeElement != null) {
            List<String> officeNeighbors = parseNeighbors(officeElement);
            CastingOffice office = new CastingOffice();
            // Add neighbors to office
            for (String neighbor : officeNeighbors) {
                office.getAdjacentRooms().add(neighbor.toLowerCase());
            }
            
            // Parse upgrade info
            parseUpgrades(officeElement, office);
            
            rooms.put("office", office);
            System.out.println("Parsed office with " + officeNeighbors.size() + " neighbors.");
        }
        
        return rooms;
    }
    
    private List<String> parseNeighbors(Element element) {
        List<String> neighbors = new ArrayList<>();
        Element neighborsElement = (Element) element.getElementsByTagName("neighbors").item(0);
        
        if (neighborsElement != null) {
            NodeList neighborNodes = neighborsElement.getElementsByTagName("neighbor");
            for (int i = 0; i < neighborNodes.getLength(); i++) {
                Element neighborElement = (Element) neighborNodes.item(i);
                String neighborName = neighborElement.getAttribute("name");
                neighbors.add(neighborName);
            }
        }
        
        return neighbors;
    }
    
    private int parseTakes(Element setElement) {
        NodeList takesNodes = setElement.getElementsByTagName("take");
        return takesNodes.getLength();
    }
    
    private List<Map<String, Object>> parseParts(Element setElement) {
        List<Map<String, Object>> parts = new ArrayList<>();
        Element partsElement = (Element) setElement.getElementsByTagName("parts").item(0);
        
        if (partsElement != null) {
            NodeList partNodes = partsElement.getElementsByTagName("part");
            for (int i = 0; i < partNodes.getLength(); i++) {
                Element partElement = (Element) partNodes.item(i);
                Map<String, Object> part = new HashMap<>();
                
                part.put("name", partElement.getAttribute("name"));
                part.put("level", Integer.parseInt(partElement.getAttribute("level")));
                
                Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
                part.put("line", lineElement.getTextContent());
                
                parts.add(part);
            }
        }
        
        return parts;
    }
    
    private void parseUpgrades(Element officeElement, CastingOffice office) {
        Element upgradesElement = (Element) officeElement.getElementsByTagName("upgrades").item(0);
        
        if (upgradesElement != null) {
            NodeList upgradeNodes = upgradesElement.getElementsByTagName("upgrade");
            List<String> upgrades = new ArrayList<>();
            List<Integer> upgradePrices = new ArrayList<>();
            
            for (int i = 0; i < upgradeNodes.getLength(); i++) {
                Element upgradeElement = (Element) upgradeNodes.item(i);
                int level = Integer.parseInt(upgradeElement.getAttribute("level"));
                String currency = upgradeElement.getAttribute("currency");
                int amount = Integer.parseInt(upgradeElement.getAttribute("amt"));
                
                // For now, just handle dollar upgrades (you might need to handle credits too)
                if (currency.equals("dollar")) {
                    // Make sure we have entries for levels 1 to (level-1)
                    while (upgrades.size() < level - 1) {
                        upgrades.add("dummy");
                        upgradePrices.add(0);
                    }
                    
                    upgrades.add("Rank " + level);
                    upgradePrices.add(amount);
                    
                    System.out.println("Added upgrade to rank " + level + " for $" + amount);
                }
            }
            
            // You'll need to implement a way to set these in your CastingOffice class
            // This depends on your implementation
        }
    }
}