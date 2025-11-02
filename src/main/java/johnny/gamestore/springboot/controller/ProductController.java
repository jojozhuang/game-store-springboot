package johnny.gamestore.springboot.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import johnny.gamestore.springboot.model.Product;
import johnny.gamestore.springboot.model.ProductDto;
import johnny.gamestore.springboot.model.ProductRequest;
import johnny.gamestore.springboot.paging.PagedResource;
import johnny.gamestore.springboot.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;

@Tag(
    name = "Product Management",
    description = "APIs to manage product listings, including creation, updates, and queries")
@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController {
  @Autowired
  ProductService productService;

  // GET /products
  @Operation(summary = "Get all products", description = "Get all products sorted by id",
      tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieved products",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))) })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Product> findAll() {
    List<Product> products = productService.findAll();
    products.forEach(product -> product.setImage(getBaseUrl() + product.getImage()));
    Collections.reverse(products);
    return products;
  }

  @Operation(summary = "Get all products in pagination", description = "Get all products in pagination",
      tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieved products",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))) })
  @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Product> findAllPagination(
      @RequestParam(value = "price", required = false, defaultValue = "269") String price,
      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
      @RequestParam(value = "size", required = false, defaultValue = "3") int size,
      @RequestParam(value = "sortby", required = false, defaultValue = "id") String sortBy) {
    Page<Product> result = productService
        .findAllByPrice(ProductRequest.builder()
            .price(Double.parseDouble(price))
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .build());

    return result.getContent();
  }

  @Operation(summary = "Get all products in custom pagination", description = "Get all products in custom pagination",
      tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieved products",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))) })
  @GetMapping(value = "all-custom", produces = MediaType.APPLICATION_JSON_VALUE)
  public PagedResource<Product> findAllCustomPagination(
      @RequestParam(value = "price", required = false, defaultValue = "269") String price,
      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
      @RequestParam(value = "size", required = false, defaultValue = "3") int size,
      @RequestParam(value = "sortby", required = false, defaultValue = "id") String sortBy) {
    Page<Product> result = productService
        .findAllByPrice(ProductRequest.builder()
            .price(Double.parseDouble(price))
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .build());

    PagedResourcesAssembler<Product> productPagedResourcesAssembler =
        new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(), null);

    WebMvcLinkBuilder linkBuilder = linkTo(methodOn(this.getClass())
        .findAllCustomPagination(price, page, size, sortBy));

    return new PagedResource<>(productPagedResourcesAssembler,
        linkBuilder,
        result,
        getBaseUrl());
  }

  // GET /products/5
  @Operation(summary = "Get one product", description = "Get one product by id", tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieved one product by id",
        content = {@Content(schema = @Schema(implementation = Product.class))}),
      @ApiResponse(responseCode = "404", description = "Product is not found with the given id",
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
  @Operation(summary = "Create new product", description = "Create new product", tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created one new product",
        content = {@Content(schema = @Schema(implementation = Product.class))})})
  @PostMapping("")
  public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto productDto) {
    // Convert DTO to entity
    Product product = new Product();
    product.setProductName(productDto.getProductName());
    product.setPrice(productDto.getPrice());
    product.setImage(productDto.getImage().replace(getBaseUrl(), ""));
    Product newProduct = productService.create(product);

    // Convert entity to DTO with builder
    ProductDto response = ProductDto.builder()
        .id(newProduct.getId())
        .productName(newProduct.getProductName())
        .price(newProduct.getPrice())
        .image(newProduct.getImage())
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // PUT /products/5
  @Operation(summary = "Update one product", description = "Update one product", tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update one product by id",
        content = {@Content(schema = @Schema(implementation = Product.class))}),
      @ApiResponse(responseCode = "404", description = "Product is not found with the given id",
        content = @Content)})
  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> update(@PathVariable(value = "id") Long id,
                                           @Valid @RequestBody ProductDto productDto) throws Exception {
    if (!productService.exists(id)) {
      return ResponseEntity.notFound().build();
    }
    Product currentProduct = productService.findById(id);
    currentProduct.setProductName(productDto.getProductName());
    currentProduct.setPrice(productDto.getPrice());
    currentProduct.setImage(productDto.getImage().replace(getBaseUrl(), ""));

    Product newProduct = productService.update(currentProduct);
    ProductDto response = ProductDto.builder()
        .id(newProduct.getId())
        .productName(newProduct.getProductName())
        .price(newProduct.getPrice())
        .image(newProduct.getImage())
        .build();

    return ResponseEntity.ok(response);
  }

  // DELETE /products/5
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete one product", description = "Delete one product by id", tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted product",
        content = {@Content(schema = @Schema(implementation = Product.class))}),
      @ApiResponse(responseCode = "404", description = "Product is not found with the given id",
        content = @Content)})
  public ResponseEntity<Product> delete(@PathVariable(value = "id") long id) {
    if (!productService.exists(id)) {
      return ResponseEntity.notFound().build();
    }

    productService.delete(id);
    return ResponseEntity.ok().build();
  }
}
