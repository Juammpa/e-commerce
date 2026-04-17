package com.micompany.ecommerce.services.products;

import com.micompany.ecommerce.dto.products.ProductRequestDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.mappers.Mapper;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.repositories.CategoryRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public List<ProductResponseDto> getList(Long id) {

        if(id==null) {
            return productRepository.findAll().stream().map(Mapper::toDTO).toList();
        }

        return productRepository.findAll().stream().map(Mapper::toDTO).
                filter(p -> p.getId().equals(id)).toList();

    }

    @Override
    public ProductResponseDto getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID: " + id + " not found."));

        return Mapper.toDTO(product);
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request) {

        if(!categoryRepository.existsById(request.getCategoryId())) {
            throw new EntityNotFoundException("The category ID not exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId()).get();

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
                .orElseThrow(() -> new EntityNotFoundException("Product with ID: " + id + " not found"));

        if(!categoryRepository.existsById(request.getCategoryId())) {
            throw new EntityNotFoundException("The category ID not exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId()).get();

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        return Mapper.toDTO(productRepository.save(product));

    }

    @Override
    public void deleteProduct(Long id) {

        if(!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product with ID: " +id+ " not found.");
        }

        productRepository.deleteById(id);

    }
}
