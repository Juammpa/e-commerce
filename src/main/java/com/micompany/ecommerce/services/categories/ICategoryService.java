package com.micompany.ecommerce.services.categories;

import com.micompany.ecommerce.dto.categories.CategoryRequestDto;
import com.micompany.ecommerce.dto.categories.CategoryResponseDto;

import java.util.List;

public interface ICategoryService {

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto createCaegory(CategoryRequestDto request);

    CategoryResponseDto updateCategory(Long id,CategoryRequestDto request);

    void deleteCategory(Long id);

}
