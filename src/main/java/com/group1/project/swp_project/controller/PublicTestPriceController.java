package com.group1.project.swp_project.controller;



import com.group1.project.swp_project.dto.ServicePriceDTO;
import com.group1.project.swp_project.service.ServicePriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bảng giá công khai xét nghiệm")
@RestController
@RequestMapping("/api/public/prices/test")
public class PublicTestPriceController {

    @Autowired
    private ServicePriceService servicePriceService;

    // GET /api/public/prices/test
    @GetMapping
    public List<ServicePriceDTO> getPublicTestPrices() {
        return servicePriceService.getActivePricesByType("test");
    }
}

