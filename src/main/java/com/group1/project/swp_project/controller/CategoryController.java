package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.CategoryDto;
import com.group1.project.swp_project.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.group1.project.swp_project.entity.Category;

import java.util.List;

@Tag(name = "Thêm danh mục cho việc xét nghiệm và tư vấn")
@RestController
@RequestMapping("api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Category>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(categoryService.findByType(type));
    }


}
