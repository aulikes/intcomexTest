package com.intcomex.rest.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.intcomex.rest.api.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select count(c) > 0 from Category c where lower(c.categoryName) = lower(:name)")
    boolean existsByCategoryNameIgnoreCase (@Param("name") String categoryName);

}