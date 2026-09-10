package com.nexamart.backend.config;

import com.nexamart.backend.domain.Category;
import com.nexamart.backend.domain.Product;
import com.nexamart.backend.repository.CategoryRepository;
import com.nexamart.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class StartupSeeder {
  @Bean CommandLineRunner seedCatalog(CategoryRepository cats, ProductRepository products) {
    return args -> {
      if (cats.count() == 0) {
        Category grocery = new Category();
        grocery.setName("Groceries");
        grocery.setDescription("Everyday grocery essentials");
        grocery.setSortOrder(1);
        grocery = cats.save(grocery);

        Product rice = new Product();
        rice.setName("Premium Rice 5kg");
        rice.setCategory(grocery);
        rice.setPrice(new BigDecimal("420.00"));
        rice.setDiscount(BigDecimal.ZERO);
        rice.setUnit("5 kg");
        rice.setStock(100);
        rice.setAvailable(true);
        rice.setSku("RICE-5KG");
        products.save(rice);

        Product oil = new Product();
        oil.setName("Sunflower Oil 1L");
        oil.setCategory(grocery);
        oil.setPrice(new BigDecimal("145.00"));
        oil.setDiscount(BigDecimal.ZERO);
        oil.setUnit("1 L");
        oil.setStock(100);
        oil.setAvailable(true);
        oil.setSku("OIL-1L");
        products.save(oil);
      }
    };
  }
}
