package johnny.gamestore.springboot.controller;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController{
    @Autowired
    ProductRepository productRepository;

    // GET /products
    @GetMapping("")
    public Iterable<Product> findAll(){
        List<Product> products = new ArrayList<>();
        Iterator<Product> iterator = productRepository.findAll().iterator();
        //iterator.forEachRemaining(products::add);
        iterator.forEachRemaining(product -> {
            product.setImage(getApiUrl() + product.getImage());
            products.add(product);
        });
        Collections.reverse(products);
        return products;
    }

    // GET /products/5
    @GetMapping("/{id}")
    public ResponseEntity<Product> findOne(@PathVariable(value = "id") long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if(!productOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productOptional.get();
        product.setImage(getApiUrl() + product.getImage());
        return ResponseEntity.ok().body(product);
    }

    // POST /products
    @PostMapping("")
    public ResponseEntity<Product> create(@Valid @RequestBody Product product){
        product.setImage(product.getImage().replace(getApiUrl(), ""));
        Product newProduct = productRepository.save(product);
        return ResponseEntity.ok(newProduct);
    }

    // PUT /products/5
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable(value = "id") Long id, 
                                          @Valid @RequestBody Product product) {
        Optional<Product> productOptional = productRepository.findById(id);
        if(!productOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Product oldProduct = productOptional.get();
        oldProduct.setProductName(product.getProductName());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setImage(product.getImage().replace(getApiUrl(), ""));

        Product updProduct = productRepository.save(oldProduct);
        return ResponseEntity.ok(updProduct);
    }

    // DELETE /products/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> delete(@PathVariable(value = "id") long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = productOptional.get();
        if(product == null) {
            return ResponseEntity.notFound().build();
        }

        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
}
