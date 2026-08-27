package com.zeptopluse.repository;

import com.zeptopluse.entity.CustomerOrder;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findByIdAndCustomerId(Long id, Long customerId);

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findByIdempotencyKey(String idempotencyKey);
}