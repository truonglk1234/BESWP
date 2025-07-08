package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ServicePriceDTO;
import com.group1.project.swp_project.service.ServicePriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bảng giá công khai tư vấn")
@RestController
@RequestMapping("/api/public/prices/advice")
public class PublicAdvicePriceController {

    @Autowired
    private ServicePriceService servicePriceService;

    // GET /api/public/prices/advice
    @GetMapping
    public List<ServicePriceDTO> getPublicAdvicePrices() {
        return servicePriceService.getActivePricesByType("advice");
    }
}