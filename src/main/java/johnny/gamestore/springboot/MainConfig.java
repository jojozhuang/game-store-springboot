package johnny.gamestore.springboot;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class MainConfig {

  @Bean
  public BasicDataSource dataSource() throws URISyntaxException {
    String databaseUrl = System.getenv("DATABASE_URL");
    if (!StringUtils.hasText(databaseUrl)) {
      BasicDataSource basicDataSource = new BasicDataSource();
      basicDataSource.setUrl("jdbc:h2:mem:gamestore");
      basicDataSource.setUsername("sa");
      basicDataSource.setPassword("abc123");

      return basicDataSource;
    } else {
      // DATABASE_URL sample: postgres://<username>:<password>@<host>:<port>/<dbname>
      URI dbUri = new URI(databaseUrl);

      String username = dbUri.getUserInfo().split(":")[0];
      String password = dbUri.getUserInfo().split(":")[1];
      String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

      BasicDataSource basicDataSource = new BasicDataSource();
      basicDataSource.setUrl(dbUrl);
      basicDataSource.setUsername(username);
      basicDataSource.setPassword(password);

      return basicDataSource;
    }
  }
}
