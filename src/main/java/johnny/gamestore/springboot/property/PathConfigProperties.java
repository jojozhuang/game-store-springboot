package johnny.gamestore.springboot.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "site.paths")
public class PathConfigProperties {
  @NotBlank private String uploadDir;
}
