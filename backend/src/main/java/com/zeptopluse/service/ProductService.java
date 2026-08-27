package com.zeptopluse.service;

import com.zeptopluse.dto.ProductResponse;
import com.zeptopluse.entity.Product;
import com.zeptopluse.exception.ResourceNotFoundException;
import com.zeptopluse.mapper.ProductMapper;
import com.zeptopluse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final ProductMapper mapper;

    public List<ProductResponse> list() { return map(products.findByAvailabilityTrueOrderByNameAsc()); }
    public ProductResponse get(Long id) { return mapper.toResponse(products.findById(id).filter(Product::isAvailability).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id))); }
    public List<ProductResponse> byCategory(Long categoryId) {
        if (!categories.existsById(categoryId)) throw new ResourceNotFoundException("Category not found: " + categoryId);
        return map(products.findByCategoryIdAndAvailabilityTrueOrderByNameAsc(categoryId));
    }
    public List<ProductResponse> search(String query) {
        if (query == null || query.trim().isBlank()) throw new IllegalArgumentException("query must not be blank");
        return map(products.searchActive(query.trim()));
    }
    private List<ProductResponse> map(List<Product> products) { return products.stream().map(mapper::toResponse).toList(); }
}
