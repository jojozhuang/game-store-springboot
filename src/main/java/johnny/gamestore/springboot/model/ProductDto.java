/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductDto {
  Long id;
  String productName;
  double price;
  String image;
}
