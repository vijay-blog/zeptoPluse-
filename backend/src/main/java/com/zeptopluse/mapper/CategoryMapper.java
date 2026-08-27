package com.zeptopluse.mapper;

import com.zeptopluse.dto.CategoryResponse;
import com.zeptopluse.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug(), category.getDescription(), category.getImageUrl(), category.isActive(), category.getDisplayOrder());
    }
}
