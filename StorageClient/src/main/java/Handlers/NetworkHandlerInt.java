package Handlers;

import java.io.File;

public interface NetworkHandlerInt {
    void getFilesList(String name);

    void download(String fileName, String path);

    void upload(File file);

}
