package johnny.gamestore.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import johnny.gamestore.springboot.domain.Product;

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
    mockProduct.setProductName("Wii");
    mockProduct.setPrice(269.00);
    mockProduct.setImage("/images/wii.jpg");
    return mockProduct;
  }

  protected Product mockProduct1WithId() {
    Product mockProduct = new Product();
    mockProduct.setId(1L);
    mockProduct.setProductName("Wii");
    mockProduct.setPrice(269.00);
    mockProduct.setImage("/images/wii.jpg");
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
    mockProduct.setProductName("Xbox 360");
    mockProduct.setPrice(299.00);
    mockProduct.setImage("/images/xbox360.jpg");
    return mockProduct;
  }
}
