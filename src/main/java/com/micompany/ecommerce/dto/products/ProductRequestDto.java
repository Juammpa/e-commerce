package com.micompany.ecommerce.dto.products;

import com.micompany.ecommerce.models.entities.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "The name is mandatory")
    private String name;


    @NotNull(message = "The price is mandatory")
    private Double price;

    @NotNull(message = "The stock is mandatory")
    @Min(value = 1, message = "The stock must be at least 1")
    private Integer stock;

    @NotNull(message = "The category is mandatory")
    private Long categoryId;

}
