/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import johnny.gamestore.springboot.property.PathConfigProperties;
import johnny.gamestore.springboot.property.UrlConfigProperties;
import johnny.gamestore.springboot.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UploadController.class)
class UploadControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private PathConfigProperties pathConfigProperties;

  @MockBean private UrlConfigProperties urlConfigProperties;

  @MockBean private FileStorageService fileStorageService;

  @BeforeEach
  void setup() {
    // You can add common setup here if needed
  }

  @Test
  void uploadFile_shouldReturnMessage_whenFileUploaded() throws Exception {
    // Arrange
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "sample data".getBytes());
    when(fileStorageService.storeFile(any())).thenReturn("/uploads/test.txt");

    // Act & Assert
    mockMvc
        .perform(multipart("/api/upload").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void uploadFile_shouldReturnMessage_whenFileIsEmpty() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

    mockMvc
        .perform(multipart("/api/upload").file(file))
        .andExpect(status().isOk())
        .andExpect(content().string("please select a file!"));
  }

  @Test
  void uploadFileMulti_shouldUploadMultipleFiles() throws Exception {
    MockMultipartFile file1 =
        new MockMultipartFile("files", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "data1".getBytes());
    MockMultipartFile file2 =
        new MockMultipartFile("files", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "data2".getBytes());

    when(fileStorageService.storeFile(any()))
        .thenReturn("/uploads/file1.txt", "/uploads/file2.txt");

    mockMvc
        .perform(multipart("/api/upload/multi").file(file1).file(file2).param("extraField", "info"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Successfully uploaded")));
  }

  @Test
  void uploadFileMulti_shouldReturnMessage_whenFilesEmpty() throws Exception {
    MockMultipartFile emptyFile =
        new MockMultipartFile("files", "", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

    mockMvc
        .perform(multipart("/api/upload/multi").file(emptyFile).param("extraField", "info"))
        .andExpect(status().isOk())
        .andExpect(content().string("please select a file!"));
  }
}
