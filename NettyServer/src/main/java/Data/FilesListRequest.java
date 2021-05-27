package Data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FilesListRequest implements Serializable {
    private File file;
}
