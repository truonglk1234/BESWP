package com.group1.project.swp_project.dto;

import com.group1.project.swp_project.entity.ServicePrice;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class ServicePriceDTO {
    private int id;
    private String name;
    private int price;
    private String status;
    private int categoryId;
    private String description;


    public static ServicePriceDTO from(ServicePrice entity) {
        return ServicePriceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : 0)
                .description(entity.getDescription())
                .build();
    }
}

