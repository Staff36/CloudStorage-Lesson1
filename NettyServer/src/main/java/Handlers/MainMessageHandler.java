package Handlers;

import Data.FilesList;
import Data.FilesListRequest;
import MessageTypes.RegularFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j;

import java.io.File;

@Log4j
public class MainMessageHandler extends ChannelInboundHandlerAdapter {
    private File currentFile = new File("C:\\test\\");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected");
        ctx.writeAndFlush(new FilesList(currentFile));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("I have new message " + msg.getClass().getCanonicalName());
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
        if (msg instanceof FilesListRequest){
           FilesListRequest filesList = (FilesListRequest) msg;
           currentFile  = filesList.getFile();
           if (currentFile.isDirectory()){
               ctx.writeAndFlush(new FilesList(currentFile));
           } else {
               ctx.writeAndFlush(new RegularFile(currentFile));
           }
        }

    }
}
