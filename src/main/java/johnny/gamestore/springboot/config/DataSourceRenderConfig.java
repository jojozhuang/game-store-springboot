package johnny.gamestore.springboot.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.URISyntaxException;

@Configuration
@Profile("render")
public class DataSourceRenderConfig {
  @Bean
  public BasicDataSource dataSource() throws URISyntaxException {
    return DataSourceFactory.createDataSource(false, true);
  }
}
