package com.micompany.ecommerce.mappers;

import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.models.entities.Category;

public class Mapper {

    public static CategoryResponseDto toDTO(Category category) {

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

}
