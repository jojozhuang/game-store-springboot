package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.exception.NotFoundException;
import johnny.gamestore.springboot.repository.ProductRepository;
import johnny.gamestore.springboot.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    public void getProductById () throws Exception {

        Product mockProduct = new Product();
        mockProduct.setProductName("Wii");

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product product = productService.findById(1);

        assertThat(product).isEqualTo(mockProduct);

        verify(productRepository).findById(1L);
    }

    @Test
    public void getProductByIdNotFound() {

        Optional<Product> mockProduct = Optional.empty();

        when(productRepository.findById(1L)).thenReturn(mockProduct);

        assertThrows(NotFoundException.class, () -> {
            Product product = productService.findById(1);
        });

        verify(productRepository).findById(1L);
    }

}
