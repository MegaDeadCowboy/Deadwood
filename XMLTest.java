import org.w3c.dom.Document;
import java.util.List;

public class XMLTest {
    public static void main(String[] args) {
        ParseXML parser = new ParseXML();
        
        try {
            System.out.println("Loading cards.xml...");
            Document cardsDoc = parser.getDocFromFile("cards.xml");
            
            CardXMLParser cardParser = new CardXMLParser(cardsDoc);
            List<RoleCard> cards = cardParser.parseCards();
            
            System.out.println("\nParsed " + cards.size() + " role cards.");
            
            // Print details of a few cards
            System.out.println("\nSample card details:");
            for (int i = 0; i < Math.min(3, cards.size()); i++) {
                System.out.println("\n=== Card " + (i+1) + " ===");
                System.out.println(cards.get(i));
            }
            
        } catch (Exception e) {
            System.out.println("Error during XML parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}