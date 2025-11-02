package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.property.UrlConfigProperties;
import johnny.gamestore.springboot.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class BaseController {
  @Autowired
  protected UrlConfigProperties urlConfigProperties;

  protected String getBaseUrl() {
    String baseUrl = urlConfigProperties.getBaseUrl();
    if (!StringUtils.hasText(baseUrl)) {
      baseUrl = UrlUtil.getBaseEnvLinkURL();
    }

    return baseUrl;
  }
}
