package com.micompany.ecommerce.services.products;

import com.micompany.ecommerce.dto.products.ProductRequestDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;

import java.util.List;

public interface IProductService {

    List<ProductResponseDto> getList(Long categoryId);

    ProductResponseDto getProduct(Long id);

    ProductResponseDto createProduct(ProductRequestDto request);

    ProductResponseDto updateProduct(Long id, ProductRequestDto request);

    void deleteProduct(Long id);

}
