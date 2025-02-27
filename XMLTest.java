import org.w3c.dom.Document;
import java.util.List;
import java.util.Map;

public class XMLTest {
    public static void main(String[] args) {
        ParseXML parser = new ParseXML();
        
        try {
            System.out.println("Loading cards.xml...");
            Document cardsDoc = parser.getDocFromFile("cards.xml");
            
            CardXMLParser cardParser = new CardXMLParser(cardsDoc);
            
            // Get all card names
            List<String> cardNames = cardParser.getCardNames();
            System.out.println("\nScene Cards:");
            for (String name : cardNames) {
                System.out.println("- " + name);
            }
            
            // Get all budgets
            Map<String, Integer> budgets = cardParser.getSceneBudgets();
            System.out.println("\nScene Budgets:");
            for (Map.Entry<String, Integer> entry : budgets.entrySet()) {
                System.out.println(entry.getKey() + ": $" + entry.getValue());
            }
            
            // Parse all cards
            List<RoleCard> cards = cardParser.parseCards();
            System.out.println("\nParsed " + cards.size() + " role cards.");
            
        } catch (Exception e) {
            System.out.println("Error during XML parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}