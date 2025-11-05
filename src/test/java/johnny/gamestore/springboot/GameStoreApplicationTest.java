/**
 * Copyright (c) 2025 Johnny, Inc.
 * All rights reserved. Patents pending.
 */

package johnny.gamestore.springboot;

import static org.mockito.Mockito.mockStatic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GameStoreApplicationTest {
  @Test
  void mainMethodShouldRunSpringApplication() {
    try (var mocked = mockStatic(SpringApplication.class)) {
      GameStoreApplication.main(new String[] {});
      mocked.verify(() -> SpringApplication.run(GameStoreApplication.class, new String[] {}));
    }
  }
}
