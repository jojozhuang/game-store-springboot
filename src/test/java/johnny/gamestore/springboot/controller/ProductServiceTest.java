package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.exception.NotFoundException;
import johnny.gamestore.springboot.repository.ProductRepository;
import johnny.gamestore.springboot.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {
    @Autowired
    ProductService productService;

    @MockBean
    ProductRepository productRepository;

    @Test
    public void testGetAllProducts() {
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
    public void testGetProductById() throws Exception {
        Product mockProduct = new Product();
        mockProduct.setProductName("Wii");

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product product = productService.findById(1);
        assertThat(product).isEqualTo(mockProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    public void testGetProductByIdNotFound() {
        Optional<Product> mockProduct = Optional.empty();

        when(productRepository.findById(1L)).thenReturn(mockProduct);

        assertThrows(NotFoundException.class, () -> {
            Product product = productService.findById(1);
        });
        verify(productRepository).findById(1L);
    }

    @Test
    public void testExistsById() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean exists = productService.existsById(1);
        assertThat(exists).isEqualTo(true);
    }
}
