package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.messages.Messages;
import bg.sofia.uni.fmi.mjt.wish.list.services.WishListService;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class WishListServer extends WishListService {

    private final Map<Pair<String, String>, SocketChannel> users = new HashMap<>();
    private final Map<String, Boolean> loggedUsers = new HashMap<>();
    private final Map<String, ArrayList<String>> wishes = new HashMap<>();
    private static final int DEFAULT_PORT = 8080;

    public WishListServer(int port) throws IOException {
        super(DEFAULT_PORT);

        System.out.printf("--- server started on: %s%n", InetAddress.getLocalHost().getHostAddress());
    }

    public WishListServer() throws IOException {
        super(8080);
        System.out.printf("--- server started on: %s%n", InetAddress.getLocalHost().getHostAddress());
    }

    public void start() throws IOException {
        while (runServer) {
            int readyChannels = selector.select();

            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                }

                keyIterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(this.selector, SelectionKey.OP_READ);

    }

    private void read(SelectionKey key) {

        SocketChannel sc = (SocketChannel) key.channel();

        this.commandBuffer.clear();
        try {
            sc.read(this.commandBuffer);
            this.commandBuffer.flip();

            String command = StandardCharsets.UTF_8.decode(this.commandBuffer).toString();
            Messages response = executeCommand(command, sc);

            // no response needed for these cases
            if (response == Messages.RESPONSE_TEXT && "".equals(response.getMessage())) {
                return;
            }

            if (response == Messages.DISCONNECT_OK) {
                return;
            }

            // internal server error - stop the server
            if (response == Messages.DISCONNECT_FAIL) {
                System.err.println("--- could not close а socket");
                this.stop();
            }

            String responseText = response.getMessage();

            this.commandBuffer.clear();
            this.commandBuffer.put(responseText.getBytes());
            this.commandBuffer.put(System.lineSeparator().getBytes());

            this.commandBuffer.flip();
            while (commandBuffer.hasRemaining()) {
                sc.write(commandBuffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Messages executeCommand(String recvMsg, SocketChannel sc) {
        recvMsg = recvMsg.trim().replaceAll(" +", " ");

        String[] cmdParts = recvMsg.split(" ");

        if (cmdParts.length == 0) {
            return Messages.NO_COMMAND;
        }

        String command = cmdParts[0].trim();

        if ("register".equals(command)) {
            if (cmdParts.length < 2) {
                return Messages.MISSING_USERNAME_AND_PASSWORD;
            }

            String username = cmdParts[1].trim();
            String password = cmdParts[2].trim();
            return registerUser(username, password, sc);
        } else if ("login".equals(command)) {
            return login(cmdParts);
        } else if ("disconnect".equals(command)) {
            return disconnectClient(sc);
        } else if (users.containsValue(sc) && checkIfUserIsLogged(sc)) {
            switch (command) {
                case "logout":
                    return logout(sc);
                case "get-wish":
                    return listWishes(sc);
                case "post-wish":
                    return postWish(recvMsg);
                default:
                    return Messages.UNKNOWN_COMMAND;
            }
        } else {
            return Messages.NOT_LOGGED_IN;
        }

    }

    public boolean checkIfUserIsLogged(SocketChannel sc) {
        boolean result;
        List<Pair<String, String>> userList = new ArrayList<>(getKeysByValue(users, sc));
        for (Pair<String, String> user : userList) {
            if (loggedUsers.containsKey(user.getFirst())) {
                result = loggedUsers.get(user.getFirst());
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public Messages registerUser(String username, String password, SocketChannel sc) {

        if (!username.matches("[a-zA-Z\\-_.]+")) {
            Messages.BUFFER_MESSAGE.setMessage(String.format(Messages.IVALID_USERNAME.getMessage(), username));
            return Messages.BUFFER_MESSAGE;
        }
        for (Pair<String, String> pair : users.keySet()) {
            if (pair.getFirst().equals(username)) {
                Messages.BUFFER_MESSAGE.setMessage(String.format(Messages.USERNAME_EXISTS.getMessage(), username));
                return Messages.BUFFER_MESSAGE;
            }
        }
        users.put(new Pair<>(username, password), sc);
        loggedUsers.put(username, true);
        System.out.printf("%s has registered%n", username);
        String message = String.format(Messages.SUCCESSFULLY_REGISTERED.getMessage(), username);
        Messages.BUFFER_MESSAGE.setMessage(message);
        return Messages.BUFFER_MESSAGE;

    }

    public Messages disconnectClient(SocketChannel sc) {

        try {
            sc.close();
        } catch (IOException e) {
            return Messages.DISCONNECT_FAIL;
        }
        if (users.isEmpty()) {
            return Messages.DISCONNECT_OK;
        }

        List<Pair<String, String>> userList = new ArrayList<>(getKeysByValue(users, sc));
        for (Pair<String, String> user : userList) {
            String username = user.getFirst();
            users.remove(user);
            if (loggedUsers.containsKey(username)) {
                loggedUsers.put(username, false);
            }
            System.out.printf("%s has disconnected%n", username);
        }
        return Messages.DISCONNECT_OK;
    }

    public Messages login(String[] credentials) {
        if (credentials.length < 2) {
            return Messages.MISSING_USERNAME_AND_PASSWORD;
        }

        String username = credentials[1].trim();
        String password = credentials[2].trim();

        if (users.containsKey(new Pair<>(username, password))) {
            if (loggedUsers.get(username)) {
                return Messages.ALREADY_LOGGED_IN;
            }
            loggedUsers.put(username, true);
            Messages.BUFFER_MESSAGE.setMessage(String.format(Messages.SUCCESSFULLY_LOGGED_IN.getMessage(), username));
            return Messages.BUFFER_MESSAGE;
        }

        return Messages.INVALID_COMBINATION;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Messages logout(SocketChannel sc) {
        List<Pair<String, String>> userList = new ArrayList<>(getKeysByValue(users, sc));
        for (Pair<String, String> user : userList) {
            if (loggedUsers.containsKey(user.getFirst())) {
                loggedUsers.put(user.getFirst(), false);
                return Messages.SUCCESSFULLY_LOGGED_OUT;
            }
        }
        return Messages.NOT_LOGGED_IN;
    }

    public Messages listWishes(SocketChannel sc) {
        if (wishes.isEmpty()) {
            return Messages.NO_STUDENTS_IN_WISH_LIST;
        }

        String currentUser = getCurrentUser(sc);

        StringBuilder list = new StringBuilder();
        String user = getRandomUser(wishes, currentUser);

        if (user.equals("SAME_USER")) {
            return Messages.CANT_VIEW_OWN_GIFTS;
        }


        list.append("[ ").append(user).append(": ").append("[");

        for (int i = 0; i < wishes.get(user).size(); i++) {
            String wish = wishes.get(user).get(i);
            list.append(wish);
            if (i != wishes.get(user).size() - 1) {
                list.append(", ");
            } else {
                list.append("] ]");
            }
        }
        Messages.RESPONSE_TEXT.setMessage(list.toString());
        return Messages.RESPONSE_TEXT;
    }

    public String getCurrentUser(SocketChannel sc) {
        String currentUser = "";
        List<Pair<String, String>> userList = new ArrayList<>(getKeysByValue(users, sc));
        for (Pair<String, String> user : userList) {
            if (loggedUsers.containsKey(user.getFirst())) {
                currentUser = user.getFirst();
            }
        }
        return currentUser;
    }

    public String getRandomUser(Map<String, ArrayList<String>> wishes, String currentUser) {
        List<String> userList = new ArrayList<>(wishes.keySet());
        int randomIndex = new Random().nextInt(userList.size());
        String randomUser = userList.get(randomIndex);
        while (randomUser.equals(currentUser)) {
            if (userList.size() == 1) {
                return "SAME_USER";
            }
            randomUser = userList.get(randomIndex);
        }
        return randomUser;
    }

    public Messages postWish(String line) {
        String[] lineParts = line.split(" ");

        try {
            String user = lineParts[1];
            String wish = lineParts[2];
            boolean userExists = false;
            for (Pair<String, String> pair : users.keySet()) {
                if (pair.getFirst().equals(user)) {
                    userExists = true;
                    break;
                }
            }
            if (!userExists) {
                Messages.BUFFER_MESSAGE
                        .setMessage(String.format(Messages.STUDENT_NOT_REGISTERED.getMessage(), user));
                return Messages.BUFFER_MESSAGE;
            }
            ArrayList<String> tempList;
            if (wishes.containsKey(user)) {
                tempList = wishes.get(user);
                if (!tempList.contains(wish)) {
                    tempList.add(wish);
                } else {
                    Messages.BUFFER_MESSAGE
                            .setMessage(String.format(Messages.GIFT_ALREADY_SUBMITTED.getMessage(), user));
                    return Messages.BUFFER_MESSAGE;
                }
            } else {
                tempList = new ArrayList<>();
                tempList.add(wish);
            }
            wishes.put(user, tempList);

            Messages.BUFFER_MESSAGE
                    .setMessage(String.format(Messages.GIFT_SUBMITTED_SUCCESSFULLY.getMessage(), wish, user));
            return Messages.BUFFER_MESSAGE;

        } catch (Exception e) {
            Messages.RESPONSE_TEXT.setMessage(e.getMessage());
            return Messages.RESPONSE_TEXT;
        }
    }

    // if the server shutdowns - send a proper message to all currently connected users and disconnect them
    private void killClients() {
        HashSet<SocketChannel> tempSet = new HashSet<>(users.values());
        for (SocketChannel sc : tempSet) {
            disconnectClient(sc);
        }
    }

    private void stop() {
        try {
            this.runServer = false;

            this.killClients();

            final int second = 1000;
            Thread.sleep(second);

            System.out.printf("sockets still opened: %d%n", users.size());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        this.stop();

        this.selector.close();
        this.commandBuffer.clear();
        this.serverSocketChannel.close();
    }

    public static void main(String[] args) {
        final int serverPort = 8080;

        try (WishListServer es = new WishListServer(serverPort)) {
            es.start();
        } catch (IOException e) {
            System.err.println("Server could not be started");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Server could not be auto-closed");
            e.printStackTrace();
        }
    }
}