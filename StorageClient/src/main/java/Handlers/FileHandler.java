package Handlers;

import lombok.Data;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class FileHandler {
    String currentDirectory;
    File currentClientsFile;
    private List<File> filesList;
    File previousFile;

    public FileHandler() {
        filesList = new ArrayList<>();
        Collections.addAll(filesList,File.listRoots());
        previousFile = new File("");
        currentDirectory = filesList.get(0).getAbsolutePath();
    }

    public boolean isRoot(String name){
        return Arrays.stream(File.listRoots()).anyMatch(x->x.getAbsolutePath().equals(name));
    }


    public void updateCurrentClientsFile(String selectedItem) {
        if (isRoot(selectedItem)){
            currentClientsFile = filesList.stream()
                    .filter(x->x.getAbsolutePath().equals(selectedItem))
                    .findFirst()
                    .orElse(currentClientsFile);
        } else {
            currentClientsFile = filesList.stream()
                    .filter(x->x.getName().equals(selectedItem))
                    .findFirst()
                    .orElse(currentClientsFile);
        }
    }

    public void updateFilesList(File[] files) {
        filesList.clear();
        Collections.addAll(filesList,files);
    }

    public String[] getListOfFileNames() {
        if (isRoot(filesList.get(0).getAbsolutePath())) {
            return filesList.stream().filter(File::exists).map(File::getAbsolutePath).toArray(String[]::new);
        } else {
            return filesList.stream().filter(File::exists).map(File::getName).toArray(String[]::new);
        }

    }

    public File[] getListOfParentDirectory() {
        if (isRoot(currentDirectory)){
            return File.listRoots();
        } else {
            currentClientsFile = new File(currentDirectory).getParentFile();
            currentDirectory = currentClientsFile.getAbsolutePath();
            return currentClientsFile.listFiles();
        }
    }

    public File[] getOrOpen(String selectedItem) {
            if (currentClientsFile.isDirectory()){
                currentDirectory = currentClientsFile.getAbsolutePath();
            } else {
                try {
                    Desktop.getDesktop().open(currentClientsFile);
                } catch (IOException e) {
                    throw new RuntimeException("Exception when we try open file " + currentClientsFile.getName() + " " + e);
                }
            }

        return currentClientsFile.listFiles();
    }
    public File[] getFilesListOfCurrentDirectory(){
        return new File(currentDirectory).listFiles();
    }
}
