package johnny.gamestore.springboot.repository;

import johnny.gamestore.springboot.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findAllByPrice(double price, Pageable pageable);
}
