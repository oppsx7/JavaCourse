package bg.sofia.uni.fmi.mjt.chat;

import java.util.Scanner;

public class StartServer
{
    
    public static final int port = 5050;
    
    public static void main ( String [] args ) throws Exception
    {
        try (ChatServer s = new ChatServer( port );
             Scanner in = new Scanner(System.in))
        {
            s.start();
            String command;
            while( !(command = in.nextLine()).equalsIgnoreCase( "exit" ) )
            {
                if( command.equalsIgnoreCase( "users" ) )
                {
                    System.out.println( s.getHandlers() );
                }
            }
        }

        System.out.println( "Goodbye" );
    }

}
