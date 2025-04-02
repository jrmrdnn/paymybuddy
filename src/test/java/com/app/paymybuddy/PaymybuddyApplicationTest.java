package com.app.paymybuddy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class PaymybuddyApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void applicationStartsSuccessfully() {
    PaymybuddyApplication.main(new String[] {});
  }

  @Test
  void contextLoads() {
    assertNotNull(applicationContext);

    // Check Controllers
    assertBeansExist(
      List.of("authController", "relationController", "transferController")
    );

    // Check Services
    assertBeansExist(
      List.of("registerService", "relationService", "transactionService")
    );

    // Check Repositories
    assertBeansExist(
      List.of("userRepository", "relationRepository", "transactionRepository")
    );
  }

  /**
   * Assert that the beans exist in the application context.
   * @param beanNames
   */
  private void assertBeansExist(List<String> beanNames) {
    for (String beanName : beanNames) {
      assertTrue(applicationContext.containsBean(beanName));
    }
  }
}
