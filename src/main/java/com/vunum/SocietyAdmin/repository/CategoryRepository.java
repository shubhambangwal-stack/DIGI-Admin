package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoryById(Long categoryId);
}