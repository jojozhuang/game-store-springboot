package johnny.gamestore.springboot.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequest {
  private double price;

  private int page;

  private int size;

  private String sortBy;
}
