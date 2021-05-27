package MessageTypes;

import lombok.Data;

import java.io.*;

@Data
public class RegularFile implements Serializable {
    String name;
    byte[] data;
    File file;

    public RegularFile(File file) {
        this.file = file;
        this.name = file.getName();
        try ( FileInputStream fis = new FileInputStream(file)){
            data = new byte[(int) file.length()];
            fis.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInstanceToFile(File file){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

        if (data == null){
                return;
            }
            fos.write(data);
            fos.flush();
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
