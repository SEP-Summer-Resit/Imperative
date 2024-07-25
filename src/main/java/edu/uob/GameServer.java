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



public final class GameServer {

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<ArrayList<Location>> maps = new ArrayList<>();
    private ArrayList<Action> actions = new ArrayList<>();

    public static void main(String[] args) throws IOException, ParseException {
        GameServer server = new GameServer();
        server.blockingListenOn(8888);
    }

    // Return map specific to player at index 'p' in players/maps lists.
    public ArrayList<Location> getMap(int p) {
        return maps.get(p);
    }

    // Return Player class for player p in players/maps lists.
    public Player getPlayer(int p) {
        return players.get(p);
    }

    public GameServer() {
        try {
            actions = loadActionsFile("actions.xml");
        } catch (ParseException e) {
            System.err.println("Error parsing actions file: " + e.getMessage());
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
    public String lookCommand(Player player, ArrayList<Location> map, String command) {
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
    public String invCommand(Player player, ArrayList<Location> map, String command) {
        String response = "";

        if (!player.getInventory().isEmpty()) {
            response += "You have the following items in your inventory:\n";
            for (int i = 0; i < player.getInventory().size(); i++) {
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
    public String gotoCommand(Player player, ArrayList<Location> map, String command) {
        int newLocation = 0;
        boolean moving = false;
        String response = "";
        String intendedLocation;
        Location currentLocation = map.get(player.getLocation());

        // Check that the command only contains 'goto' and 'location', two words exactly.
        if(!command.trim().matches("^\\s*\\w+\\s+\\w+\\s*$")) {
            response += "You must provide a valid location you wish to move to.\n";
            return response;
        }

        // Get the intended location from the command.
        intendedLocation = command.split(" ")[1].trim();

        // Check for a matching location in the possible paths out of current location.
        foundMatch:
        // Search through the available paths for a match
        for (int i = 0; i < currentLocation.getPathsOut().size(); i++) {
            if (currentLocation.getPathsOut().get(i).getDestination().equals(intendedLocation)) {
                // If a match is found, get the location within the 'maps' structure
                for (newLocation = 0; newLocation < map.size(); newLocation++) {
                    if(currentLocation.getPathsOut().get(i).getDestination().equals(map.get(newLocation).getName())) {
                        moving = true;
                        break foundMatch;
                    }
                }
                // We should never reach this. TODO: Error state.
                // If we reach this then the entities.dot file contains errors.
                break;
            }
        }

        if (!moving) {
            response += "'" + intendedLocation + "' is not a location you can travel to.\n";
        }
        else {
            // Move the player
            player.setLocation(newLocation);
            response += "You have moved to " + map.get(newLocation).getName() + "\n";
        }

        return response;
    }

    // Move artefact from location to inventory if it's available to take
    // Return a response to the 'get' command being sent
    public String getCommand(Player player, ArrayList<Location> map, String command) {
        String response = "";
        boolean artefactTaken = false;
        String intendedArtefact;
        Location currentLocation = map.get(player.getLocation());

        // Check that the command only contains 'get' and 'artefact', two words exactly.
        if(!command.trim().matches("^\\s*\\w+\\s+\\w+\\s*$")) {
            response += "You must provide a valid artefact you wish to get.\n";
            return response;
        }

        // Get the intended location from the command.
        intendedArtefact = command.split(" ")[1].trim();

        for (int i = 0; i < currentLocation.getArtefacts().size(); i++) {
            Artefact artefact = currentLocation.getArtefacts().get(i);
            if(artefact.getName().equals(intendedArtefact)) {
                player.addArtefactToInventory(artefact);
                currentLocation.removeArtefact(artefact);
                response += "You now have '" + artefact.getName() + "' in your inventory\n";
                artefactTaken = true;
                break;
            }
        }

        if(!artefactTaken) {
            response += "'" + intendedArtefact + "' is not available to take.\n";
        }

        return response;
    }

    // Move artefact from inventory to the current location if artefact exists in inventory.
    // Return a response to the 'drop' command being sent.
    public String dropCommand(Player player, ArrayList<Location> map, String command) {
        String response = "";
        boolean artefactDropped = false;
        String intendedArtefact;
        Location currentLocation = map.get(player.getLocation());

        // Check that the command only contains 'get' and 'artefact', two words exactly.
        if(!command.trim().matches("^\\s*\\w+\\s+\\w+\\s*$")) {
            response += "You must provide a valid artefact you wish to drop from your inventory.\n";
            return response;
        }

        // Get the intended location from the command.
        intendedArtefact = command.split(" ")[1].trim();

        for (int i = 0; i < player.getInventory().size(); i++) {
            Artefact artefact = player.getInventory().get(i);
            if(artefact.getName().equals(intendedArtefact)) {
                currentLocation.addArtefact(artefact);
                player.removeArtefactFromInventory(artefact);
                response += "You have dropped '" + artefact.getName() + "' from your inventory\n";
                artefactDropped = true;
                break;
            }
        }

        if(!artefactDropped) {
            response += "'" + intendedArtefact + "' is not in your inventory.\n";
        }

        return response;
    }

    // Reset game state for the given player.
    // Return a response to the 'reset' command.
    public String resetCommand(Player player, ArrayList<Location> map, String command, int p) throws ParseException {
        String response = "";
        ArrayList<Location> locations;
        String username = player.getName();

        // Check that the command is precisely 'reset'
        if(!command.trim().matches("reset")) {
            response += "To reset the game please simply type 'reset'\n";
            return response;
        }

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
        String filteredCommand = filterCommand(command);
        String response = "";

        p = findPlayer(username);
        player = players.get(p);
        map = maps.get(p);
        currentLocation = map.get(player.getLocation());

        if (filteredCommand.startsWith("look")) {
            response += lookCommand(player, map, filteredCommand);
        }
        else if (filteredCommand.startsWith("inv")) {
            response += invCommand(player, map, filteredCommand);
        }
        else if (filteredCommand.startsWith("goto")) {
            response += gotoCommand(player, map, filteredCommand);
        }
        else if (filteredCommand.startsWith("get")) {
            response += getCommand(player, map, filteredCommand);
        }
        else if (filteredCommand.startsWith("drop")) {
            response += dropCommand(player, map, filteredCommand);
        }
        else if (filteredCommand.startsWith("reset")) {
            response += resetCommand(player, map, filteredCommand, p);
        }

        Boolean actionValid = false;
        // Check through all the actions to see if the command matches any of the triggers
        for (Action action : actions){
            for (String trigger : action.getTriggers()){
                if (filteredCommand.startsWith(trigger)){
                    actionValid = true;
                    // loop through the subjects of the action
                    for (String subject : action.getSubjects()){
                        // check if the subject is in the command
                        if (filteredCommand.contains(subject)){
                            // check if the subject is in the players inventory
                            if (player.getInventory().contains(new Artefact(subject, ""))){
                                continue;
                            }
                            // or if its on the ground
                            else if (currentLocation.hasArtefact(subject)){
                                continue;
                            }
                            // if not the command is not valid
                            else {
                                response += "You do not have the required item to perform this action\n";
                                actionValid = false;
                                break;
                            }
                        }
                        else{
                            response += "The" + subject + " is required to perform this action\n";
                            actionValid = false;
                            break;
                        
                        }
                    }
                }
            }
            if (actionValid == true) {
                response += action.getNarration() + "\n";
                break;
            }
        }
    
        if (actionValid == false){
            response += "There is no action " + filteredCommand + "\n";
        }



        

        return response;
    }

    /**
     * Consumption Function: Removes 'item' from 'container' and adds it to the storeroom.
     *
     * The item passed here can be either an 'Artefact', or a 'Furniture' type.
     * If the item is an Artefact, the function expects that the container will be a player's inventory.
     * If the item is a Furniture, the function expects that the container will be a location's list of furniture.
     *
     * @param p         Player index within the players list.
     * @param item      Entity that will be removed from container & added to storeroom.
     * @param container Containing List that will have 'item' removed from it.
     */
    public void consumption(int p, Entity item, List<?> container) {
        Location storeroom = maps.get(p).get(5);
        if(container.contains(item)) {
            if(item instanceof Artefact) {
                storeroom.getArtefacts().add((Artefact) item);
            }
            else if(item instanceof Furniture) {
                storeroom.getFurniture().add((Furniture) item);
            }
            else {
                // We should never reach this. TODO: Error state
                System.out.println("ERROR: " + item.getName() + " is not a valid type");
            }

            container.remove(item);
        }
        else {
            // We should never reach this. TODO: Error state
            System.out.println("ERROR: " + item.getName() + " is not able to be consumed\n");
        }
    }

    /**
     * Production Function: Removes an 'item' from the storeroom and adds it to 'destination'
     *
     * The item passed here can be an artefact, furniture, or character.
     * If the item is an artefact, we expect that the destination will be the player's inventory
     * If the item is furniture, we expect that the destination will be a location.getFurniture().
     * If the item is a character, we expect that the destination will be a location.getCharacters().
     *
     * (This function may have unintended errors if you pass an invalid destination)
     *
     * @param p             Player index within the players list.
     * @param item          Entity that will be removed from storeroom & added to the destination.
     * @param destination   ArrayList<Entity> that will have the item added to it.
     *                      eg: player's inventory
     *                          location's furniture list
     *                          location's character list
     */
    public void production(int p, Entity item, Object destination) {
        Location storeroom = maps.get(p).get(5);
        List<Entity> listDestination = (List<Entity>) destination;
        listDestination.add(item);

        if(item instanceof Artefact) {
            storeroom.getArtefacts().remove(item);
        }
        else if (item instanceof Furniture) {
            storeroom.getFurniture().remove(item);
        }
        else if(item instanceof Character) {
            storeroom.getCharacters().remove(item);
        }
        else {
            // We should never reach this. TODO: Error state
            System.out.println("ERROR: " + item.getName() + " is not a valid type\n");
        }
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