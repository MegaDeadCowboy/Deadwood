import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardXMLParser {
    private Document doc;
    
    public CardXMLParser(Document doc) {
        this.doc = doc;
    }
    
    public List<RoleCard> parseCards() {
        List<RoleCard> cards = new ArrayList<>();
        Element root = doc.getDocumentElement();
        
        // Get all cards
        NodeList cardNodes = root.getElementsByTagName("card");
        System.out.println("Found " + cardNodes.getLength() + " scene cards.");
        
        for (int i = 0; i < cardNodes.getLength(); i++) {
            Element cardElement = (Element) cardNodes.item(i);
            
            // Extract card attributes
            String cardName = cardElement.getAttribute("name");
            String cardImg = cardElement.getAttribute("img");
            int budget = Integer.parseInt(cardElement.getAttribute("budget"));
            
            // Get scene info
            Element sceneElement = (Element) cardElement.getElementsByTagName("scene").item(0);
            int sceneNumber = Integer.parseInt(sceneElement.getAttribute("number"));
            String sceneDescription = sceneElement.getTextContent().trim();
            
            // Create a new RoleCard
            RoleCard card = new RoleCard();
            // You'll need to customize this based on your RoleCard implementation
            // card.setSceneName(cardName);
            // card.setSceneID(sceneNumber);
            // card.setSceneBudget(budget);
            
            // Parse parts/roles
            List<Map<String, Object>> roles = parseRoles(cardElement);
            
            // Add roles to the card
            // This depends on your RoleCard implementation
            
            cards.add(card);
            
            System.out.println("Parsed card: " + cardName + " (Scene " + sceneNumber + 
                    ") with budget " + budget + " and " + roles.size() + " roles.");
        }
        
        return cards;
    }
    
    private List<Map<String, Object>> parseRoles(Element cardElement) {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        NodeList partNodes = cardElement.getElementsByTagName("part");
        for (int i = 0; i < partNodes.getLength(); i++) {
            Element partElement = (Element) partNodes.item(i);
            
            Map<String, Object> role = new HashMap<>();
            role.put("name", partElement.getAttribute("name"));
            role.put("level", Integer.parseInt(partElement.getAttribute("level")));
            
            Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
            role.put("line", lineElement.getTextContent());
            
            roles.add(role);
        }
        
        return roles;
    }
    
    // Utility method to get all card names
    public List<String> getCardNames() {
        List<String> cardNames = new ArrayList<>();
        Element root = doc.getDocumentElement();
        
        NodeList cardNodes = root.getElementsByTagName("card");
        for (int i = 0; i < cardNodes.getLength(); i++) {
            Element cardElement = (Element) cardNodes.item(i);
            cardNames.add(cardElement.getAttribute("name"));
        }
        
        return cardNames;
    }
    
    // Utility method to get scene budgets
    public Map<String, Integer> getSceneBudgets() {
        Map<String, Integer> budgets = new HashMap<>();
        Element root = doc.getDocumentElement();
        
        NodeList cardNodes = root.getElementsByTagName("card");
        for (int i = 0; i < cardNodes.getLength(); i++) {
            Element cardElement = (Element) cardNodes.item(i);
            String cardName = cardElement.getAttribute("name");
            int budget = Integer.parseInt(cardElement.getAttribute("budget"));
            budgets.put(cardName, budget);
        }
        
        return budgets;
    }
}