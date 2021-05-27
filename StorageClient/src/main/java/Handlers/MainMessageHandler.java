package Handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.util.function.Consumer;

public class MainMessageHandler extends ChannelInboundHandlerAdapter {
    Consumer<Object> collable;

    public MainMessageHandler(Consumer<Object> collable) {
        this.collable = collable;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("Connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        collable.accept(msg);

        try {
            //NULL
            if (msg == null) {
                return;
            }
            //String
            if (msg instanceof String){
                System.out.println(msg);
            }
            //File
            if (msg instanceof File){
                File file = (File) msg;
                if (file.isDirectory()){

                } else {
                    System.out.println("Single File" + file.getName());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }


}
