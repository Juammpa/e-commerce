package com.micompany.ecommerce.services.categories;

import com.micompany.ecommerce.dto.categories.CategoryRequestDto;
import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.mappers.Mapper;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Get list of categories
    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    // 2. Create new category
    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto request) {

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        // Saving in the repository and return
        return Mapper.toDTO(categoryRepository.save(category));
    }

    // 3. Updata an existing category
    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {

        Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Category with ID: " + id + " not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return Mapper.toDTO(categoryRepository.save(category));
    }

    // 4. Delete category
    @Override
    public void deleteCategory(Long id) {

        if(!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with ID: " + id + "not exists");
        }

        categoryRepository.deleteById(id);

    }
}
