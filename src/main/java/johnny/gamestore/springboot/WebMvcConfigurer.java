package johnny.gamestore.springboot;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/** Configuration adapter. */
@Configuration
class WebMvcConfigurer extends WebMvcConfigurerAdapter {
  /**
   * Register image resources.
   *
   * @param registry registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**").addResourceLocations("file:./public/uploads/");
    registry
        .addResourceHandler("/images/**")
        .addResourceLocations("file:./src/main/resources/images/");
  }
}
