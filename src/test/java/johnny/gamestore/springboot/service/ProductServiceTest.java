/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import johnny.gamestore.springboot.exception.NotFoundException;
import johnny.gamestore.springboot.model.Product;
import johnny.gamestore.springboot.model.ProductRequest;
import johnny.gamestore.springboot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
  @InjectMocks ProductService productService;

  @Mock ProductRepository productRepository;

  @Test
  public void testFindAll() {
    Product mockProduct1 = new Product();
    mockProduct1.setProductName("Wii");
    Product mockProduct2 = new Product();
    mockProduct2.setProductName("XBox");

    when(productRepository.findAll()).thenReturn(List.of(mockProduct1, mockProduct2));
    List<Product> products = productService.findAll();

    assertThat(products.size()).isEqualTo(2);
    assertThat(products.get(0)).isEqualTo(mockProduct1);
    assertThat(products.get(1)).isEqualTo(mockProduct2);
    verify(productRepository).findAll();
  }

  @Test
  public void testFindById() throws Exception {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wii");

    when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
    Product product = productService.findById(1);

    assertThat(product).isEqualTo(mockProduct);
    verify(productRepository).findById(1L);
  }

  @Test
  public void testFindByIdNotFound() {
    Optional<Product> mockProduct = Optional.empty();

    when(productRepository.findById(1L)).thenReturn(mockProduct);

    assertThrows(
        NotFoundException.class,
        () -> {
          productService.findById(1);
        });
    verify(productRepository).findById(1L);
  }

  @Test
  public void testCreate() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wii");

    when(productRepository.save(any())).thenReturn(mockProduct);
    Product product = productService.create(mockProduct);

    assertThat(product).isEqualTo(mockProduct);
    verify(productRepository).save(any());
  }

  @Test
  public void testUpdate() {
    Product mockProduct = new Product();
    mockProduct.setProductName("Wii");

    when(productRepository.save(any())).thenReturn(mockProduct);
    Product product = productService.update(mockProduct);

    assertThat(product).isEqualTo(mockProduct);
    verify(productRepository).save(any());
  }

  @Test
  public void testDelete() {
    productService.delete(1L);

    verify(productRepository).deleteById(1L);
  }

  @Test
  public void testExists() {
    when(productRepository.existsById(1L)).thenReturn(true);
    boolean exists = productService.exists(1);

    assertThat(exists).isEqualTo(true);
    verify(productRepository).existsById(1L);
  }

  @Test
  public void findAllByPrice() {
    Product mockProduct1 = new Product();
    mockProduct1.setId(1L);
    mockProduct1.setPrice(269);
    mockProduct1.setProductName("Wii");
    Product mockProduct2 = new Product();
    mockProduct1.setId(2L);
    mockProduct2.setPrice(269);
    mockProduct2.setProductName("XBox");

    ProductRequest pr = ProductRequest.builder().price(269).page(0).size(10).sortBy("id").build();

    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

    Page page = new PageImpl(List.of(mockProduct1, mockProduct2));
    when(productRepository.findAllByPrice(269, pageable)).thenReturn(page);

    Page<Product> productPage = productService.findAllByPrice(pr);

    List<Product> products = productPage.getContent();
    assertThat(products.size()).isEqualTo(2);
    assertThat(products.get(0)).isEqualTo(mockProduct1);
    assertThat(products.get(1)).isEqualTo(mockProduct2);
    verify(productRepository).findAllByPrice(269, pageable);
  }
}
