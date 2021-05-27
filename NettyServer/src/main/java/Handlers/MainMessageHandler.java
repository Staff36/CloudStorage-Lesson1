package Handlers;

import MessageTypes.RegularFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j;

import java.io.File;

@Log4j
public class MainMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("I have new message");
        if (msg == null){
            return;
        }
        if (msg instanceof String){
            ctx.writeAndFlush("Server say: " + msg);
            if (msg.toString().startsWith("list")){
                ctx.writeAndFlush(new File("C:\\GeekBrains\\JavaCloudStorage\\Share"));
            }

        }
        if (msg instanceof RegularFile){
            log.info("Catch file");
            RegularFile file = (RegularFile) msg;
            file.writeInstanceToFile(new File("C:\\GeekBrains\\JavaCloudStorage\\Share\\dest.txt"));
            log.info("File written");
        }

    }
}
