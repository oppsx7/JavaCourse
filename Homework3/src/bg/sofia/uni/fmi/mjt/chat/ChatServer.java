package bg.sofia.uni.fmi.mjt.chat;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.chat.commands.SendMessageCommand;


public class ChatServer implements Closeable {

    private static final int DEFAULT_SERVER_PORT = 5050;

    private final ServerSocket socket;

    private boolean running = false;

    private Set<ClientHandler> handlers;

    public ChatServer() throws IOException {
        this.socket = new ServerSocket(DEFAULT_SERVER_PORT);
        handlers = new LinkedHashSet<>();
    }

    public ChatServer(int port) throws IOException {
        this.socket = new ServerSocket(port);
        handlers = new LinkedHashSet<>();
    }

    public void start() {
        this.running = true;

        final Runnable receiver = () -> {
            print("Starting server");
            while (running) {

                try
                {
                    Socket client = this.socket.accept();
                    print( "Got new user!" );

                    InputStream receiving = client.getInputStream();

                    char next = (char) receiving.read();
                    final StringBuilder commandSB = new StringBuilder();

                    if ( receiving.available() > 0 )
                    {
                        while (
                                next != '\r'
                                        && next != '\0'
                        )
                        {
                            commandSB.append( next );
                            next = (char) receiving.read();
                        }
                        print(
                                "Received command: "
                                        + commandSB.toString()
                        );
                    } else
                    {
                        System.err.println( "No available input!" );
                        byte [] msg = ( "We need your username in the form of a command:"
                                + " register <username>"
                                + ", to connect please input it" ).getBytes();
                        client.getOutputStream().write( msg );
                    }

                    final String [] command = commandSB.toString().split( " " );

                    ChatClient clientCredentials = null;

                    if (
                            command[0].equalsIgnoreCase( "nick" )
                                    && clientNotRegistered( command[1] )
                    )
                    {
                        clientCredentials = new ChatClient( command[1] , client );
                    }

                    print( "Creating the handler..." );
                    ClientHandler ch = new ClientHandler(
                            clientCredentials , this
                    );

                    this.handlers.add( ch );

                    Thread receivingMessgages = new Thread( () -> {
                        try
                        {
                            ch.startReceiving();
                        } catch ( IOException e )
                        {
                            e.printStackTrace();
                        }
                    } );

                    receivingMessgages.start();

                } catch ( IOException e )
                {
                    // e.printStackTrace();
                }
            }
        };

        new Thread(receiver).start();
    }

    void addClient(ClientHandler ch) {
        this.handlers.add(ch);
    }

    public boolean clientNotRegistered(String name) {
        for (ClientHandler clientHandler : handlers) {
            if (clientHandler.getAssociatedClient().getName().equals(name))
                return false;
        }
        return true;
    }

    public void stop() throws IOException {
        print("Stopping server");
        this.running = false;

        for (ClientHandler clientHandler : handlers) {
            clientHandler.stopReceiving();
        }

        this.socket.close();
    }

    public Set<ClientHandler> getHandlers() {
        return Collections.unmodifiableSet(this.handlers);
    }


    public void showListOfUsers(SendMessageCommand sendMessageCommand) {
        sendMessageCommand.setMessage(getHandlers().toString());
        for (ClientHandler clientHandler : handlers) {

            clientHandler.receiveMessage(sendMessageCommand);
            break;

        }

    }

    public void send(SendMessageCommand sendCommand) {
        for (ClientHandler clientHandler : handlers) {
            if (clientHandler.getAssociatedClient().getName()
                    .equals(sendCommand.getReceiver().getName()) &&
                    !clientHandler.getAssociatedClient().getName().equals(sendCommand.getSender().getName())) {
                print(
                        "Sending message from " + sendCommand.getSender()
                                + " to " + sendCommand.getReceiver());
                clientHandler.receiveMessage(sendCommand);
                break;
            }
        }
    }

    public void sendAll(SendMessageCommand sendCommand) {
        for (ClientHandler clientHandler : handlers) {
            if (!clientHandler.getAssociatedClient().getName().equalsIgnoreCase(sendCommand.getSender().getName())) {
                clientHandler.receiveMessage(sendCommand);
            }
        }

    }

    public void disconnectUser(ClientHandler client) {
        client.receiveMessage(
                new SendMessageCommand(
                        "Sucessfully disconnected", new ChatClient(
                        "ChatServer", null), client.getAssociatedClient()));
        client.stopReceiving();
        this.handlers.remove(client);
    }


    public static final void print(String str) {
        System.out.println("<ChatServer>: " + str);
    }

    @Override
    public void close() throws IOException {
        this.stop();
    }
}
