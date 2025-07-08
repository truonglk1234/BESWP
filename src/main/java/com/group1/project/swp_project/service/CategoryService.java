package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.CategoryDto;
import com.group1.project.swp_project.entity.Category;
import com.group1.project.swp_project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category create(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setType(dto.getType());
        return categoryRepository.save(category);
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findByType(String type) {
        return categoryRepository.findByType(type);
    }
}
