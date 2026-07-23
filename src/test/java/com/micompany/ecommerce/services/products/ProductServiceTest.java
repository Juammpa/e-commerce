package com.micompany.ecommerce.services.products;

import com.micompany.ecommerce.dto.products.ProductRequestDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.repositories.CategoryRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getList_debeRetornarListaCompleta_cuandoCategoriaEsNull() {

        Category category1 = category(1L, "Deportes");
        Category category2 = category(2L, "Tecnología");

        Product product1 = product(1L, category1);
        Product product2 = product(2L, category2);

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponseDto> result =
                productService.getList(null);

        assertEquals(2, result.size());
    }

    @Test
    void getList_debeFiltrarPorCategoria() {

        Category category1 = category(1L, "Deportes");
        Category category2 = category(2L, "Tecnología");

        Product product1 = product(1L, category1);
        Product product2 = product(2L, category2);

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponseDto> result =
                productService.getList(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCategoryId());
    }

    @Test
    void getProduct_debeDarError_cuandoProductoNoExiste() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProduct(99L)
        );

        assertEquals("Product", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(99L, exception.getFieldValue());
    }

    @Test
    void getProduct_debeDevolverProducto_cuandoExiste() {

        Category category = category(1L, "Deportes");
        Product product = product(1L, category);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponseDto result =
                productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCategoryId());
    }

    @Test
    void createProduct_debeDarError_cuandoCategoriaNoExiste() {

        ProductRequestDto request = productRequest(99L);

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.createProduct(request)
        );

        assertEquals("Category", exception.getResourceName());
        assertEquals(99L, exception.getFieldValue());

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void createProduct_debeCrearProducto() {

        Category category = category(1L, "Deportes");
        ProductRequestDto request = productRequest(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {

                    Product savedProduct = invocation.getArgument(0);
                    savedProduct.setId(1L);

                    return savedProduct;
                });

        ProductResponseDto result =
                productService.createProduct(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCategoryId());
        assertEquals("Notebook", result.getName());
    }

    @Test
    void updateProduct_debeDarError_cuandoProductoNoExiste() {

        ProductRequestDto request = productRequest(1L);

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, request)
        );

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void updateProduct_debeDarError_cuandoCategoriaNoExiste() {

        Category currentCategory = category(1L, "Anterior");
        Product currentProduct = product(1L, currentCategory);
        ProductRequestDto request = productRequest(99L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(currentProduct));

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(1L, request)
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void updateProduct_debeActualizarProducto() {

        Category oldCategory = category(1L, "Anterior");
        Category newCategory = category(2L, "Nueva");

        Product currentProduct = product(1L, oldCategory);
        ProductRequestDto request = productRequest(2L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(currentProduct));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(newCategory));

        when(productRepository.save(currentProduct))
                .thenReturn(currentProduct);

        ProductResponseDto result =
                productService.updateProduct(1L, request);

        assertEquals("Notebook", result.getName());
        assertEquals(2L, result.getCategoryId());
        assertEquals(1000.0, result.getPrice());
        assertEquals(10, result.getStock());
    }

    @Test
    void deleteProduct_debeDarError_cuandoProductoNoExiste() {

        when(productRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L)
        );

        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteProduct_debeEliminarProducto_cuandoExiste() {

        when(productRepository.existsById(1L))
                .thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    private Category category(Long id, String name) {

        Category category = new Category();
        category.setId(id);
        category.setName(name);

        return category;
    }

    private Product product(Long id, Category category) {

        Product product = new Product();
        product.setId(id);
        product.setName("Notebook");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setCategory(category);

        return product;
    }

    private ProductRequestDto productRequest(Long categoryId) {

        ProductRequestDto request = new ProductRequestDto();
        request.setName("Notebook");
        request.setPrice(1000.0);
        request.setStock(10);
        request.setCategoryId(categoryId);

        return request;
    }
}