package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
        if (command.startsWith("look")) {
            response += "The location you are currently in is ???\n";
            response += "There are the following artefacts in this location ???\n";
            response += "There are paths to the following locations ???";
        }
        if (command.startsWith("inv")) {
            response += "You have the following items in your inventory ???";
        }
        return response;
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
