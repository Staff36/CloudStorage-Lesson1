package Handlers;

import Controllers.Mainframe;
import Data.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class NetworkHandlerImpl  implements NetworkHandlerInt{
    private final NetworkConnector networkConnector;
    private final Consumer<Object> objectMessageCallBack;
    private final Mainframe mainframe;
    private Map<File, Integer> bigFilesNames;

    public NetworkHandlerImpl(Mainframe mainframe) {
        this.objectMessageCallBack = this::getFile;
        networkConnector = new NetworkConnector(objectMessageCallBack);
        new Thread(networkConnector).start();
        bigFilesNames = new HashMap<>();
        this.mainframe = mainframe;
    }

    @Override
    public void getFilesList(String name) {
        networkConnector.writeObject(new FilesListRequest());
    }

    @Override
    public void download(String fileName, String path) {
        networkConnector.writeObject(new InquiryToDownloadFile(fileName, path));
    }

    @Override
    public void upload(File file) {
        if (file.isFile()) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                if (file.length() > 1024 * 1024) {
                    int parts = (int) Math.ceil(file.length() / 1024f);
                    for (int i = 0; i < parts; i++) {
                        byte[] bytes = new byte[1024];
                        int partsLength = (int) Math.min(1024L, (file.length() - i * 1024L));
                        randomAccessFile.read(bytes, i * 1024, partsLength);
                        networkConnector.writeObject(new BigFilesPart(i, parts, bytes, file));
                    }
                } else {
                    byte[] bytes = new byte[(int) file.length()];
                    randomAccessFile.read(bytes);
                    networkConnector.writeObject(new RegularFile(bytes, file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void getFile(Object o){
        if (o == null){
            return;
        }
        if (o instanceof RegularFile){
            writeRegularFile((RegularFile) o);
        }
        if (o instanceof BigFilesPart){
            writePartOfBigFile((BigFilesPart) o);
        }
        if (o instanceof FilesList){
            updateServersList((FilesList) o);
        }
    }



    private void updateServersList(FilesList o) {
        FilesList filesList = o;
        File[] files = filesList.getFiles();
        List<String> list = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        mainframe.getServersFoldersList().getItems().clear();
        mainframe.getServersFoldersList().getItems().addAll(list);
    }

    private void writePartOfBigFile(BigFilesPart o) {
        BigFilesPart bigFilesPart = o;
        File file = Paths.get(mainframe.getFileHandler().getCurrentDirectory(),bigFilesPart.getFile().getName() + "." + bigFilesPart.getPartsNumber()).toFile();
        if (bigFilesNames.get(file) == null){
            bigFilesNames.put(file, 0);
        } else {
            bigFilesNames.put(file, bigFilesNames.get(file) + 1 );
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")){
                randomAccessFile.write(bigFilesPart.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer totalPartsOfFile = bigFilesPart.getTotalPartsValue();
        if (totalPartsOfFile.equals(bigFilesNames.get(file))){
            // TODO: 26.05.2021  собрать 1 файл из частей и удалить части
        }
    }

    private void writeRegularFile(RegularFile o) {
        RegularFile regularFile = o;
        File path= Paths.get(mainframe.getFileHandler().getCurrentDirectory(),
                            regularFile.getFile().getName())
                    .toFile();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw")){
            randomAccessFile.write(regularFile.getData());
            mainframe.refreshClientsFilesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
