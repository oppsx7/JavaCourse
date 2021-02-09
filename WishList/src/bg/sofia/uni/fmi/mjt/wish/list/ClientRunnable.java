package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.messages.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRunnable implements Runnable {

    private static final String KILLED = Messages.STOP_ALL_CLIENTS.getMessage();

    private final Socket socket;
    private boolean running = true;

    ClientRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (running) {
                if (socket.isClosed()) {
                    System.out.println("client socket is closed, stop waiting for server messages");
                    return;
                }

                String received = reader.readLine();

                // if the response is the super-random string for disconnection
                // kill the client thread
                if (isKilled(received)) {
                    running = false;
                    System.err.println(Messages.SERVER_CLOSED);
                    System.exit(0);
                } else {
                    System.out.println(received);
                }
            }
        } catch (IOException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println(Messages.DISCONNECT_OK);
            } else if ("Connection reset".equals(e.getMessage())) {
                System.err.println(Messages.SERVER_CLOSED);
            } else {
                System.err.println(e.getMessage());
            }

            System.exit(0);
        }
    }

    private boolean isKilled(String received) {
        return received.split(" ")[1].equals(KILLED);
    }

}