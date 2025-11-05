/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.controller.integration;

import static org.assertj.core.api.Assertions.assertThat;
import johnny.gamestore.springboot.GameStoreApplication;
import johnny.gamestore.springboot.helper.TestHelper;
import johnny.gamestore.springboot.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    classes = GameStoreApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {
  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  private String getRootUrl() {
    return "http://localhost:" + port + "/api/products";
  }

  @Test
  public void testGetAllProducts() {
    ResponseEntity<Product[]> response = restTemplate.getForEntity(getRootUrl(), Product[].class);

    Product[] products = response.getBody();
    assertThat(products).isNotNull();
    assertThat(products.length).isEqualTo(3);
    assertProduct(products[0], TestHelper.mockProduct3());
    assertProduct(products[1], TestHelper.mockProduct2());
    assertProduct(products[2], TestHelper.mockProduct1());
  }

  @Test
  public void testGetProductById() {
    Product mockProduct = TestHelper.mockProduct1();
    Product product = restTemplate.getForObject(getRootUrl() + "/1", Product.class);

    assertProduct(product, mockProduct);
  }

  @Test
  public void testCreateProduct() {
    Product mockProduct = TestHelper.mockProduct1();
    ResponseEntity<Product> postResponse =
        restTemplate.postForEntity(getRootUrl(), mockProduct, Product.class);

    assertThat(postResponse).isNotNull();
    assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Product product = postResponse.getBody();
    assertProduct(product, mockProduct);

    // reset data
    restTemplate.delete(getRootUrl() + "/4");
  }

  @Test
  public void testUpdateProduct() {
    int id = 2;
    Product product = restTemplate.getForObject(getRootUrl() + "/" + id, Product.class);
    product.setProductName("PS4");
    product.setPrice(567);
    restTemplate.put(getRootUrl() + "/" + id, product);

    Product updatedProduct = restTemplate.getForObject(getRootUrl() + "/" + id, Product.class);
    assertThat(updatedProduct).isNotNull();
    assertProduct(updatedProduct, product);

    // reset data
    Product mockProduct = TestHelper.mockProduct2();
    product.setProductName(mockProduct.getProductName());
    product.setPrice(mockProduct.getPrice());
    restTemplate.put(getRootUrl() + "/" + id, product);
  }

  @Test
  public void testDeleteProduct() {
    int id = 3;
    Product product = restTemplate.getForObject(getRootUrl() + "/" + id, Product.class);
    assertThat(product).isNotNull();

    restTemplate.delete(getRootUrl() + "/" + id);

    product = restTemplate.getForObject(getRootUrl() + "/" + id, Product.class);
    assertThat(product).isNull();

    // reset data
    Product mockProduct = TestHelper.mockProduct3();
    restTemplate.postForEntity(getRootUrl(), mockProduct, Product.class);
  }

  private void assertProduct(Product actual, Product expected) {
    assertThat(actual).isNotNull();
    assertThat(actual.getProductName()).isEqualTo(expected.getProductName());
    assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    assertThat(actual.getImage()).contains(expected.getImage());
  }
}
