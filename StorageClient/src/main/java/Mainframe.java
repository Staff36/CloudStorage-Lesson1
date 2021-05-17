import Handlers.FileHandler;
import Handlers.NetworkHandler;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class Mainframe  implements Initializable {
    public TextField clientsPath;
    public ListView<String> clientsFoldersList;
    public ListView<String> serversFoldersList;
    private String selectedServersFile;
    private String previousServersFile;
    private NetworkHandler networkHandler;
    private static Logger logger = Logger.getLogger(Mainframe.class.getName());
    private FileHandler fileHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileHandler = new FileHandler();
        this.networkHandler = new NetworkHandler("Dmitry");
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
        if (files.length == 0){
            return;
        }
        fileHandler.updateFilesList(files);
        clientsFoldersList.getItems().addAll(fileHandler.getListOfFileNames());
        clientsFoldersList.refresh();
    }

    public void clientsListUp(ActionEvent actionEvent) {
        refreshClientsList(fileHandler.getListOfParentDirectory());
    }

    private void refreshServersList(String path){
        logger.info("Refreshing servers list  : " + path);
        serversFoldersList.getItems().clear();
        serversFoldersList.getItems().addAll(networkHandler.getFilesList(path));
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
        refreshClientsList(fileHandler.getFilesListOfCurrentDirectory());
    }


    public void upload(ActionEvent actionEvent) {
        logger.info("Uploading file " + fileHandler.getCurrentClientsFile());
            networkHandler.upload(fileHandler.getCurrentClientsFile());
            refreshServersList("");
    }
}
