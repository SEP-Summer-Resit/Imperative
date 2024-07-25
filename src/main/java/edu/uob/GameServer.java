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
import java.util.Objects;
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
//import sun.tools.serialver.resources.serialver;



public final class GameServer {

    static ArrayList<Player> players = new ArrayList<>();
    static ArrayList<ArrayList<Location>> maps = new ArrayList<>();
    public Set<String> validTriggers = new HashSet<>(Arrays.asList("look", "inv", "goto", "get", "drop", "reset"));
    public Set<String> validSubjects = new HashSet<>(Arrays.asList());
    List<Action> actions;
    

    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<Action> actions = loadActionsFile("actions.xml");
        ArrayList<Location> locations = readEntityFile("actions.xml");
        GameServer server = new GameServer(actions, locations);
        server.blockingListenOn(8888);
    }

    public GameServer(){
    }
    public GameServer(ArrayList<Action> actions, ArrayList<Location> locations ) {
        this.actions = actions;
        for (int i = 0; i < locations.size()-1; i++) {
            maps.add(new ArrayList<>(locations)); 
        }
        for (Action action : actions){
            this.validTriggers.addAll(action.getTriggers());
        }
        for (Location location: locations){
            for (Artefact artefact: location.getArtefacts()){
                this.validSubjects.add(artefact.getName());
            }
            for (Furniture furniture: location.getFurniture()){
                this.validSubjects.add(furniture.getName());
            }
            for (Character character : location.getCharacters()){
                this.validSubjects.add(character.getName());
            }
            this.validSubjects.add(location.getName());
        }
    }

    // Returns an integer identifying where in the players list the current player exists.
    // Creates a new player and adds to the players list if the username hasn't been previously used.
    // Creates the corresponding map in the maps list for the given player if it doesn't already exist.
    public int findPlayer(String username) throws ParseException {
        int i;
        Player player;

        if (players.size() <= 0) {
            player = new Player(username, new ArrayList<>());
            players.add(player);
            System.out.println("Adding first player: " + username);

            ArrayList<Location> locations = readEntityFile("entities.dot");
            maps.add(locations);
            return 0;
        }

        for (i = 0; i < players.size(); i++) {
            if(Objects.equals(players.get(i).getName(), username)) {
                // System.out.println("Found player: " + players.get(i).getName());
                return i;
            }
        }

        player = new Player(username, new ArrayList<>());
        players.add(player);
        ArrayList<Location> locations = readEntityFile("entities.dot");
        maps.add(locations);
        System.out.println("Adding new player: " + username);
        return i;
    }

    // Return a response to the 'look' command being sent by a player
    public String lookCommand(Player player, ArrayList<Location> map) {
        Location currentLocation = map.get(player.getLocation());
        String response = "";

        response += "You are in " +
                // Uncapitalize the first letter of description which comes in the middle of the sentence.
                java.lang.Character.toLowerCase(currentLocation.getDescription().charAt(0)) +
                currentLocation.getDescription().substring(1) + "\n";
        // Characters
        if(!currentLocation.getCharacters().isEmpty()) {
            response += "The following characters are here:\n";
            for (int i = 0; i < currentLocation.getCharacters().size(); i++) {
                response += "* " + currentLocation.getCharacters().get(i).getName() + "\n";
            }
        }
        else {
            response += "There are no characters here.\n";
        }
        // Artefacts
        if(!currentLocation.getArtefacts().isEmpty()) {
            response += "There are the following artefacts in this location: \n";
            for (int i = 0; i < currentLocation.getArtefacts().size(); i++) {
                response += "* " + currentLocation.getArtefacts().get(i).getName() + "\n";
            }
        }
        else {
            response += "There are no artefacts in this location\n";
        }
        // Furniture
        if(!currentLocation.getFurniture().isEmpty()) {
            response += "In the " + currentLocation.getName() + " there are:\n";
            for (int i = 0; i < currentLocation.getFurniture().size(); i++) {
                response += "* " + currentLocation.getFurniture().get(i).getName() + "\n";
            }
        }
        // Paths Out
        if(!currentLocation.getPathsOut().isEmpty()) {
            response += "There are paths to the following locations: \n";
            for (int i = 0; i < currentLocation.getPathsOut().size(); i++) {
                response += "* " + currentLocation.getPathsOut().get(i).getDestination() + "\n";
            }
        }
        else {
            response += "There are no paths from here\n";
        }

        return response;
    }

    // Return a response to the 'inv' command being sent by a player
    public String invCommand(Player player, ArrayList<Location> map) {
        String response = "";

        if (!player.getInventory().isEmpty()) {
            response += "You have the following items in your inventory:\n";
            for (int i = 0; i < player.getInventory().size() -1; i++) {
                response += "* " + player.getInventory().get(i).getName() + "\n";
            }
        }
        else {
            response += "You have no items in your inventory\n";
        }
        return response;
    }

    // Move player to a new location if the location provided is valid
    // Return a response to the 'goto' command being sent
    public String gotoCommand(Player player, ArrayList<Location> map, Set<String> subjects) {
        int newLocation = 0;
        boolean moving = false;
        String response = "";
        Location currentLocation = map.get(player.getLocation());
        Set<Location> validLocation = new HashSet<>();

        // Search through the available paths for a match
        for (int i = 0; i < currentLocation.getPathsOut().size(); i++) {
        //check if subjects contains a valid destination
            if (subjects.contains(currentLocation.getPathsOut().get(i).getDestination())) {
                // If a match is found, get the location within the 'maps' structure
                for (newLocation = 0; newLocation < map.size(); newLocation++) {
                    if(currentLocation.getPathsOut().get(i).getDestination().equals(map.get(newLocation).getName())) {
                        validLocation.add(map.get(newLocation));
                    }
                }
                // We should never reach this. TODO: Error state.
                // If we reach this then the entities.dot file contains errors.
                break;
            }
        }
        
        
        if (validLocation.size() == 1){
            // Move the player
            player.setLocation(newLocation);
            response += "You have moved to " + map.get(newLocation).getName() + "\n";
        }else if (validLocation.size() > 1){
            response += "Please choose one location to travel too";
        }
       
        if (!moving) {
            response += "You must provide a valid location you wish to move to\n";
        }
    
        return response;
    }

    // Move artefact from location to inventory if it's available to take
    // Return a response to the 'get' command being sent
    public String getCommand(Player player, ArrayList<Location> map, Set<String> subjects) {
        String response = "";
        boolean artefactTaken = false;
        Location currentLocation = map.get(player.getLocation());
        Set<Artefact> validArtefact = new HashSet<>();
        
        //interate over artefacts in the area and see if they match artefacts in command
        //add valid artefacts to a list
        for (int i = 0; i < currentLocation.getArtefacts().size(); i++) {
            Artefact artefact = currentLocation.getArtefacts().get(i);
            if(subjects.contains(artefact.getName())) {
                validArtefact.add(artefact);
            }
        }

        //if only one valid artefact listed then pick up else give warning to only pick up one
        if (validArtefact.size() == 1){
            Artefact artefact = validArtefact.iterator().next();
            player.addArtefactToInventory(artefact);
            currentLocation.removeArtefact(artefact);
            response += "You now have '" + artefact.getName() + "' in your inventory\n";
            artefactTaken = true;
        }else if (validArtefact.size() > 1){
            response += "You can only pick up one artefact at a time.";
        }
        
        if(!artefactTaken) {
            response += "You must provide a valid item to pick up.";
        }
        return response;
    }

    // Move artefact from inventory to the current location if artefact exists in inventory.
    // Return a response to the 'drop' command being sent.
    public String dropCommand(Player player, ArrayList<Location> map, Set<String> subjects) {
        String response = "";
        boolean artefactDropped = false;
        Location currentLocation = map.get(player.getLocation());
        Set<Artefact> validArtefact = new HashSet<>();
        
        for (int i = 0; i < player.getInventory().size(); i++) {
            Artefact artefact = player.getInventory().get(i);
            if(subjects.contains(artefact.getName())) {
                validArtefact.add(artefact);
            }
        }

        if (validArtefact.size() == 1){
            Artefact artefact = validArtefact.iterator().next();
            currentLocation.addArtefact(artefact);
            player.removeArtefactFromInventory(artefact);
            response += "You have dropped '" + artefact.getName() + "' from your inventory\n";
            artefactDropped = true;
        }else if (validArtefact.size() > 1){
            response += "Only drop one artefact at a time";
        }
        
        if(!artefactDropped) {
            response += "Artefact is not in your inventory.\n";
        }
        return response;
    }

    // Reset game state for the given player.
    // Return a response to the 'reset' command.
    public String resetCommand(Player player, ArrayList<Location> map, int p) throws ParseException {
        String response = "";
        ArrayList<Location> locations;
        String username = player.getName();

        System.out.println("Resetting player: " + username);

        // Reset the current player
        players.set(p, new Player(username, new ArrayList<>()));

        // Reset the current player's map
        locations = readEntityFile("entities.dot");
        maps.set(p, locations);

        response += "Game reset.\n";
        return response;
    }

    // Handle an incoming command from a player
    public String handleCommand(String incomming) throws ParseException {
        int p;
        Player player;
        ArrayList<Location> map;
        Location currentLocation;

        String username = incomming.split(":")[0].trim();
        String command = incomming.split(":")[1].trim();
        String response = "";
        
        List<Set<String>> filteredCommand = produceValidCommand(filterCommand(command));
        Set<String> triggers = filteredCommand.get(0);
        Set<String> subjects = filteredCommand.get(1);
        p = findPlayer(username);
        player = players.get(p);
        map = maps.get(p);
        currentLocation = map.get(player.getLocation());

        if ((triggers.size() > 1) || (triggers.size() < 1)){
            response = "Please give one valid command.";
        }else{
            if (triggers.contains("look")) {
                response += lookCommand(player, map);
            }
            else if (triggers.contains("inv")) {
                response += invCommand(player, map);
            }
            else if (triggers.contains("goto")) {
                response += gotoCommand(player, map, subjects);
            }
            else if (triggers.contains("get")) {
                response += getCommand(player, map, subjects);
            }
            else if (triggers.contains("drop")) {
                response += dropCommand(player, map, subjects);
            }
            else if (triggers.contains("reset")) {
                response += resetCommand(player, map, p);
            }
        }
        return response;
    }

    public List<Set<String>> produceValidCommand(Set<String> actualCommand){
        Set<String> triggers = new HashSet<>();
        Set<String> subjects = new HashSet<>();

        // Categorize the commands
        for (String command : actualCommand) {
            if (validTriggers.contains(command)) {
                triggers.add(command);
            }
            if (validSubjects.contains(command)) {
                subjects.add(command);
            }
        }

        // Create a set containing both sets
        List<Set<String>> validCommand = new ArrayList<>();
        validCommand.add(triggers);
        validCommand.add(subjects);

        return validCommand;
    }

     

    

    public static ArrayList<Location> readEntityFile(String entityFileName) throws ParseException {
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

  
  
  
    public static ArrayList<Action> loadActionsFile(String entityFileName) throws ParseException {
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


    public Set<String> filterCommand(String command){
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
        Set<String> filteredTokens = new HashSet<>();
        for (String token : tokens) {
            if (!stopwords.contains(token)) {
                filteredTokens.add(token);
            }
        }

        return filteredTokens;
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
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Networking method - you shouldn't need to chenge this method !
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException, ParseException {
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
