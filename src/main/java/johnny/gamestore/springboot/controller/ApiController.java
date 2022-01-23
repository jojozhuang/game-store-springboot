package johnny.gamestore.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The root path of the RESTful API.
 */
@RestController
@RequestMapping("/api")
public class ApiController {
  @GetMapping("")
  public String home() {
    return "Hello! welcome to game store api!";
  }
}
