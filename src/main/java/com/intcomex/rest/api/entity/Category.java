package com.intcomex.rest.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryID;

    @Column(unique = true, nullable = false)
    private String categoryName;

    @Column(length = 500)
    private String description;

    private String picture;
}
