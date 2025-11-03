package johnny.gamestore.springboot.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.InvocationTargetException;

class UrlUtilTest {
  @AfterEach
  void tearDown() {
    // Clear RequestContextHolder to avoid test interference
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void testPrivateConstructorThrowsException() throws Exception {
    var constructor = UrlUtil.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    Exception exception =
        assertThrows(
            InvocationTargetException.class,
            () -> {
              constructor.newInstance();
            });

    // Verify the *cause* of the InvocationTargetException
    assertThat(exception.getCause())
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Utility class — instantiation not allowed");
  }

  @Test
  void testGetBaseEnvLinkURL_DefaultPortNoContextPath() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setLocalName("localhost");
    request.setLocalPort(80);
    request.setContextPath("");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    String result = UrlUtil.getBaseEnvLinkURL();
    assertThat(result).isEqualTo("http://localhost");
  }

  @Test
  void testGetBaseEnvLinkURL_CustomPortWithContextPath() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setLocalName("example.com");
    request.setLocalPort(8080);
    request.setContextPath("/api");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    String result = UrlUtil.getBaseEnvLinkURL();
    assertThat(result).isEqualTo("http://example.com:8080/api");
  }

  @Test
  void testGetBaseEnvLinkURL_CustomPortNoContextPath() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setLocalName("myserver");
    request.setLocalPort(8081);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    String result = UrlUtil.getBaseEnvLinkURL();
    assertThat(result).isEqualTo("http://myserver:8081");
  }
}
