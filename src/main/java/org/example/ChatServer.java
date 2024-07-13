package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Chat server started...");

        // loop to accept client connections
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New Client Connected: " + clientSocket.getInetAddress());
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clientHandlers.add(clientHandler);
            clientHandler.start();
        }
    }

    // Inner class to handle each client
    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        // Constructor to initialize the client handler with the client socket
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // Method to handle client communication
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Read client's name
                clientName = in.readLine();
                System.out.println(clientName + " has joined the chat.");
                broadcastMessage(clientName + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientHandlers.remove(this);
                System.out.println(clientName + " has left the chat.");
                broadcastMessage(clientName + " has left the chat.");
            }
        }

        // Method to broadcast a message to all clients
        private void broadcastMessage(String message) {
            synchronized (clientHandlers) {
                for (ClientHandler handler : clientHandlers) {
                    handler.out.println(message);
                }
            }
        }
    }
}
