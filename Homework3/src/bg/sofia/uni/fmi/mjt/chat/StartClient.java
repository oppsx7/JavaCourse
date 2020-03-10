package bg.sofia.uni.fmi.mjt.chat;


import javax.sound.midi.Receiver;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


public class StartClient {

    private static final int PORT = StartServer.port;

    private static final String ADDRESS = "127.0.1";

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        System.out.print("Your name is: ");
        String name = in.nextLine();

        Socket client = new Socket(
                ADDRESS, PORT
        );

        try (InputStream receiver = client.getInputStream();
             PrintWriter writer = new PrintWriter(client.getOutputStream())) {



        String connectionMsg = "nick " + name + "\r";
        writer.println(connectionMsg);

        Receiver receivingFromServer = new Receiver(receiver);

        receivingFromServer.start();

        String command;
        while (!(command = in.nextLine()).equalsIgnoreCase("exit")
                && !command.equalsIgnoreCase("disconnect")) {
            writer.println(command);
        }

        String disconnectMsg = "disconnect " + name + "\r";

        writer.println(disconnectMsg);

        receivingFromServer.stopReceiving();
        receivingFromServer.join();
}
        in.close();
        client.close();
    }

    private static class Receiver extends Thread {

        boolean receive = false;

        InputStream receiver;

        public Receiver(InputStream receiver) {
            this.receiver = receiver;
        }

        @Override
        public void run() {
            this.receive = true;

            while (receive) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(receiver))) {
                    String response = reader.readLine();
                    System.out.println(response);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        public void stopReceiving() {
            this.receive = false;

        }
    }
}
