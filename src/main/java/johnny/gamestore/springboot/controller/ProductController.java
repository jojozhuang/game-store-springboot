package johnny.gamestore.springboot.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.paging.PagedResource;
import johnny.gamestore.springboot.property.UrlConfigProperties;
import johnny.gamestore.springboot.service.ProductRequest;
import johnny.gamestore.springboot.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController {
  Logger logger = LoggerFactory.getLogger(ProductController.class);
  @Autowired
  private UrlConfigProperties urlConfigProperties;
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
    //logger.info("Method {} of ProductController gets called.", "findAll()");
    logger.trace("A TRACE Message");
    logger.debug("A DEBUG Message");
    logger.info("An INFO Message");
    logger.warn("A WARN Message");
    logger.error("An ERROR Message");
    List<Product> products = productService.findAll();
    products.forEach(product -> {
      product.setImage(getBaseUrl() + product.getImage());
    });
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
        urlConfigProperties.getBaseUrl());
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
    logger.info("ID of the requested product: {}", id);
    if (!productService.exists(id)) {
      logger.warn("Product with ID {} doesn't exist!", id);
      return ResponseEntity.notFound().build();
    }
    Product product = productService.findById(id);
    logger.debug("Product is found: {}", product);
    product.setImage(getBaseUrl() + product.getImage());
    return ResponseEntity.ok().body(product);
  }

  // POST /products
  @Operation(summary = "Create new product", description = "Create new product", tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created one new product",
        content = {@Content(schema = @Schema(implementation = Product.class))})})
  @PostMapping("")
  public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
    product.setImage(product.getImage().replace(getBaseUrl(), ""));
    Product newProduct = productService.create(product);
    logger.debug("New product has been created: {}", newProduct);
    return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
  }

  // PUT /products/5
  @Operation(summary = "Update one product", description = "Update one product", tags = { "Product Controller" })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update one product by id",
        content = {@Content(schema = @Schema(implementation = Product.class))}),
      @ApiResponse(responseCode = "404", description = "Product is not found with the given id",
        content = @Content)})
  @PutMapping("/{id}")
  public ResponseEntity<Product> update(@PathVariable(value = "id") Long id,
                                        @Valid @RequestBody Product product) throws Exception {
    logger.info("ID of the product to be updated: {}", id);
    if (!productService.exists(id)) {
      logger.warn("Product with ID {} doesn't exist for update!", id);
      return ResponseEntity.notFound().build();
    }
    Product oldProduct = productService.findById(id);
    logger.debug("Product before update: {}", oldProduct);
    oldProduct.setProductName(product.getProductName());
    oldProduct.setPrice(product.getPrice());
    oldProduct.setImage(product.getImage().replace(getBaseUrl(), ""));

    Product updProduct = productService.update(oldProduct);
    logger.debug("Product after update: {}", updProduct);
    return ResponseEntity.ok(updProduct);
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
    logger.info("ID of the product to be deleted: {}", id);
    if (!productService.exists(id)) {
      logger.warn("Product with ID {} doesn't exist for deletion!", id);
      return ResponseEntity.notFound().build();
    }

    productService.delete(id);
    logger.debug("Product with id {} has been deleted.", id);
    return ResponseEntity.ok().build();
  }
}
