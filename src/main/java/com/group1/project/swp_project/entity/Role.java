package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Role")


public class Role {

    @Id
    @Column(name="role_id")
    private int id;

    @Column(name ="role_name", unique=true, nullable=false, length=50)
    private String roleName;

    @Lob
    @Column(name = "description")
    private String description;
}
