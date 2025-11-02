package johnny.gamestore.springboot.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.URISyntaxException;

@Configuration
@Profile("prod")
public class DataSourceProdConfig {
  @Bean
  public BasicDataSource dataSource() throws URISyntaxException {
    return DataSourceFactory.createDataSource(true, false);
  }
}
