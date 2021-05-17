import Handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

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
                System.err.println("Waiting new connection");
                client = socket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ClientHandler(client, "test")).start();
        }
    }


}

