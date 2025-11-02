package johnny.gamestore.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API Root", description = "Basic API entry point for Game Store services")
@RestController
@RequestMapping("/api")
public class ApiController {
  @Operation(
    summary = "Welcome message",
    description = "Returns a simple greeting message to confirm the API is running.",
    responses = {
      @ApiResponse(responseCode = "200", description = "API is up and responding with welcome message")
    }
  )
  @GetMapping("")
  public String home() {
    return "Hello! welcome to game store api!";
  }
}
