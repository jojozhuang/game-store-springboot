package johnny.gamestore.springboot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
      title = "Game Store API",
      version = "1.0",
      description = "RESTful APIs for managing products, uploads, and other game store features."
    )
)
@SpringBootApplication
public class GameStoreApplication {
  public static void main(String[] args) {
    SpringApplication.run(GameStoreApplication.class, args);
  }
}
