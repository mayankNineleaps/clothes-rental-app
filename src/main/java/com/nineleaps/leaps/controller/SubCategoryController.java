package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/v1/subcategory")
@AllArgsConstructor
@Api(tags = "Subcategory Api", description = "Contains APIs for adding subcategory, updating subcategory, and listing subcategories")
@SuppressWarnings("deprecation")
@Slf4j
public class SubCategoryController {

    /**
     * Status Code: 200 - HttpStatus.OK
     * Description: The request was successful, and the response contains the requested data.

     * Status Code: 201 - HttpStatus.CREATED
     * Description: The request was successful, and a new resource has been created as a result.

     * Status Code: 404 - HttpStatus.NOT_FOUND
     * Description: The requested resource could not be found but may be available in the future.

     */

    // Category service for category-related operations
    private final CategoryServiceInterface categoryService;

    // Subcategory service for subcategory-related operations
    private final SubCategoryServiceInterface subCategoryService;
    private final Helper helper;


    // API to create a new subcategory
    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> createSubCategory(@Valid @RequestBody SubCategoryDto subCategoryDto, HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            // Check if the parent category exists
            Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
            if (optionalCategory.isEmpty()) {
                log.error("Parent Category is invalid: {}", subCategoryDto.getCategoryId());
                return new ResponseEntity<>(new ApiResponse(false, "Parent Category is invalid"), HttpStatus.NOT_FOUND);
            }
            Category category = optionalCategory.get();

            // Check if subcategory with the same name exists in the same category
            if (Helper.notNull(subCategoryService.readSubCategory(subCategoryDto.getSubcategoryName(), category))) {
                log.warn("Sub Category already exists in category: {}", subCategoryDto.getSubcategoryName());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Sub Category already exists"));
            }

            // Create the subcategory
            subCategoryService.createSubCategory(subCategoryDto, category, user);
            log.info("Sub Category created successfully: {}", subCategoryDto.getSubcategoryName());
            return new ResponseEntity<>(new ApiResponse(true, "Category is created"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating subcategory", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal server error"));
        }
    }

    // API to list all subcategories
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<SubCategory>> listSubCategories() {
        // Get the list of all subcategories
        try {
            // Get the list of all subcategories
            List<SubCategory> body = subCategoryService.listSubCategory();
            log.info("List of subcategories fetched successfully.");
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while fetching the list of subcategories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // API to list subcategories by category id
    @GetMapping(value = "/listbyid/{categoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<SubCategory>> listSubCategoriesByCategoriesId(@PathVariable("categoryId") Long categoryId) {
        // Check if the category id is valid
        try {
            // Check if the category id is valid
            Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
            if (optionalCategory.isPresent()) {
                // Return all subcategories for the specified category
                List<SubCategory> body = subCategoryService.listSubCategory(categoryId);
                log.info("List of subcategories for category {} fetched successfully.", categoryId);
                return new ResponseEntity<>(body, HttpStatus.OK);
            } else {
                log.error("Category with id {} not found.", categoryId);
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error while fetching subcategories for category id {}", categoryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // API to update a subcategory
    @PutMapping(value = "/update/{subcategoryId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateSubCategory(@PathVariable("subcategoryId") Long subcategoryId, @Valid @RequestBody SubCategoryDto subCategoryDto,HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            // Check if the category is valid
            Optional<Category> optionalCategory = categoryService.readCategory(subCategoryDto.getCategoryId());
            if (optionalCategory.isEmpty()) {
                log.error("Category is invalid: {}", subCategoryDto.getCategoryId());
                return new ResponseEntity<>(new ApiResponse(false, "Category is invalid"), HttpStatus.NOT_FOUND);
            }
            // Check if subcategory is valid
            Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
            if (optionalSubCategory.isEmpty()) {
                log.error("Subcategory is invalid: {}", subcategoryId);
                return new ResponseEntity<>(new ApiResponse(false, "Subcategory is invalid"), HttpStatus.NOT_FOUND);
            }

            Category category = optionalCategory.get();

            // Update the subcategory
            subCategoryService.updateSubCategory(subcategoryId, subCategoryDto, category, user);
            log.info("Subcategory updated successfully: {}", subcategoryId);
            return new ResponseEntity<>(new ApiResponse(true, "Subcategory updated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating subcategory", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal server error"));
        }
    }
}
