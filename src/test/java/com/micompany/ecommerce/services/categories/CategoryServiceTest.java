package com.micompany.ecommerce.services.categories;

import com.micompany.ecommerce.dto.categories.CategoryRequestDto;
import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.repositories.CategoryRepository;
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
        category1.setId(1L);
        category1.setName("Deportes");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Tecnología");

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
        requestDto.setDescription("Productos deportivos");

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {

                    Category category = invocation.getArgument(0);
                    category.setId(1L);

                    return category;
                });

        // Act
        CategoryResponseDto result = categoryService.createCategory(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Deportes", result.getName());

    }

    // ========== Pruebas metodo updateCategory() ==============
    @Test
    void updateCategory_debeDarError_cuandoCategoriaNoExiste() {

        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Deportes");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, request)
        );

        assertEquals("Category", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(99L, exception.getFieldValue());

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
        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(99L)
        );

        verify(categoryRepository, never()).deleteById(anyLong());

    }

    @Test
    void deleteCategory_debeEliminarCategoria_cuandoExiste() {

        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).deleteById(1L);

    }
}