package Handlers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.log4j.Log4j;

import java.util.function.Consumer;

@Log4j
public class NetworkConnector implements Runnable{
    private SocketChannel clientChanel;
    private Consumer<Object> messageCallBack;


    public NetworkConnector(Consumer<Object> messageCallBack) {
        this.messageCallBack = messageCallBack;
    }

    @Override
    public void run() {
        final int PORT = 9909;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            log.info("Loop created");
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            log.info("Channel initialized");
                            clientChanel = socketChannel;
                            log.info(socketChannel.toString());
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new MainMessageHandler(messageCallBack)
                            );
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", PORT).sync();
            channelFuture.channel().closeFuture().sync();
            log.debug("Server is started");
        } catch (InterruptedException e) {
            log.error("Something was wrong", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void writeObject(Object obj){
        clientChanel.writeAndFlush(obj);
    }

}
