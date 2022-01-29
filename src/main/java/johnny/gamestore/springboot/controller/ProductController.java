package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController {
  @Autowired
  ProductService productService;

  // GET /products
  @Operation(summary = "Get all products", description = "Get all products sorted by id", tags = { "Products" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))) })
  @GetMapping("")
  public Iterable<Product> findAll() {
    List<Product> products = productService.findAll();
    products.forEach(product -> {
      product.setImage(getBaseUrl() + product.getImage());
    });
    Collections.reverse(products);
    return products;
  }

  // GET /products/5
  @Operation(summary = "Get one product", description = "Get one product by id", tags = { "Products" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get one product by id",
        content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "400", description = "Bad request",
        content = @Content)})
  @GetMapping("/{id}")
  public ResponseEntity<Product> findOne(@PathVariable(value = "id") long id) throws Exception {
    if (!productService.exists(id)) {
      return ResponseEntity.notFound().build();
    }
    Product product = productService.findById(id);
    product.setImage(getBaseUrl() + product.getImage());
    return ResponseEntity.ok().body(product);
  }

  // POST /products
  @Operation(summary = "Create new product", description = "Create new product", tags = { "Products" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created one new product",
        content = {@Content(mediaType = "application/json")}) })
  @PostMapping("")
  public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
    product.setImage(product.getImage().replace(getBaseUrl(), ""));
    Product newProduct = productService.create(product);

    return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
  }

  // PUT /products/5
  @Operation(summary = "Update one product", description = "Update one product", tags = { "Products" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update one product by id",
        content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", description = "Product with the given id is not found",
        content = @Content)})
  @PutMapping("/{id}")
  public ResponseEntity<Product> update(@PathVariable(value = "id") Long id,
                                        @Valid @RequestBody Product product) throws Exception {
    if (!productService.exists(id)) {
      return ResponseEntity.notFound().build();
    }
    Product oldProduct = productService.findById(id);
    oldProduct.setProductName(product.getProductName());
    oldProduct.setPrice(product.getPrice());
    oldProduct.setImage(product.getImage().replace(getBaseUrl(), ""));

    Product updProduct = productService.update(oldProduct);
    return ResponseEntity.ok(updProduct);
  }

  // DELETE /products/5
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete one product", description = "Delete one product by id", tags = { "Products" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product is deleted",
        content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", description = "Product with the given id is not found",
        content = @Content)})
  public ResponseEntity<Product> delete(@PathVariable(value = "id") long id) {
    if (!productService.exists(id)) {
      return ResponseEntity.notFound().build();
    }

    productService.delete(id);
    return ResponseEntity.ok().build();
  }
}
