package com.micompany.ecommerce.models.entities;

import com.micompany.ecommerce.models.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name= "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
    private String description;

    @Column(nullable = false)
    private Double price = 0.0;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

}
