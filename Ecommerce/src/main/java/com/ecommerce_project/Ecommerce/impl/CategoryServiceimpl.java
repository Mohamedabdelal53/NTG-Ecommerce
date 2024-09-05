package com.ecommerce_project.Ecommerce.impl;

import com.ecommerce_project.Ecommerce.DTO.CategoryDTO;

import java.util.List;

public interface CategoryServiceimpl {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getcategories();
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long id);
    void deleteCategory(Long id);
}
