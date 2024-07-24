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
import java.util.*;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import edu.uob.Character;



public final class GameServer {

    static ArrayList<Player> players = new ArrayList<>();
    static ArrayList<ArrayList<Location>> maps = new ArrayList<>();

    public static void main(String[] args) throws IOException, ParseException {
        GameServer server = new GameServer();
        server.blockingListenOn(8888);
    }

    public GameServer() {
    }

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
            Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");
            Location currLoc = new Location(locationName, locationDescription);

            //get a list of subgraphs/entities
            ArrayList<Graph> entities = location.getSubgraphs();
            //go through each entity type like artefact, furniture etc
            for (Graph entity : entities) {
                ArrayList<Node> entityNodes = entity.getNodes(false);
                String entityType = entity.getId().getId();
                //go through each node within each entity type
                for (Node node : entityNodes) {
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
            Node fromLocation = edge.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = edge.getTarget().getNode();
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
