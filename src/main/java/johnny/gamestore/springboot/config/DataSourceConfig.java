/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@Slf4j
public final class DataSourceConfig {
  private DataSourceConfig() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static BasicDataSource createDataSource(boolean requireSsl, boolean setSchema)
      throws URISyntaxException {
    String databaseUrl = System.getenv("DATABASE_URL");
    log.info("DATABASE_URL: {}", databaseUrl);
    if (databaseUrl == null || databaseUrl.isEmpty()) {
      throw new IllegalStateException("DATABASE_URL environment variable is not set.");
    }

    URI dbUri = new URI(databaseUrl);
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

    if (requireSsl) {
      dbUrl += "?sslmode=require";
    } else if ("localhost".equalsIgnoreCase(dbUri.getHost())) {
      dbUrl += "?sslmode=disable";
    }

    String[] userInfoParts =
        dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":", 2) : new String[0];
    String username = userInfoParts.length > 0 ? userInfoParts[0] : "";
    String password = userInfoParts.length > 1 ? userInfoParts[1] : "";

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setUrl(dbUrl);
    basicDataSource.setUsername(username);
    basicDataSource.setPassword(password);

    if (setSchema) {
      basicDataSource.setConnectionInitSqls(
          Collections.singletonList("SET search_path TO gamestore"));
    }

    return basicDataSource;
  }
}
