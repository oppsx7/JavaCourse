package bg.sofia.uni.fmi.mjt.wish.list.services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public abstract class WishListService implements AutoCloseable {

    protected ServerSocketChannel serverSocketChannel;
    protected Selector selector;
    protected ByteBuffer commandBuffer;

    protected boolean runServer;

    protected WishListService(int port) throws IOException {
        this.runServer = true;
        this.selector = Selector.open();

        final int bufferSize = 1024;
        this.commandBuffer = ByteBuffer.allocate(bufferSize);

        this.serverSocketChannel = ServerSocketChannel.open();
        this.configureServerSocketChannel(port);
    }

    private void configureServerSocketChannel(int port) throws IOException {
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);

        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public abstract void close() throws Exception;
}
