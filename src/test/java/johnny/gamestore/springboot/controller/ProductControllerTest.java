package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.property.UrlConfigProperties;
import johnny.gamestore.springboot.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(UrlConfigProperties.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest extends BaseControllerTest {
  @MockBean
  ProductService productService;

  @Autowired
  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testFindAll() throws Exception {
    when(productService.findAll()).thenReturn(List.of(mockProduct1()));

    mockMvc.perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].productName").isNotEmpty())
        .andExpect(jsonPath("$[0].productName").value("Xbox 360"));
  }

  @Test
  public void testFindOne() throws Exception {
    when(productService.exists(1)).thenReturn(true);
    when(productService.findById(1)).thenReturn(mockProduct1());

    mockMvc.perform(get("/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productName").value("Xbox 360"));
  }

  @Test
  public void testCreate() throws Exception {
    when(productService.create(any())).thenReturn(mockProduct1WithId());

    mockMvc.perform(post("/api/products")
        .content(asJsonString(mockProduct1()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  public void testUpdate() throws Exception {
    Product product1 = mockProduct1WithId();
    Product product2 = mockProduct2();
    Product product3 = mockProduct2WithId();
    product3.setId(1L);
    when(productService.exists(1)).thenReturn(true);
    when(productService.findById(1)).thenReturn(product1);
    when(productService.update(any())).thenReturn(product3);

    mockMvc.perform(put("/api/products/1")
        .content(asJsonString(product2))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(product3.getId()))
        .andExpect(jsonPath("$.productName").value(product3.getProductName()))
        .andExpect(jsonPath("$.price").value(product3.getPrice()))
        .andExpect(jsonPath("$.image").value(product3.getImage()));
  }

  @Test
  public void testDelete() throws Exception {
    when(productService.exists(1)).thenReturn(true);

    mockMvc.perform(delete("/api/products/1"))
        .andExpect(status().isOk());
  }
}