package johnny.gamestore.springboot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseResult {
  private int statusCode;
  private String message;
}
