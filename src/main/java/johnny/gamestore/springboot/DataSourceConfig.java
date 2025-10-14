package johnny.gamestore.springboot;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@Configuration
public class DataSourceConfig {
  @Profile({"prod", "render"})
  @Bean
  public BasicDataSource dataSource() throws URISyntaxException {
    String databaseUrl = System.getenv("DATABASE_URL");
    System.out.println("DATABASE_URL:" + databaseUrl);
    // DATABASE_URL sample: postgres://<username>:<password>@<host>:<port>/<dbname>
    URI dbUri = new URI(databaseUrl);

    String dbUrl =
        "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
    if ("localhost".equalsIgnoreCase(dbUri.getHost())) {
      dbUrl += "?sslmode=disable";
    } else {
      dbUrl += "?sslmode=disable";
    }
    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setUrl(dbUrl);
    basicDataSource.setUsername(username);
    basicDataSource.setPassword(password);

    // Set default schema to 'gamestore' for all connections
    basicDataSource.setConnectionInitSqls(Collections.singletonList("SET search_path TO gamestore"));
    return basicDataSource;
  }
}
