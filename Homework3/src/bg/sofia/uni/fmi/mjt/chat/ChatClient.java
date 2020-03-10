package bg.sofia.uni.fmi.mjt.chat;


import java.net.Socket;


public class ChatClient
{

    private final String name;

    private final Socket socket;

    public ChatClient(String name , Socket socket )
    {
        super();
        this.name = name;
        this.socket = socket;
    }

    public String getName ()
    {
        return name;
    }

    public Socket getSocket ()
    {
        return socket;
    }

    @Override
    public boolean equals ( Object obj )
    {
        return this.name.equals( ( (ChatClient) obj ).name );
    }
    
    @Override
    public String toString ()
    {
        return this.name;
    }
}
