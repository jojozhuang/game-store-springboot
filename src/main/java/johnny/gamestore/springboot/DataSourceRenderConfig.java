package johnny.gamestore.springboot;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@Configuration
public class DataSourceRenderConfig {
  private static final int USERINFO_SPLIT_LIMIT = 2;

  @Profile("render")
  @Bean
  public BasicDataSource dataSource() throws URISyntaxException {
    String databaseUrl = System.getenv("DATABASE_URL");
    System.out.println("DATABASE_URL:" + databaseUrl);
    if (databaseUrl == null || databaseUrl.isEmpty()) {
      throw new IllegalStateException("DATABASE_URL environment variable is not set.");
    }
    // DATABASE_URL sample: postgres://<username>:<password>@<host>:<port>/<dbname>
    URI dbUri = new URI(databaseUrl);
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

    String[] userInfoParts = dbUri.getUserInfo() != null
      ? dbUri.getUserInfo().split(":", USERINFO_SPLIT_LIMIT) : new String[0];
    String username = userInfoParts.length > 0 ? userInfoParts[0] : "";
    String password = userInfoParts.length > 1 ? userInfoParts[1] : "";

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setUrl(dbUrl);
    basicDataSource.setUsername(username);
    basicDataSource.setPassword(password);

    // Set default schema to 'gamestore' for all connections
    basicDataSource.setConnectionInitSqls(Collections.singletonList("SET search_path TO gamestore"));
    return basicDataSource;
  }
}
