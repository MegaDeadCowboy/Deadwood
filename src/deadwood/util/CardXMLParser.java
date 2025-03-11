package deadwood.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import deadwood.model.RoleCard;

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
            RoleCard card = new RoleCard(
                sceneNumber,  
                cardName,      
                sceneDescription, 
                cardImg,       
                budget,        
                3   
            );
            
            // Parse roles
            NodeList partNodes = cardElement.getElementsByTagName("part");
            for (int j = 0; j < partNodes.getLength(); j++) {
                Element partElement = (Element) partNodes.item(j);
                
                String roleName = partElement.getAttribute("name");
                int roleLevel = Integer.parseInt(partElement.getAttribute("level"));
                
                Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
                String roleLine = lineElement.getTextContent();
                
                // Add role to card
                card.addRole(roleName, roleLevel, roleLine);
            }
            cards.add(card);
        }
        
        return cards;
    }
    
    // Utility method to get all scene ids
    public List<Integer> getAllSceneIDs() {
        List<Integer> sceneIDs = new ArrayList<>();
        Element root = doc.getDocumentElement();
        
        NodeList sceneNodes = root.getElementsByTagName("scene");
        for (int i = 0; i < sceneNodes.getLength(); i++) {
            Element sceneElement = (Element) sceneNodes.item(i);
            int sceneNumber = Integer.parseInt(sceneElement.getAttribute("number"));
            sceneIDs.add(sceneNumber);
        }
        
        return sceneIDs;
    }
}