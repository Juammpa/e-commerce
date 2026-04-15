package com.micompany.ecommerce.mappers;

import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.models.entities.Product;

public class Mapper {

    public static CategoryResponseDto toDTO(Category category) {

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public static ProductResponseDto toDTO(Product product) {

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .build();
    }

}
