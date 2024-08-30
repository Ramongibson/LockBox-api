package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.model.Category;
import com.ramongibson.lockboxapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category addCategory(String name) {
        try {
            log.info("Attempting to add a new category with name: {}", name);

            if (categoryRepository.existsById(name)) {
                log.error("Category with name '{}' already exists", name);
                throw new RuntimeException("Category already exists");
            }

            Category category = new Category();
            category.setName(name);

            Category savedCategory = categoryRepository.save(category);
            log.info("Category '{}' added successfully", name);
            return savedCategory;
        } catch (Exception e) {
            log.error("Error occurred while adding category '{}': {}", name, e.getMessage(), e);
            throw new RuntimeException("An error occurred while adding the category");
        }
    }

    public List<Category> getAllCategories() {
        try {
            log.debug("Retrieving all categories");
            List<Category> categories = categoryRepository.findAll();
            log.info("Retrieved {} categories", categories.size());
            return categories;
        } catch (Exception e) {
            log.error("Error occurred while retrieving categories: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while retrieving categories");
        }
    }
}