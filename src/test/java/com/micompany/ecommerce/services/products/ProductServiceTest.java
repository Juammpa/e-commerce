package com.micompany.ecommerce.services.products;

import com.micompany.ecommerce.dto.products.ProductRequestDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.repositories.CategoryRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
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


    @BeforeEach
    void setUp() {
    }

    // =============== Pruebas metodo getList() =============
    @Test
    void getList_debeRetornarListaCompleta_cuandoIdCategoriaEsNull() {

        // Arrange
        Category category1 = new Category();
        category1.setName("Deportes");
        Category category2 = new Category();
        category2.setName("Juguetes");

        Product product1 = new Product();
        product1.setCategory(category1);
        Product product2 = new Product();
        product2.setCategory(category2);

        List<Product> listProducts = new ArrayList<>();
        listProducts.add(product1);
        listProducts.add(product2);

        when(productRepository.findAll()).thenReturn(listProducts);

        // Act
        List<ProductResponseDto> result = productService.getList(null);

        // Assert
        assertEquals(2, result.size());

    }

    @Test
    void getList_debeRetornarLista_cuandoFiltroCategoriaNoEsNull(){

        // Arrange
        Category category1 = new Category();
        category1.setName("Deportes");
        category1.setId(1L);

        Category category2 = new Category();
        category2.setName("Juguetes");
        category2.setId(2L);

        Product product1 = new Product();
        product1.setCategory(category1);
        Product product2 = new Product();
        product2.setCategory(category2);

        List<Product> listProducts = new ArrayList<>();
        listProducts.add(product1);
        listProducts.add(product2);

        when(productRepository.findAll()).thenReturn(listProducts);

        // Act
        List<ProductResponseDto> result = productService.getList(1L);

        // Assert
        assertEquals(1, result.size());

    }

    // ============== Pruebas metodo getProduct() ==============
    @Test
    void getProduct_debeDarError_cuandoProductoNoExiste() {

        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
           productService.getProduct(99L);
        });

    }

    @Test
    void getProduct_debeDevolverProducto_cuandoExiste() {

        // Arrange
        Category category = new Category();
        category.setName("Deportes");
        category.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setCategory(category);


        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponseDto result = productService.getProduct(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

    }

    // =========== Pruebas metodo createProduct() ===============
    @Test
    void createProduct_debeDarError_cuandoCategoriaNoExiste() {

        // Arrange
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setCategoryId(99L);

        when(categoryRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            productService.createProduct(requestDto);

        });
    }

    @Test
    void createProduct_debeCrearProducto_cuandoDatosSonCorrectos() {

        // Arrange
        Category category = new Category();
        category.setId(1L);

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setCategoryId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setCategory(category);

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponseDto result = productService.createProduct(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());

    }

    // =========== Pruebas metodo updateProduct() ===============

    @Test
    void updateProduct_debeDarError_cuandoProductoNoExiste() {

        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        ProductRequestDto requestDto = new ProductRequestDto();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            productService.updateProduct(1L,requestDto);

        });

    }

    @Test
    void updateProduct_debeDarError_cuandoCategoriaNoExiste() {

        // Arrange
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setCategoryId(99L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(categoryRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            productService.updateProduct(1L, requestDto);
        });
    }

    @Test
    void updateProduct_debeActualizarProducto_cuandoDatosSonCorrectos() {

        // Arrange

        Category category = new Category();
        category.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);

        // ProductRequest con datos actualizados
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setCategoryId(category2.getId());

        // Producto actual
        Product product = new Product();
        product.setId(1L);
        product.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.existsById(2L)).thenReturn(true);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponseDto result = productService.updateProduct(1L, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getCategoryId());

    }

    // ============== Pruebas metodo deleteProduct() ===========
    @Test
    void deleteProduct_debeDarError_cuandoProductoNoExiste() {

        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            productService.deleteProduct(99L);

        });
    }

    @Test
    void deleteProduct_debeEliminarProducto_cuandoExiste() {

        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);

    }
}