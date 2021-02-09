package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WishListClient {

    private PrintWriter writer = null;
    private boolean connected = false;
    private static final int SERVER_PORT = 8080;
    private static final String HOST = "localhost";
    private static final int C_IDX = 0;
    private static final int U_IDX = 1;
    private static final int P_IDX = 2;

    private void run() {

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                String input = scanner.nextLine();

                String[] tokens = input.split(" ");
                String command = tokens[C_IDX];
                connect();

                if ("register".equals(command) && tokens.length == 3) {
                    String username = tokens[U_IDX];
                    String password = tokens[P_IDX];
                    register(username, password);
                } else if ("login".equals(command) && tokens.length == 3) {
                    String username = tokens[U_IDX];
                    String password = tokens[P_IDX];
                    login(username, password);
                } else if ("disconnect".equals(command)) {
                    disconnect(command);
                    return;
                } else {
                    writer.println(input);
                }
            }
        }
    }

    private void connect() {
        try {
            // if we are already connected - do not open a new socket
            // this case can be reached if we try to connect with a username that is already in use
            if (!connected) {
                Socket socket = new Socket(WishListClient.HOST, WishListClient.SERVER_PORT);
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                this.connected = true;

                ClientRunnable clientRunnable = new ClientRunnable(socket);
                new Thread(clientRunnable).start();
            }
        } catch (IOException e) {
            System.out.printf("[ Cannot connect to server on %s:%d, make sure that the server is started ]",
                    WishListClient.HOST, WishListClient.SERVER_PORT);
        }
    }

    private void register(String username, String password) {
        writer.println("register " + username + " " + password);
    }

    private void login(String username, String password) {
        writer.println("login " + username + " " + password);
    }

    private void disconnect(String disconnect) {
        writer.println(disconnect);
        this.writer.close();
        this.connected = false;
    }

    public static void main(String[] args) {
        new WishListClient().run();
    }

}