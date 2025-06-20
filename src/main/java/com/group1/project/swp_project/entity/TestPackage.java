package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_name", length = 100)
    private String packageName;

    @Column(length = 255)
    private String description;

    private Double price;
}
