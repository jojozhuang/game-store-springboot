package johnny.gamestore.springboot.service;

import johnny.gamestore.springboot.exception.FileStorageException;
import johnny.gamestore.springboot.property.PathConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * File storage service.
 */
@Service
public class FileStorageService {
  private static final long TICKS_AT_EPOCH = 621355968000000000L;
  private static final int TEN_THOUSAND = 10000;
  private final Path fileStorageLocation;

  /**
   * The constructor of FileStorageService.
   *
   * @param pathConfigProperties pathConfigProperties
   */
  @Autowired
  public FileStorageService(PathConfigProperties pathConfigProperties) {
    this.fileStorageLocation = Paths.get(pathConfigProperties.getUploadDir())
      .toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  /**
   * Store file to disk.
   *
   * @param file file
   * @return file path
   */
  public String storeFile(MultipartFile file) {
    // Normalize file name
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    try {
      // Check if the file's name contains invalid characters
      if (fileName.contains("..")) {
        throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
      }

      String contentType = file.getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
        throw new IllegalArgumentException("Only image uploads are allowed.");
      }

      // create unique name
      long tick = System.currentTimeMillis() * TEN_THOUSAND + TICKS_AT_EPOCH;
      fileName = String.valueOf(tick).concat("_").concat(fileName);
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = this.fileStorageLocation.resolve(fileName);

      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      String absolutePath = targetLocation.toString();
      String rootPath = this.fileStorageLocation.getParent().toAbsolutePath().toString();
      return absolutePath.replace(rootPath, "");
    } catch (IOException ex) {
      throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
    }
  }

  public Resource loadFileAsResource(String fileName) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new FileStorageException("File not found " + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new FileStorageException("File not found " + fileName);
    }
  }
}
