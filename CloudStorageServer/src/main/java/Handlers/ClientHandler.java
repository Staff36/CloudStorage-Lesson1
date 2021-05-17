package Handlers;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

public class ClientHandler  extends Thread implements AutoCloseable{
    private static Logger logger =  Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;
    private final String directory;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private  DataOutputStream dataOutputStream ;
    private  DataInputStream dataInputStream;
    private final int PACKAGE_SIZE = 1024;
    private final byte[] data =  new byte[PACKAGE_SIZE];
    Path parentDir;
    @Override
    public void close() throws Exception {
        bufferedOutputStream.close();
        bufferedInputStream.close();
        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
    }

    public ClientHandler(Socket socket, String directory) {

        this.directory = directory;
        this.socket = socket;
        try {
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            this.bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            this.parentDir = Paths.get(Arrays.stream(File.listRoots())
                                            .findFirst()
                                            .get()
                                            .getAbsolutePath(),
                            directory,
                            dataInputStream.readUTF());
            logger.info("Root path is: " + parentDir);
            File file = new File(parentDir.toString());
            if(!file.exists()) file.mkdir();

            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(String pathAndName) {
        try {
            File file = new File(Paths.get(parentDir.toString(), pathAndName).toString());
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            long fileSize = file.length();
            System.out.println(file.getName());
            dataOutputStream.writeLong(fileSize);
            dataOutputStream.writeInt(PACKAGE_SIZE);
            dataOutputStream.writeUTF(file.getName());
            while (fileSize > 0){
                int size = bufferedInputStream.read(data);
                bufferedOutputStream.write(data, 0, size);
                bufferedOutputStream.flush();
                fileSize -= size;
                System.out.println(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Upload was wrong " + e);
        }

    }

    public void downloadFile(String pathAndName) {
        try {
            System.out.println("STRING " + pathAndName);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(Paths.get(parentDir.toString(), pathAndName).toString()));
            long size = dataInputStream.readLong();
            int packageSize = dataInputStream.readInt();
            byte[] bytes = new byte[packageSize];
            while (size > 0) {
                int innerDataSize = bufferedInputStream.read(bytes);
                bufferedOutputStream.write(bytes, 0, innerDataSize);
                bufferedOutputStream.flush();
                size -= innerDataSize;
            }
            System.out.println(new File(parentDir.toString() + " " + pathAndName).getAbsolutePath());
            System.out.println("downloading ok");
        }catch (IOException e){
         throw new RuntimeException("Download was wrong " + e);
        }
    }

    public void sendListOfFiles(String path){
        try {
            System.out.println( "Path " + path);
            File file = new File(Paths.get(parentDir.toString(), path).toString());
            if(file.isDirectory()){
                StringBuilder sb = new StringBuilder();
                File [] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    sb.append(files[i].getName());
                    sb.append(",");
                }
                dataOutputStream.writeUTF(sb.toString());
            }
            System.out.println("files was sended");
        } catch (IOException e) {
            throw new RuntimeException("Sending path " + e);
        }
    }

    public void listen() {
        try {
            while (true){
                System.out.println("listening");
                String msg = dataInputStream.readUTF();
                System.out.println(msg);
                if (msg.startsWith("/download")){
                    System.out.println("uploading file");
                    uploadFile(msg.substring(10));
                } else if(msg.startsWith("/upload")){
                    System.out.println("downloading file");
                    downloadFile(msg.substring(8));
                } else if(msg.startsWith("/ls")){
                    System.out.println("sending files list");
                    String[] splitedCommand = splitCommand(msg);
                    if (splitedCommand.length>1){
                        sendListOfFiles(splitedCommand[1]);
                    } else{
                        sendListOfFiles("");
                    }
                } else{
                    System.err.println("SOMETHING WAS WRONG");
                    dataOutputStream.writeUTF("Unknown command " + msg);
                }
            }
        } catch (IOException e) {
            System.err.print("Exception " + e);
            return;
        } finally {
            try {
                this.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private String [] splitCommand (String command){
        String[] strings= command.split("\\s");
        return strings;
    }
}
