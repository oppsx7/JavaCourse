package bg.sofia.uni.fmi.mjt.chat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.chat.commands.SendMessageCommand;


public class ClientHandler {

    private final ChatClient associatedClient;

    private final ChatServer server;

    private boolean running = false;

    public ClientHandler(ChatClient associatedClient, ChatServer server) {
        this.associatedClient = associatedClient;
        this.server = server;
    }

    public ChatClient getAssociatedClient() {
        return associatedClient;
    }

    @Override
    public boolean equals(Object obj) {
        return this.associatedClient
                .equals(((ClientHandler) obj).associatedClient);
    }

    @Override
    public String toString() {
        return this.associatedClient.toString();
    }

    public void startReceiving() throws IOException {
        ChatServer.print("Thread of " + associatedClient.getName() + " started ");
        this.running = true;

        BufferedReader input = new BufferedReader(new InputStreamReader(this.associatedClient
                .getSocket().getInputStream()));

        while (running) {
            String command = input.readLine();
            String[] arguments = command.split("\\s+");

            switch (arguments[0]) {
                case "send": {
                    SendMessageCommand sendCommand = new SendMessageCommand(
                            arguments[2], associatedClient,
                            new ChatClient(arguments[1], null)
                    );
                    server.send(sendCommand);
                }
                break;

                case "send-all": {
                    SendMessageCommand sendMessageCommand =
                            new SendMessageCommand(arguments[1], associatedClient);
                    server.sendAll(sendMessageCommand);
                }
                break;
                case "disconnect": {
                    server.disconnectUser(this);
                }
                break;

                case "list-users": {
                    SendMessageCommand sendMessageCommand = new SendMessageCommand();
                    server.showListOfUsers(sendMessageCommand);
                }
                break;

            }
        }

    }

    void stopReceiving() {
        this.running = false;
    }

    void receiveMessage(SendMessageCommand sendCommand) {
        try {
            associatedClient.getSocket().getOutputStream()
                    .write(
                            (sendCommand.getSender().getName() + " : "
                                    + sendCommand.getMessage() + '\r')
                                    .getBytes()
                    );

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


}
