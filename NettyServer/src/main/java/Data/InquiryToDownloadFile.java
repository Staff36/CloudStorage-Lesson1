package Data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class InquiryToDownloadFile implements Serializable {
    String fileName;
    String path;
}
