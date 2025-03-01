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
                    
                    // System.out.println("Added upgrade to rank " + level + " for $" + amount);
                }
            }
        }
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
            
            // Create a FilmSetRoom
            FilmSetRoom filmRoom = new FilmSetRoom(setName, adjacentRooms);
            
            // Add it to the map as Room (which it implements)
            Room room = filmRoom;
            rooms.put(setName.toLowerCase(), room);
            
            // Parse takes for the set
            int takes = parseTakes(setElement);
            
            // Parse parts for the set and create a temporary RoleCard
            List<Map<String, Object>> parts = parseParts(setElement);
            
            // Create a temporary RoleCard with setName as the scene name
            RoleCard extraRoleCard = new RoleCard(
                0,           // No scene ID for extra roles
                "Extra Roles for " + setName,
                "Extra roles for the " + setName + " set",
                "",          // No card image for extra roles
                0,           // No budget for extra roles
                takes        // Use takes as shots
            );
            
            // Add each extra role to the card
            for (Map<String, Object> part : parts) {
                String roleName = (String) part.get("name");
                int roleLevel = (int) part.get("level");
                String roleLine = (String) part.get("line");
                
                extraRoleCard.addRole(roleName, roleLevel, roleLine);
            }
            
            // Create an empty Set for this room (it will be filled with a scene card later)
            Set setObj = new Set(null, takes, parts.size());
            
            // Store the extra roles RoleCard in the Set for later access
            setObj.setExtraRolesCard(extraRoleCard);
            
            // Assign the set to the room
            filmRoom.assignSet(setObj);
            
            // System.out.println("Created set for " + setName + " with " + parts.size() + " extra roles");
        }
        
        // Parse trailer - explicitly cast to Room
        Element trailerElement = (Element) root.getElementsByTagName("trailer").item(0);
        if (trailerElement != null) {
            List<String> trailerNeighbors = parseNeighbors(trailerElement);
            Trailer trailerObj = new Trailer(trailerNeighbors);
            rooms.put("trailer", trailerObj);
        }

        
        // Parse office - explicitly cast to Room
        Element officeElement = (Element) root.getElementsByTagName("office").item(0);
        if (officeElement != null) {
            List<String> officeNeighbors = parseNeighbors(officeElement);
            CastingOffice castingOffice = new CastingOffice(officeNeighbors);
            
            // Parse upgrade prices
            parseUpgrades(officeElement, castingOffice);
            
            Room office = castingOffice;
            rooms.put("office", office);
        }
        
        return rooms;
    }
}