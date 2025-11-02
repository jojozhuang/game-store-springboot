package johnny.gamestore.springboot.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import johnny.gamestore.springboot.property.UrlConfigProperties;
import johnny.gamestore.springboot.util.UrlUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaseControllerTest {
  @InjectMocks
  private BaseController baseController;  // Mockito will inject mocks here

  @Mock
  private UrlConfigProperties urlConfigProperties;  // Mocked dependency

  @Test
  void getBaseUrl_returnsConfiguredUrl_whenNotEmpty() {
    when(urlConfigProperties.getBaseUrl()).thenReturn("https://example.com");

    String result = baseController.getBaseUrl();

    assertEquals("https://example.com", result);
    verify(urlConfigProperties, times(1)).getBaseUrl();
  }

  @Test
  void getBaseUrl_returnsEnvUrl_whenConfiguredUrlEmpty() {
    when(urlConfigProperties.getBaseUrl()).thenReturn("");

    try (MockedStatic<UrlUtil> mockedStatic = mockStatic(UrlUtil.class)) {
      mockedStatic.when(UrlUtil::getBaseEnvLinkURL).thenReturn("https://env.example.com");

      String result = baseController.getBaseUrl();

      assertEquals("https://env.example.com", result);

      mockedStatic.verify(UrlUtil::getBaseEnvLinkURL, times(1));
    }

    verify(urlConfigProperties, times(1)).getBaseUrl();
  }
}
