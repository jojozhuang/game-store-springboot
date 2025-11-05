package johnny.gamestore.springboot.helper;

import johnny.gamestore.springboot.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestHelper {
  private static final double PRICE_XBOX = 299.00;
  private static final double PRICE_WII = 269.00;
  private static final double PRICE_CONTROLLER = 19.99;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Product mockProduct1() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Xbox 360");
    mockProduct.setPrice(PRICE_XBOX);
    mockProduct.setImage("/images/xbox360.jpg");
    return mockProduct;
  }

  public static Product mockProduct1WithId() {
    Product mockProduct = mockProduct1();
    mockProduct.setId(1L);
    return mockProduct;
  }

  public static Product mockProduct2() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wii");
    mockProduct.setPrice(PRICE_WII);
    mockProduct.setImage("/images/wii.jpg");
    return mockProduct;
  }

  public static Product mockProduct2WithId() {
    Product mockProduct = mockProduct2();
    mockProduct.setId(2L);
    return mockProduct;
  }

  public static Product mockProduct3() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wireless Controller");
    mockProduct.setPrice(PRICE_CONTROLLER);
    mockProduct.setImage("/images/controller.jpg");
    return mockProduct;
  }

  public static Product mockProduct3WithId() {
    Product mockProduct = mockProduct3();
    mockProduct.setId(3L);
    return mockProduct;
  }
}
