/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import johnny.gamestore.springboot.exception.FileStorageException;
import johnny.gamestore.springboot.property.PathConfigProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileStorageServiceTest {
  private static Path tempDir;
  private FileStorageService fileStorageService;

  @BeforeAll
  static void init() throws IOException {
    // Create a temporary directory for testing
    tempDir = Files.createTempDirectory("filestorage-test");
  }

  @AfterAll
  static void cleanup() throws IOException {
    FileSystemUtils.deleteRecursively(tempDir);
  }

  @BeforeEach
  void setUp() {
    PathConfigProperties mockConfig = Mockito.mock(PathConfigProperties.class);
    Mockito.when(mockConfig.getUploadDir()).thenReturn(tempDir.toString());
    fileStorageService = new FileStorageService(mockConfig);
  }

  @Test
  void storeFile_Success() throws IOException {
    // Arrange
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test-image.png", "image/png", "fake image content".getBytes());

    // Act
    String resultPath = fileStorageService.storeFile(file);

    // Assert
    assertNotNull(resultPath);
    assertTrue(resultPath.contains("test-image.png"));

    // Verify file exists
    Path savedFile = tempDir.resolve(Paths.get(resultPath).getFileName());
    assertTrue(Files.exists(savedFile));
  }

  @Test
  void storeFile_InvalidExtension_ShouldThrow() {
    MockMultipartFile file =
        new MockMultipartFile("file", "bad-file.txt", "text/plain", "not an image".getBytes());

    assertThrows(IllegalArgumentException.class, () -> fileStorageService.storeFile(file));
  }

  @Test
  void storeFile_PathTraversal_ShouldThrow() {
    MockMultipartFile file =
        new MockMultipartFile("file", "../evil.png", "image/png", "malicious content".getBytes());

    FileStorageException ex =
        assertThrows(FileStorageException.class, () -> fileStorageService.storeFile(file));
    assertTrue(ex.getMessage().contains("invalid path sequence"));
  }

  @Test
  void storeFile_IOException_ShouldThrow() throws IOException {
    // Mock MultipartFile that throws IOException when getInputStream() is called
    MockMultipartFile mockFile = Mockito.mock(MockMultipartFile.class);
    Mockito.when(mockFile.getOriginalFilename()).thenReturn("test.png");
    Mockito.when(mockFile.getContentType()).thenReturn("image/png");
    Mockito.when(mockFile.getInputStream()).thenThrow(new IOException("boom"));

    assertThrows(FileStorageException.class, () -> fileStorageService.storeFile(mockFile));
  }

  @Test
  void loadFileAsResource_Success() throws IOException {
    // Create a file manually
    Path testFile = tempDir.resolve("sample.png");
    Files.write(testFile, "content".getBytes());

    Resource resource = fileStorageService.loadFileAsResource("sample.png");

    assertTrue(resource.exists());
    assertEquals("sample.png", Paths.get(resource.getFilename()).toString());
  }

  @Test
  void loadFileAsResource_NotFound_ShouldThrow() {
    FileStorageException ex =
        assertThrows(
            FileStorageException.class, () -> fileStorageService.loadFileAsResource("missing.png"));
    assertTrue(ex.getMessage().contains("File not found"));
  }
}
