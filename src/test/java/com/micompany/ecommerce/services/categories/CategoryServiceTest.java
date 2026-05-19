package com.micompany.ecommerce.services.categories;

import com.micompany.ecommerce.dto.categories.CategoryRequestDto;
import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // ============= Pruebas metodo getAllCategories() ==============
    @Test
    void getAllCategories_debeDevolverListaCategorias() {

        // Arrange
        Category category1 = new Category();
        Category category2 = new Category();

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        // Act
        List<CategoryResponseDto> listCategories = categoryService.getAllCategories();

        // Assert
        assertNotNull(listCategories);
        assertEquals(2, listCategories.size());

    }

    // ============ Pruebas metodo createCategory() ================
    @Test
    void createCategory_debeCrearCategoria() {

        // Arrange

        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Deportes");

        Category category = new Category();
        category.setName("Deportes");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponseDto result = categoryService.createCategory(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Deportes", result.getName());

    }

    // ========== Pruebas metodo updateCategory() ==============
    @Test
    void updateCategory_debeDarError_cuandoCategoriaNoExiste() {

        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        CategoryRequestDto requestDto = new CategoryRequestDto();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            categoryService.updateCategory(99L, requestDto);
        });

    }

    @Test
    void updateCategory_debeActualizarCategoria() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Ropa");
        category.setDescription("Indumentaria");

        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Deportes");
        requestDto.setDescription("Todo relacionado al deporte");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponseDto result = categoryService.updateCategory(1L, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Deportes", result.getName());
        assertEquals("Todo relacionado al deporte", result.getDescription());
    }

    // ======== Pruebas metodo deleteCategory() =============
    @Test
    void deleteCategory_debeDarError_cuandoCategoriaNoExiste() {

        // Arrange
        when(categoryRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            categoryService.deleteCategory(99L);
        });

    }

    @Test
    void deleteCategory_debeEliminarCategoria_cuandoExiste() {

        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository,times(1)).deleteById(1L);

    }
}