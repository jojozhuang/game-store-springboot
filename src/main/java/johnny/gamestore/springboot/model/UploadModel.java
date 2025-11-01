package johnny.gamestore.springboot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Model for upload file.
 */
@Getter
@Setter
@NoArgsConstructor
public class UploadModel {
  private String extraField;
  private MultipartFile[] files;
}
