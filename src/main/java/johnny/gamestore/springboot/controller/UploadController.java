package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.model.ResponseResult;
import johnny.gamestore.springboot.model.UploadModel;
import johnny.gamestore.springboot.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "File Upload", description = "APIs for uploading single or multiple files")
@RestController
@RequestMapping("/api/upload")
public class UploadController extends BaseController {
  @Autowired
  private FileStorageService fileStorageService;

  @Operation(
      summary = "Upload a single file",
      description = "Uploads a single file and returns the file URL.",
      responses = {
        @ApiResponse(responseCode = "200", description = "File uploaded successfully",
          content = @Content(schema = @Schema(implementation = ResponseResult.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file or upload error")
      }
    )
  @PostMapping("")
  public ResponseEntity<?> uploadFile(
      @Parameter(description = "File to upload", required = true)
      @RequestParam("file") MultipartFile uploadfile) {
    if (uploadfile.isEmpty()) {
      return ResponseEntity.ok().body("please select a file!");
    }

    ResponseResult rr = new ResponseResult();

    try {
      String[] fileUrls = saveUploadedFiles(Arrays.asList(uploadfile));
      rr.setMessage(getBaseUrl() + fileUrls[0]);
    } catch (IOException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(rr);
  }

  @Operation(
      summary = "Upload multiple files",
      description = "Uploads multiple files with an extra form field.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Files uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Upload failed or invalid files")
      }
    )
  @PostMapping("/multi")
  public ResponseEntity<?> uploadFileMulti(
      @Parameter(description = "Extra metadata field", example = "exampleField")
      @RequestParam("extraField") String extraField,
      @Parameter(description = "Files to upload", required = true)
      @RequestParam("files") MultipartFile[] uploadfiles) {
    String uploadedFileName = Arrays.stream(uploadfiles)
        .map(MultipartFile::getOriginalFilename)
        .filter(StringUtils::hasText)
        .collect(Collectors.joining(","));

    if (!StringUtils.hasText(uploadedFileName)) {
      return ResponseEntity.ok().body("please select a file!");
    }

    try {
      saveUploadedFiles(Arrays.asList(uploadfiles));
    } catch (IOException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().body("Successfully uploaded - " + uploadedFileName);
  }

  @Operation(
      summary = "Upload multiple files via form model",
      description = "Uploads multiple files using a model binding form submission.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Files uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Upload failed or invalid form")
      }
  )
  @PostMapping("/multi/model")
  public ResponseEntity<?> multiUploadFileModel(
      @Parameter(description = "Upload form model", required = true)
      @ModelAttribute UploadModel model) {
    try {
      saveUploadedFiles(Arrays.asList(model.getFiles()));
    } catch (IOException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok().body("Successfully uploaded!");
  }

  private String[] saveUploadedFiles(List<MultipartFile> files) throws IOException {
    String[] fileUrls = new String[files.size()];
    int index = 0;
    for (MultipartFile file : files) {
      if (file.isEmpty()) {
        continue;
      }
      fileUrls[index] = fileStorageService.storeFile(file);
      index++;
    }
    return fileUrls;
  }
}
