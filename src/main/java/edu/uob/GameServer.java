package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;

import edu.uob.Character;



public final class GameServer {

    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        server.blockingListenOn(8888);
    }

    public GameServer() {
    }

    public String handleCommand(String incomming) {
        String username = incomming.split(":")[0].trim();
        String command = incomming.split(":")[1].trim();
        String response = "";
        String filteredCommand = filterCommand(command);
        if (filteredCommand.startsWith("look")) {
            response += "The location you are currently in is ???\n";
            response += "There are the following artefacts in this location ???\n";
            response += "There are paths to the following locations ???";
        }
        if (filteredCommand.startsWith("inv")) {
            response += "You have the following items in your inventory ???";
        }
        return response;
    }

    public ArrayList<Location> readEntityFile(String entityFileName) throws ParseException {
        ArrayList<Location> locationsList = new ArrayList<>();
        Parser parser = new Parser();
        String file = "config" + File.separator + entityFileName;
        try (FileReader reader = new FileReader("config" + File.separator + "entities.dot")) {
            parser.parse(reader);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            return locationsList; // return empty list or handle the error
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return locationsList; // return empty list or handle the error
        }
    
        if (parser.getGraphs().isEmpty()) {
            throw new ParseException("No graphs found in the file.");
        }
        
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> parts = wholeDocument.getSubgraphs();
        ArrayList<Graph> locations = parts.get(0).getSubgraphs();
        ArrayList<Edge> paths = parts.get(1).getEdges();
       
        for (Graph location : locations) {
            //get all locations
            com.alexmerz.graphviz.objects.Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");
            Location currLoc = new Location(locationName, locationDescription);
            
            //get a list of subgraphs/entities 
            ArrayList<Graph> entities = location.getSubgraphs();
            //go through each entity type like artefact, furniture etc
            for (Graph entity : entities) {
                ArrayList<com.alexmerz.graphviz.objects.Node> entityNodes = entity.getNodes(false);
                String entityType = entity.getId().getId();
                //go through each node within each entity type
                for (com.alexmerz.graphviz.objects.Node node : entityNodes) {
                    //get the name and description of the node
                    String entityName = node.getId().getId();
                    String entityDescription = node.getAttribute("description");
                    //check the shape to decide what it is and add to location
                    if (entityType.equals("artefacts")){
                    Artefact artefact = new Artefact(entityName, entityDescription);
                    currLoc.addArtefact(artefact);
                    }
                    if (entityType.equals("furniture")){
                        Furniture furniture = new Furniture(entityName, entityDescription);
                        currLoc.addFurniture(furniture);
                    }
                    if (entityType.equals("characters")){
                        Character character = new Character(entityName, entityDescription);
                        currLoc.addCharacter(character);
                    }
                }
            }
            locationsList.add(currLoc);
        }
    
        for (Edge edge : paths) {
            com.alexmerz.graphviz.objects.Node fromLocation = edge.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            com.alexmerz.graphviz.objects.Node toLocation = edge.getTarget().getNode();
            String toName = toLocation.getId().getId();
            Path path = new Path(toName, fromName);
            for (Location location : locationsList){
                if (location.getName().equals(fromName)){
                    location.addPath(path);
                }
            }
        } 
        return locationsList;
    }

    public ArrayList<Action> loadActionsFile(String entityFileName) throws ParseException {
        ArrayList<Action> actions = new ArrayList<>();

DocumentBuilderFactory factory;
DocumentBuilder builder;
factory = DocumentBuilderFactory.newInstance();
Document document = null;

try {
    builder = factory.newDocumentBuilder();
    String path = "config" + File.separator + entityFileName;
    document = builder.parse(path);
} catch (ParserConfigurationException | IOException | SAXException e) {
    System.err.println("Exception: " + e.getMessage());
}

if (document == null) {
    throw new IllegalStateException("Document is null");
}
Element root = document.getDocumentElement();
NodeList actionNodes = root.getChildNodes();

for (int i = 0; i < actionNodes.getLength(); i++) {
    org.w3c.dom.Node currActionNode = actionNodes.item(i);
    if (currActionNode.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
        continue;
    }
    Element actionElement = (Element) currActionNode;
    ArrayList<String> triggers = new ArrayList<>();
    ArrayList<String> consumedEntities = new ArrayList<>();
    ArrayList<String> producedEntities = new ArrayList<>();
    ArrayList<String> subjects = new ArrayList<>();
    String narration = "";

    // Get the triggers element
    NodeList triggersList = actionElement.getElementsByTagName("triggers");
    if (triggersList.getLength() > 0) {
        Element triggersElement = (Element) triggersList.item(0);
        NodeList keywordList = triggersElement.getElementsByTagName("keyword");
        for (int j = 0; j < keywordList.getLength(); j++) {
            org.w3c.dom.Node keywordNode = keywordList.item(j);
            if (keywordNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element keyword = (Element) keywordNode;
                String triggerPhrase = keyword.getTextContent();
                triggers.add(triggerPhrase);
            } 
        }
    }

    // Get the subjects element
    NodeList subjectsList = actionElement.getElementsByTagName("subjects");
    if (subjectsList.getLength() > 0) {
        Element subjectsElement = (Element) subjectsList.item(0);
        NodeList entityList = subjectsElement.getElementsByTagName("entity");
        for (int j = 0; j < entityList.getLength(); j++) {
            org.w3c.dom.Node entityNode = entityList.item(j);
            if (entityNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element entity = (Element) entityNode;
                String subject = entity.getTextContent();
                subjects.add(subject);
            } 
        }
    }

    // Get the consumed element
    NodeList consumedList = actionElement.getElementsByTagName("consumed");
    if (consumedList.getLength() > 0) {
        Element consumedElement = (Element) consumedList.item(0);
        NodeList entityListConsumed = consumedElement.getElementsByTagName("entity");
        for (int j = 0; j < entityListConsumed.getLength(); j++) {
            org.w3c.dom.Node entityNode = entityListConsumed.item(j);
            if (entityNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element entity = (Element) entityNode;
                String consumedEntity = entity.getTextContent();
                consumedEntities.add(consumedEntity);
            } 
        }
    }

    // Get the produced element
    NodeList producedList = actionElement.getElementsByTagName("produced");
    if (producedList.getLength() > 0) {
        Element producedElement = (Element) producedList.item(0);
        NodeList entityListProduced = producedElement.getElementsByTagName("entity");
        for (int j = 0; j < entityListProduced.getLength(); j++) {
            org.w3c.dom.Node entityNode = entityListProduced.item(j);
            if (entityNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element entity = (Element) entityNode;
                String producedEntity = entity.getTextContent();
                producedEntities.add(producedEntity);
            } 
        }
    }
    // Get the narration element
    NodeList narrationList = actionElement.getElementsByTagName("narration");
    if (narrationList.getLength() > 0) {
        Element narrationElement = (Element) narrationList.item(0);
        narration = narrationElement.getTextContent();
    }

    Action action = new Action(triggers, subjects, consumedEntities, producedEntities, narration);
    actions.add(action);
}
return actions;
}


        
    





    public String filterCommand(String command){
        //remove capital letters and punctuation
        String cleanCommand = command.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
        //tokenise the command (split into seperate words)
        String[] tokens = cleanCommand.split("\\s+");
        
        Set<String> stopwords = new HashSet<>(Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", 
        "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
         "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", 
         "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", 
         "from", "up", "down", "in", "out", "on", "off", "over", "under", "again","further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
          "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"));
        
        //remove stop words
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopwords.contains(token)) {
                filteredTokens.add(token);
            }
        }

        //make a single string from filtered tokens
        String filteredCommand = String.join(" ", filteredTokens);
        return filteredCommand;
    }

    // Networking method - you shouldn't need to chenge this method !
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    // Networking method - you shouldn't need to chenge this method !
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        final char END_OF_TRANSMISSION = 4;
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
