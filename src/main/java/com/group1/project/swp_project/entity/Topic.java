package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    private String name;
}
