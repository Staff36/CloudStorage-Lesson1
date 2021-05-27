package Data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class BigFilesPart implements Serializable {

    int partsNumber;
    int totalPartsValue;
    byte[] data;
    File file;


    public BigFilesPart(int partsNumber, int totalPartsValue, byte[] data, File file) {
        this.partsNumber = partsNumber;
        this.totalPartsValue = totalPartsValue;
        this.data = data;
        this.file = file;
    }

}
