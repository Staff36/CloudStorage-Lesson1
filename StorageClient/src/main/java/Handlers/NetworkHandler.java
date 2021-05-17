package Handlers;

import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;
@Data
public class NetworkHandler {
    private static final int PACKAGE_SIZE = 1024;
    private static byte[] data = new byte[PACKAGE_SIZE];
    private Socket socket;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Scanner scanner;
    private static Logger logger = Logger.getLogger(NetworkHandler.class.getName());
    public NetworkHandler(String name){
        try {
            this.socket = new Socket("localhost", 8989);
            this.dis = new DataInputStream(socket.getInputStream());
            this.bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            this.scanner = new Scanner(System.in);
            dos.writeUTF(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String[] getFilesList(String name){
        String[] list;
        try {
            dos.writeUTF("/ls " + name);
            list = dis.readUTF().split(",");
        } catch (IOException e) {
            throw new RuntimeException("Sending FilesList was failed", e);
        }
        return list;
    }


    public void download(String fileName, String path){
        try {
        dos.writeUTF("/download " + fileName);
        System.out.println(fileName);
        long size = dis.readLong();
        int packageSize = dis.readInt();
        String name = dis.readUTF();
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(Paths.get(path , name).toString()));
        byte[] bytes = new byte[packageSize];
        while (size > 0){
            int innerDataSize = bufferedInputStream.read(bytes);
            bufferedOutputStream.write(bytes,0,innerDataSize);
            bufferedOutputStream.flush();
            size -= innerDataSize;
        }
            logger.info("File has already downloaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void upload (File file){
        if (file.isDirectory()){
            return;
        }
        try {
            logger.info("filename is " + file.getName());
            dos.writeUTF("/upload " + file.getName());
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            long fileSize = file.length();
            dos.writeLong(fileSize);
            dos.writeInt(PACKAGE_SIZE);
            while (fileSize > 0) {
                int size = bufferedInputStream.read(data);
                bufferedOutputStream.write(data, 0, size);
                bufferedOutputStream.flush();
                fileSize -= size;
                logger.info("File size now is: " + fileSize);
            }
            logger.info("File has already uploaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
