package com.intcomex.rest.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import com.intcomex.rest.api.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
//    Page<Product> findAll(Pageable pageable);
//    Slice<Product> findAllBy(Pageable pageable);

    List<Product> findAllByProductName(String productName);
}