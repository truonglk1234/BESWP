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
@Table(name = "User_Status")

public class UserStatus {

    @Id
    @Column(name = "status_id")
    private int id;

    //unique = true: Đảm bảo rằng giá trị trong cột là duy nhất
    //nullable = false: Cột này không được để trống trong cơ sở dữ liệu
    @Column(name = "status_name", unique = true, nullable = false, length = 50)
    private String statusName;

    @Lob
    @Column(name = "description")
    private String description;
}
