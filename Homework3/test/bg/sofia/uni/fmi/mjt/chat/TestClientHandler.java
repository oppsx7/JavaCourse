package bg.sofia.uni.fmi.mjt.chat;

import bg.sofia.uni.fmi.mjt.chat.commands.SendMessageCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class TestClientHandler {

    private ClientHandler handler;

    @Mock
    ChatClient asociatedClient;
    @Mock
    ChatServer server;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        handler = new ClientHandler(asociatedClient, server);
    }

    @Test
    public void testReceivingMessage() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(asociatedClient.getSocket()).thenReturn(socket);
        ChatClient sender = mock(ChatClient.class);
        SendMessageCommand command = new SendMessageCommand("message", sender, asociatedClient);
        handler.receiveMessage(command);

        verify(outputStream).write((command.getSender().getName() + " : " + command.getMessage() + '\r').getBytes());
    }
}
