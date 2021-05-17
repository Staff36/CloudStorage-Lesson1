import Handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    public static void main(String[] args){
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(8989);
        } catch (IOException e) {
            throw new RuntimeException("Something was wring when server was started " + e);
        }
        while(true){
            Socket client = null;
            try {
                logger.info("Waiting new connection");
                client = socket.accept();
                logger.info("Client " + client + " was connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ClientHandler(client, "test")).start();
        }
    }


}

