package deadwood.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import org.xml.sax.SAXException;
import java.io.IOException;

public class ParseXML {
    // Method to get a Document object from an XML file
    public Document getDocFromFile(String filename) throws ParserConfigurationException, SAXException, IOException {
        // Create a DocumentBuilder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        File xmlFile = new File("resources/xml/" + filename);
        System.out.println("Trying to load XML file from: " + xmlFile.getAbsolutePath());
        System.out.println("File exists: " + xmlFile.exists());
        // Parse the XML file and return the Document
        return db.parse(xmlFile);
    }
}