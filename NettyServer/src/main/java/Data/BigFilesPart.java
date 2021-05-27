package Data;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class BigFilesPart implements Serializable {

    private int partsNumber;
    private int totalPartsValue;
    private byte[] data;
    private File file;


    public BigFilesPart(int partsNumber, int totalPartsValue, byte[] data, File file) {
        this.partsNumber = partsNumber;
        this.totalPartsValue = totalPartsValue;
        this.data = data;
        this.file = file;
    }

}
