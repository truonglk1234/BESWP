package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.CategoryDto;
import com.group1.project.swp_project.dto.ServicePriceDTO;
import com.group1.project.swp_project.entity.Category;
import com.group1.project.swp_project.entity.ServicePrice;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.CategoryRepository;
import com.group1.project.swp_project.repository.ServicePriceRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicePriceService {

    @Autowired
    private ServicePriceRepository repo;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo mới
    @Transactional
    public ServicePriceDTO create(ServicePriceDTO dto, String creatorEmail) {
        Users creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Người dùng không hợp lệ."));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại."));

        ServicePrice price = ServicePrice.builder()
                .name(dto.getName())
                .price(dto.getPrice()) // ép int sang double
                .status(dto.getStatus())
                .description(dto.getDescription())
                .createdAt(LocalDate.now())
                .createdBy(creator)
                .category(category)
                .build();

        repo.save(price);
        return ServicePriceDTO.from(price);
    }

    // Sửa
    @Transactional
    public ServicePriceDTO update(int id, ServicePriceDTO dto, String userEmail, boolean isAdmin) {
        ServicePrice price = repo.findById(id).orElseThrow(() -> new RuntimeException("Bảng giá không tồn tại."));
        if (dto.getName() != null) price.setName(dto.getName());
        if (dto.getPrice() != 0) price.setPrice(dto.getPrice());
        if (dto.getStatus() != null) price.setStatus(dto.getStatus());
        if (dto.getCategoryId() != 0) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
            price.setCategory(category);
        }
        if (dto.getDescription() != null) price.setDescription(dto.getDescription());
        repo.save(price);
        return ServicePriceDTO.from(price);
    }

    // Xóa
    @Transactional
    public void delete(int id, String userEmail, boolean isAdmin) {
        ServicePrice price = repo.findById(id).orElseThrow(() -> new RuntimeException("Bảng giá không tồn tại."));
        repo.deleteById(id);
    }

    // Lấy danh sách theo loại ("advice"/"test")
    public List<ServicePriceDTO> getPricesByType(String type) {
        return repo.findByCategory_Type(type)
                .stream()
                .map(ServicePriceDTO::from)
                .collect(Collectors.toList());
    }

    public ServicePriceDTO getById(int id) {
        ServicePrice price = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bảng giá không tồn tại."));
        return ServicePriceDTO.from(price);
    }


    @Transactional
    public ServicePriceDTO updateStatus(int id, String status) {
        ServicePrice price = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bảng giá không tồn tại."));
        price.setStatus(status);
        repo.save(price);
        return ServicePriceDTO.from(price);
    }
    public List<ServicePriceDTO> getActivePricesByType(String type) {
        return repo.findByCategory_TypeAndStatus(type, "Active")
                .stream()
                .map(ServicePriceDTO::from)
                .collect(Collectors.toList());
    }
}