package com.zeptopluse.repository;

import com.zeptopluse.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailabilityTrueOrderByNameAsc();
    List<Product> findByCategoryIdAndAvailabilityTrueOrderByNameAsc(Long categoryId);

    @Query("""
            select p from Product p join p.category c
            where p.availability = true
              and (lower(p.name) like lower(concat('%', :query, '%'))
                or lower(p.brand) like lower(concat('%', :query, '%'))
                or lower(p.sku) like lower(concat('%', :query, '%'))
                or lower(c.name) like lower(concat('%', :query, '%')))
            order by p.name asc
            """)
    List<Product> searchActive(@Param("query") String query);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p join fetch p.category where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
