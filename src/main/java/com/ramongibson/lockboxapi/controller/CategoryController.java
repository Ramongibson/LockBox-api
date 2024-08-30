package com.ramongibson.lockboxapi.controller;

import com.ramongibson.lockboxapi.model.Category;
import com.ramongibson.lockboxapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "APIs for managing password categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Add a new category", description = "Adds a new category to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestParam String name) {
        Category category = categoryService.addCategory(name);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Get all categories", description = "Retrieves all available categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

}