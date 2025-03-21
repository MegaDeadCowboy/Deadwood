package deadwood.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deadwood.model.Room;
import deadwood.model.RoleCard;
import deadwood.model.Set;
import deadwood.model.CastingOffice;
import deadwood.model.Trailer;
import deadwood.model.FilmSetRoom;

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
            
            // Create lists to store the cash and credit prices
            List<Integer> cashPrices = new ArrayList<>();
            List<Integer> creditPrices = new ArrayList<>();
            
            // Initialize with dummy values to handle array indexing later
            for (int i = 0; i < 5; i++) {  // 5 upgrade levels (rank 1->2, 2->3, 3->4, 4->5, 5->6)
                cashPrices.add(0);
                creditPrices.add(0);
            }
            
            // Parse all upgrade elements
            for (int i = 0; i < upgradeNodes.getLength(); i++) {
                Element upgradeElement = (Element) upgradeNodes.item(i);
                int level = Integer.parseInt(upgradeElement.getAttribute("level"));
                String currency = upgradeElement.getAttribute("currency");
                int amount = Integer.parseInt(upgradeElement.getAttribute("amt"));
                
                // Level is the target rank (2-6), we need index 0-4
                int index = level - 2;
                
                if (index >= 0 && index < 5) {  // Make sure it's in valid range
                    if (currency.equals("dollar")) {
                        cashPrices.set(index, amount);
                    } else if (currency.equals("credit")) {
                        creditPrices.set(index, amount);
                    }
                }
            }
            
            // Set the parsed prices in the CastingOffice object
            office.setUpgradePricesCash(cashPrices);
            office.setUpgradePricesCredit(creditPrices);
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
                0,          
                "Extra Roles for " + setName,
                "Extra roles for the " + setName + " set",
                "",          
                0,          
                takes        
            );
            
            // Add each extra role to the card
            for (Map<String, Object> part : parts) {
                String roleName = (String) part.get("name");
                int roleLevel = (int) part.get("level");
                String roleLine = (String) part.get("line");
                
                extraRoleCard.addRole(roleName, roleLevel, roleLine);
            }
            
            // Create an empty Set for this room
            Set setObj = new Set(null, takes, parts.size());
            
            // Store the extra roles RoleCard in the Set for later access
            setObj.setExtraRolesCard(extraRoleCard);
            
            // Assign the set to the room
            filmRoom.assignSet(setObj);
        }
        
        // Parse trailer - explicitly cast to Room
        Element trailerElement = (Element) root.getElementsByTagName("trailer").item(0);
        if (trailerElement != null) {
            List<String> trailerNeighbors = parseNeighbors(trailerElement);
            Trailer trailerObj = Trailer.getInstance(trailerNeighbors);
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