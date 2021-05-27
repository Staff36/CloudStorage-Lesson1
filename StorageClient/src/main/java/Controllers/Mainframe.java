package Controllers;

import Handlers.FileHandler;
import Handlers.NetworkHandlerImpl;
import Handlers.NetworkHandlerInt;
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

@Data
public class Mainframe  implements Initializable {
    public TextField clientsPath;
    public ListView<String> clientsFoldersList;
    public ListView<String> serversFoldersList;
    private String selectedServersFile;
    private String previousServersFile;
    private NetworkHandlerInt networkHandler;
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
        refreshClientsList(File.listRoots());
        refreshServersList("");
    }


    public void clientsMouseAction(MouseEvent mouseEvent) {
        fileHandler.setPreviousFile(fileHandler.getCurrentClientsFile());
        String selectedItem = clientsFoldersList.getSelectionModel().getSelectedItem();
        fileHandler.updateCurrentClientsFile(selectedItem);
        if (fileHandler.getPreviousFile() != null && fileHandler.getPreviousFile().equals(fileHandler.getCurrentClientsFile()) ) {
            refreshClientsList(fileHandler.getOrOpen(selectedItem));
        }
    }

    private void refreshClientsList(File[] files) {
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
        refreshClientsList(fileHandler.getListOfParentDirectory());
    }

    private void refreshServersList(String path){
        logger.info("Refreshing servers list  : " + path);
        networkHandler.getFilesList(path);
    }

    public void serversMouseAction(MouseEvent mouseEvent) {
        previousServersFile = selectedServersFile;
        selectedServersFile = serversFoldersList.getSelectionModel().getSelectedItem();
        if (previousServersFile != null){
            if (previousServersFile.equals(selectedServersFile)){
                //download or open directory
            }
        }

    }
    public void download(ActionEvent actionEvent) {
        if (selectedServersFile == null){
            return;
        }
        networkHandler.download(selectedServersFile, fileHandler.getCurrentDirectory());
        refreshClientsFilesList();
    }


    public void upload(ActionEvent actionEvent) {
        logger.info("Uploading file " + fileHandler.getCurrentClientsFile());
            networkHandler.upload(fileHandler.getCurrentClientsFile());
            refreshServersList("");
    }

    public void refreshClientsFilesList(){
        refreshClientsList(fileHandler.getFilesListOfCurrentDirectory());
    }

}
