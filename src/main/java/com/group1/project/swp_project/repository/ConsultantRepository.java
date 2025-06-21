package com.group1.project.swp_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group1.project.swp_project.entity.Consultant;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Long> {

}
