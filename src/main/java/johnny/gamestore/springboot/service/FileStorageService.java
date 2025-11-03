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

/** File storage service. */
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
    this.fileStorageLocation =
        Paths.get(pathConfigProperties.getUploadDir()).toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException(
          "Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  /**
   * Store file to disk.
   *
   * @param file file
   * @return file path
   */
  public String storeFile(MultipartFile file) {
    try {
      // Normalize and sanitize original filename
      String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

      // Prevent path traversal
      if (originalFileName.contains("..")) {
        throw new FileStorageException(
            "Sorry! Filename contains invalid path sequence " + originalFileName);
      }

      // Validate content type
      String contentType = file.getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
        throw new IllegalArgumentException("Only image uploads are allowed.");
      }

      // Generate server-controlled unique filename
      long tick = System.currentTimeMillis() * TEN_THOUSAND + TICKS_AT_EPOCH;
      String safeFileName = tick + "_" + Paths.get(originalFileName).getFileName().toString();

      // Resolve target location safely
      Path targetLocation = this.fileStorageLocation.resolve(safeFileName).normalize();

      // Copy file to the target location
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      // Return relative path
      String absolutePath = targetLocation.toString();
      String rootPath = this.fileStorageLocation.getParent().toAbsolutePath().toString();
      return absolutePath.replace(rootPath, "");
    } catch (IOException ex) {
      throw new FileStorageException("Could not store file. Please try again!", ex);
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
