package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ServicePriceDTO;
import com.group1.project.swp_project.service.ServicePriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Giá cho tư vấn và xét nghiệm")
@RestController
@RequestMapping("/api/prices")
public class ServicePriceController {

    @Autowired
    private ServicePriceService servicePriceService;


    @GetMapping
    public List<ServicePriceDTO> getPrices(@RequestParam String type) {
        return servicePriceService.getPricesByType(type);
    }


    @GetMapping("/detail/{id}")
    public ServicePriceDTO getPrice(@PathVariable int id) {
        return servicePriceService.getById(id);
    }


    @PostMapping
    public ResponseEntity<ServicePriceDTO> create(@RequestBody ServicePriceDTO dto, Principal principal) {
        String email = principal.getName();
        ServicePriceDTO result = servicePriceService.create(dto, email);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/{id}")
    public ServicePriceDTO update(@PathVariable int id, @RequestBody ServicePriceDTO dto, Principal principal) {
        String email = principal.getName();
        boolean isAdmin = false; // bạn tự lấy quyền admin nếu có (từ SecurityContext hoặc JWT claims)
        return servicePriceService.update(id, dto, email, isAdmin);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id, Principal principal) {
        String email = principal.getName();
        boolean isAdmin = false; // bạn tự lấy quyền admin nếu có
        servicePriceService.delete(id, email, isAdmin);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServicePriceDTO> updateStatus(
            @PathVariable int id,
            @RequestParam String status
    ) {
        ServicePriceDTO dto = servicePriceService.updateStatus(id, status);
        return ResponseEntity.ok(dto);
    }
}
