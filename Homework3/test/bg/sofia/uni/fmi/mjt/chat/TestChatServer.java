package bg.sofia.uni.fmi.mjt.chat;

import bg.sofia.uni.fmi.mjt.chat.ChatClient;
import bg.sofia.uni.fmi.mjt.chat.ChatServer;
import bg.sofia.uni.fmi.mjt.chat.ClientHandler;
import bg.sofia.uni.fmi.mjt.chat.commands.SendMessageCommand;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class TestChatServer {

    @Mock
    private ChatClient client;
    @Mock
    private ClientHandler handler;

    private ChatServer server;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(client.getName()).thenReturn("user");
        when(handler.getAssociatedClient()).thenReturn(client);
        server = new ChatServer();
    }

    @After
    public void close() throws IOException {
        server.stop();
    }

    @Test
    public void testClientNotRegistered() {
        boolean result = server.clientNotRegistered("user");

        Assert.assertTrue(result);
    }

    @Test
    public void testClientRegistered() {

        server.addClient(handler);

        boolean result = server.clientNotRegistered("user");

        Assert.assertFalse(result);
    }

    @Test
    public void testStop() throws IOException {
        server.addClient(handler);

        server.stop();

        verify(handler).stopReceiving();
    }

    @Test
    public void testSend() {
        ClientHandler secondHandler = mock(ClientHandler.class);
        ChatClient secondClient = mock(ChatClient.class);
        when(secondClient.getName()).thenReturn("secondUser");
        when(secondHandler.getAssociatedClient()).thenReturn(secondClient);

        server.addClient(handler);
        server.addClient(secondHandler);

        SendMessageCommand command = new SendMessageCommand("message", client, secondClient);

        server.send(command);

        verify(secondHandler).receiveMessage(command);
    }

    @Test
    public void testSendAll() {
        ClientHandler secondHandler = mock(ClientHandler.class);
        ClientHandler thirdHandler = mock(ClientHandler.class);
        ChatClient secondClient = mock(ChatClient.class);
        ChatClient thirdClient = mock(ChatClient.class);
        when(secondClient.getName()).thenReturn("secondUser");
        when(secondHandler.getAssociatedClient()).thenReturn(secondClient);
        when(thirdClient.getName()).thenReturn("thirdUser");
        when(thirdHandler.getAssociatedClient()).thenReturn(thirdClient);

        server.addClient(handler);
        server.addClient(secondHandler);
        server.addClient(thirdHandler);

        SendMessageCommand command = new SendMessageCommand("message", client);

        server.sendAll(command);

        verify(secondHandler).receiveMessage(command);
        verify(thirdHandler).receiveMessage(command);
    }

    @Test
    public void testShowListOfUsers(){
        ClientHandler secondHandler = mock(ClientHandler.class);
        ClientHandler thirdHandler = mock(ClientHandler.class);
        ChatClient secondClient = mock(ChatClient.class);
        ChatClient thirdClient = mock(ChatClient.class);
        when(secondClient.getName()).thenReturn("secondUser");
        when(secondHandler.getAssociatedClient()).thenReturn(secondClient);
        when(thirdClient.getName()).thenReturn("thirdUser");
        when(thirdHandler.getAssociatedClient()).thenReturn(thirdClient);

        server.addClient(handler);
        server.addClient(secondHandler);
        server.addClient(thirdHandler);

        SendMessageCommand command = new SendMessageCommand();

        server.showListOfUsers(command);

        verify(handler).receiveMessage(command);
    }

    @Test
    public void testDisconnect(){

        server.disconnectUser(handler);

        verify(handler).stopReceiving();
    }
}
