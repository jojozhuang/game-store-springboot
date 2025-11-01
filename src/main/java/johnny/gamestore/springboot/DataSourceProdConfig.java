package johnny.gamestore.springboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
public class DataSourceProdConfig {
  private static final int USERINFO_SPLIT_LIMIT = 2;

  @Profile("prod")
  @Bean
  public BasicDataSource dataSource() throws URISyntaxException {
    String databaseUrl = System.getenv("DATABASE_URL");
    log.info("DATABASE_URL: {}", databaseUrl);
    System.out.println("DATABASE_URL:" + databaseUrl);
    if (databaseUrl == null || databaseUrl.isEmpty()) {
      throw new IllegalStateException("DATABASE_URL environment variable is not set.");
    }
    // DATABASE_URL sample: postgres://<username>:<password>@<host>:<port>/<dbname>
    URI dbUri = new URI(databaseUrl);
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
    if ("localhost".equalsIgnoreCase(dbUri.getHost())) {
      dbUrl += "?sslmode=disable";
    } else {
      dbUrl += "?sslmode=require";
    }

    String[] userInfoParts = dbUri.getUserInfo() != null
      ? dbUri.getUserInfo().split(":", USERINFO_SPLIT_LIMIT) : new String[0];
    String username = userInfoParts.length > 0 ? userInfoParts[0] : "";
    String password = userInfoParts.length > 1 ? userInfoParts[1] : "";
    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setUrl(dbUrl);
    basicDataSource.setUsername(username);
    basicDataSource.setPassword(password);

    return basicDataSource;
  }
}
