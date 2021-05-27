package Data;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class RegularFile implements Serializable {
    byte[] data;
    File file;

    public RegularFile(byte[] data, File file) {
        this.data = data;
        this.file = file;
    }
}
