package com.zeptopluse.controller;

import com.zeptopluse.dto.ProductResponse;
import com.zeptopluse.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    public List<ProductResponse> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        if (search != null) {
            if (categoryId != null) {
                throw new IllegalArgumentException("Use either categoryId or search, not both");
            }
            return service.search(search);
        }
        return categoryId == null ? service.list() : service.byCategory(categoryId);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> byCategory(@PathVariable Long categoryId) {
        return service.byCategory(categoryId);
    }

    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam String query) {
        return service.search(query);
    }
}