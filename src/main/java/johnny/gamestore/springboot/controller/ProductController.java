package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController{
    @Autowired
    ProductService productService;

    // GET /products
    @GetMapping("")
    public Iterable<Product> findAll(){
        List<Product> products = productService.findAll();
        products.forEach(product -> {
            product.setImage(getBaseUrl() + product.getImage());
        });
        Collections.reverse(products);
        return products;
    }

    // GET /products/5
    @GetMapping("/{id}")
    public ResponseEntity<Product> findOne(@PathVariable(value = "id") long id) throws Exception {
        if(!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Product product = productService.findById(id);
        product.setImage(getBaseUrl() + product.getImage());
        return ResponseEntity.ok().body(product);
    }

    // POST /products
    @PostMapping("")
    public ResponseEntity<Product> create(@Valid @RequestBody Product product){
        product.setImage(product.getImage().replace(getBaseUrl(), ""));
        Product newProduct = productService.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    // PUT /products/5
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable(value = "id") Long id, 
                                          @Valid @RequestBody Product product) throws Exception  {
        if(!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Product oldProduct = productService.findById(id);
        oldProduct.setProductName(product.getProductName());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setImage(product.getImage().replace(getBaseUrl(), ""));

        Product updProduct = productService.save(oldProduct);
        return ResponseEntity.ok(updProduct);
    }

    // DELETE /products/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> delete(@PathVariable(value = "id") long id) {
        if(!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
