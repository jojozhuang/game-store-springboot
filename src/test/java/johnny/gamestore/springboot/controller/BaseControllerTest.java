package johnny.gamestore.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import johnny.gamestore.springboot.domain.Product;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseControllerTest {
  protected String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected Product mockProduct1() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Xbox 360");
    mockProduct.setPrice(299.00);
    mockProduct.setImage("/images/xbox360.jpg");
    return mockProduct;
  }

  protected Product mockProduct1WithId() {
    Product mockProduct = new Product();
    mockProduct.setId(1L);
    mockProduct.setProductName("Xbox 360");
    mockProduct.setPrice(299.00);
    mockProduct.setImage("/images/xbox360.jpg");
    return mockProduct;
  }

  protected Product mockProduct2() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wii");
    mockProduct.setPrice(269.00);
    mockProduct.setImage("/images/wii.jpg");
    return mockProduct;
  }

  protected Product mockProduct2WithId() {
    Product mockProduct = new Product();
    mockProduct.setId(2L);
    mockProduct.setProductName("Wii");
    mockProduct.setPrice(269.00);
    mockProduct.setImage("/images/wii.jpg");
    return mockProduct;
  }

  protected Product mockProduct3() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wireless Controller");
    mockProduct.setPrice(19.99);
    mockProduct.setImage("/images/controller.jpg");
    return mockProduct;
  }

  protected Product mockProduct3WithId() {
    Product mockProduct = new Product();
    mockProduct.setId(3L);
    mockProduct.setProductName("Wireless Controller");
    mockProduct.setPrice(19.99);
    mockProduct.setImage("/images/controller.jpg");
    return mockProduct;
  }

  protected void assertProduct(Product actual, Product expected) {
    assertThat(actual).isNotNull();
    assertThat(actual.getProductName()).isEqualTo(expected.getProductName());
    assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    assertThat(actual.getImage()).contains(expected.getImage());
  }
}
