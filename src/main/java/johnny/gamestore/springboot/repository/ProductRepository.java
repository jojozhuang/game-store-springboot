package johnny.gamestore.springboot.repository;

import johnny.gamestore.springboot.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
