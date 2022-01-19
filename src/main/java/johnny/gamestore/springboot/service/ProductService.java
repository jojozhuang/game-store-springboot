package johnny.gamestore.springboot.service;

import johnny.gamestore.springboot.domain.Product;
import johnny.gamestore.springboot.exception.NotFoundException;
import johnny.gamestore.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
  @Autowired
  ProductRepository productRepository;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public Product findById(long id) throws NotFoundException {
    Optional<Product> product = productRepository.findById(id);

    if (product.isEmpty()) {
      throw new NotFoundException();
    }
    return product.get();
  }

  public Product create(Product product) {
    product.setId(0L);
    return productRepository.save(product);
  }

  public Product update(Product product) {
    return productRepository.save(product);
  }

  public void delete(long id) {
    productRepository.deleteById(id);
  }

  public boolean exists(long id) {
    return productRepository.existsById(id);
  }
}
