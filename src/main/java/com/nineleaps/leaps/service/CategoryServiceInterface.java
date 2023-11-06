package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.categories.Category;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface CategoryServiceInterface {
    void createCategory(CategoryDto categoryDto);

    List<Category> listCategory();

    void updateCategory(Long id, CategoryDto updateCategory);

    Category readCategory(String categoryName);

    Optional<Category> readCategory(Long id);

    List<Category> getCategoriesFromIds(List<Long> categoryIds);
}
