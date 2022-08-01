package johnny.gamestore.springboot.service;

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
