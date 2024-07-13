package org.example;

import java.io.*;
import java.net.*;

public class ChatClientTwo {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    // constructor to initialize the client with the server address port
    public ChatClientTwo(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Connected to chat server");

        new Thread(new ReadMessages()).start();
        String userInput;
        System.out.println("Enter your name: ");
        out.println(stdIn.readLine());

        // loop to read user input & send it to the server
        while ((userInput = stdIn.readLine()) != null){
            out.println(userInput);
        }
    }

    // Inner class to handle reading messages from the server
    private class ReadMessages implements Runnable {
        public void run() {
            String serverMessage;
            try {
                while((serverMessage = in.readLine()) != null){
                    System.out.println(serverMessage);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    // Main method to run the client
    public static void main(String[] args) throws IOException {
        new ChatClient("127.0.0.1", 12345);
    }
}

