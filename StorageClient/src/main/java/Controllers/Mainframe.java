package Controllers;

import Handlers.FileHandler;
import Handlers.NetworkHandlerImpl;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.Data;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
public class Mainframe  implements Initializable {
    public TextField clientsPath;
    public TextField serversPath;
    public ListView<String> clientsFoldersList;
    public ListView<String> serversFoldersList;
    private String selectedServersFile;
    private String previousServersFile;
    private NetworkHandlerImpl networkHandler;
    private static Logger logger = Logger.getLogger(Mainframe.class.getName());
    private FileHandler fileHandler;
    private Image file = new Image("https://svl.ua/image/cache/download_pdf-32x32.png");
    private Image folder = new Image("https://i0.wp.com/cdna.c3dt.com/icon/328326-com.jrdcom.filemanager.png?w=32");
    private Image disc = new Image("https://findicons.com/files/icons/998/airicons/32/hdd.png");
    private Image image = new Image("https://findicons.com/files/icons/1637/file_icons_vs_2/32/png.png");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileHandler = new FileHandler();
        this.networkHandler = new NetworkHandlerImpl(this);
        repaintClientsList(File.listRoots());
    }


    public void clientsMouseAction(MouseEvent mouseEvent) {
        fileHandler.setPreviousFile(fileHandler.getCurrentClientsFile());
        String selectedItem = clientsFoldersList.getSelectionModel().getSelectedItem();
        fileHandler.updateCurrentClientsFile(selectedItem);
        if (fileHandler.getPreviousFile() != null && fileHandler.getPreviousFile().equals(fileHandler.getCurrentClientsFile()) ) {
            repaintClientsList(fileHandler.getOrOpen(selectedItem));
        }
    }

    public void repaintServersList(File[] files){
        serversFoldersList.getItems().clear();
        if (files == null || files.length == 0){
            return;
        }
        List<String> list = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        serversFoldersList.getItems().addAll(list);
        serversFoldersList.setCellFactory(param -> new ListCell<String>(){
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty){
                    setText(null);
                    setGraphic(null);
                } else {
                    if(item.endsWith(".png") || item.endsWith(".jpeg")){
                        imageView.setImage(image);
                    } else if (getFileByName(item, files).isFile()){
                        imageView.setImage(file);
                    }else {
                        imageView.setImage(folder);
                    }
                    setText(item);
                    setGraphic(imageView);
                }
            }
        });
    }

    public File getFileByName(String item, File[] files) {
        return Arrays.stream(files)
                .filter(x -> x.getName().equals(item))
                .findAny().get();
    }

    public void repaintClientsList(File[] files) {
        logger.info("refreshing clients list");
        clientsFoldersList.getItems().clear();
        if (fileHandler.isRoot(fileHandler.getCurrentDirectory())){
            clientsPath.setText("Root");
        } else{
            clientsPath.setText(fileHandler.getCurrentDirectory());
        }
        if (files == null || files.length == 0){
            return;
        }
        fileHandler.updateFilesList(files);
        clientsFoldersList.getItems().addAll(fileHandler.getListOfFileNames());
        clientsFoldersList.setCellFactory(param -> new ListCell<String>(){
            private ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty){
                    setText(null);
                    setGraphic(null);
                } else {
                    if(item.endsWith(".png") || item.endsWith(".jpeg")){
                        imageView.setImage(image);
                    } else if (fileHandler.getFileByName(item).isFile()){
                        imageView.setImage(file);
                    } else if(fileHandler.isRoot(item)){
                        imageView.setImage(disc);
                    }else {
                        imageView.setImage(folder);
                    }
                    setText(item);
                    setGraphic(imageView);
                }
            }
        });
        clientsFoldersList.refresh();

    }

    public void clientsListUp(ActionEvent actionEvent) {
        repaintClientsList(fileHandler.getListOfParentDirectory());
    }


    public void serversMouseAction(MouseEvent mouseEvent) {
        previousServersFile = selectedServersFile;
        selectedServersFile = serversFoldersList.getSelectionModel().getSelectedItem();
        if (previousServersFile != null){
            if (previousServersFile.equals(selectedServersFile)){
                File file = Arrays.stream(networkHandler.getCurrentServerFile().listFiles()).filter(x -> x.getName().equals(selectedServersFile)).findFirst().get();
                if (file.isDirectory()){
                    networkHandler.getFilesList(file.getAbsolutePath());;
                } else {

                }
            }
        }

    }
    public void download(ActionEvent actionEvent) {
        if (selectedServersFile == null){
            return;
        }
        networkHandler.download(selectedServersFile, fileHandler.getCurrentDirectory());
        repaintClientsList(fileHandler.getFilesListOfCurrentDirectory());
    }


    public void upload(ActionEvent actionEvent) {
        logger.info("Uploading file " + fileHandler.getCurrentClientsFile());
            networkHandler.upload(fileHandler.getCurrentClientsFile());
            networkHandler.getFilesList("");
    }


    public void serversListUp(ActionEvent actionEvent) {
        networkHandler.getFilesList(networkHandler.getCurrentServerFile().getParent());
    }
}
