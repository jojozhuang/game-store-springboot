package johnny.gamestore.springboot.repository;

import johnny.gamestore.springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { }
