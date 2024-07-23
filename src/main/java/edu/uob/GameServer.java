package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;





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

    public ArrayList<Location> readEntityFile(String entityFileName) {
        Parser parser = new Parser();
        String file = "config" + File.separator + entityFileName;
        FileReader reader = new FileReader(file);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> parts = wholeDocument.getSubgraphs();
        ArrayList<Graph> locations = parts.get(0).getSubgraphs();
        ArrayList<Edge> paths = parts.get(1).getEdges();
        
        ArrayList<Location> locationsList;
       
        for (Graph location : locations) {
            Location currLoc;
            Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            currLoc.addName = locationName;
            ArrayList<Graph> entities = location.getSubgraphs();
            for (Graph entity : entities) {
                Node entityDetails = entity.getNodes(false).get(0);
                String entityName = entityDetails.getId().getId();
                if ("diamond".equals(entityDetails.getAttribute("shape"))){
                   Artefact artefact;
                   artefact.setName(entityName)
                   currLoc.addArtefact(artefact);
                }
                if ("hexagon".equals(entityDetails.getAttribute("shape"))){
                    Furniture furniture;
                    furniture.setName(entityName)
                    currLoc.addFurniture(furniture);
                }
                if ("ellipse".equals(entityDetails.getAttribute("shape"))){
                    Character character;
                    character.setName(entityName)
                    currLoc.addCharacter(character);
                }
            }
            locationsList.add(currLoc);
        }
    
        for (Edge path : paths) {
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId();

            for (Location location : locationsList){
                Path path = (toName, fromName);
                if (location.getName() == fromName){
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
