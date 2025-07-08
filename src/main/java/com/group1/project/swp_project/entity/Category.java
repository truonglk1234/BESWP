package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category")
@Setter
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 200, columnDefinition = "NVARCHAR(200)")
    private String name;

    @Column(name = "type", length = 200, columnDefinition = "NVARCHAR(200)")
    private String type;
}
