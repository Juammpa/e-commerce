package com.micompany.ecommerce.services.products;

import com.micompany.ecommerce.dto.products.ProductRequestDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.mappers.Mapper;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.repositories.CategoryRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<ProductResponseDto> getList(Long categoryId) {

        if(categoryId==null) {
            return productRepository.findAll().stream().map(Mapper::toDTO).toList();
        }

        return productRepository.findAll().stream().map(Mapper::toDTO).
                filter(p -> p.getCategoryId().equals(categoryId)).toList();

    }

    @Override
    public ProductResponseDto getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id", id));

        return Mapper.toDTO(product);
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        return Mapper.toDTO(productRepository.save(product));

    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",id));

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        return Mapper.toDTO(productRepository.save(product));

    }

    @Override
    public void deleteProduct(Long id) {

        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product","id",id);
        }

        productRepository.deleteById(id);

    }
}
