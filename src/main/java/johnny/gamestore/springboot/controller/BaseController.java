package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.property.UrlConfigProperties;
import johnny.gamestore.springboot.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * The base controller for RESTful controller, providing basic methods.
 */
@RestController
public class BaseController {
  @Autowired
  private UrlConfigProperties urlConfigProperties;

  protected String getBaseUrl() {
    String baseUrl = urlConfigProperties.getBaseUrl();
    if (!StringUtils.hasText(baseUrl)) {
      baseUrl = UrlUtil.getBaseEnvLinkURL();
    }

    return baseUrl;
  }
}
