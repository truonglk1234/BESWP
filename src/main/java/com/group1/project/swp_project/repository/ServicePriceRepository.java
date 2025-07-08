package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ServicePriceRepository extends JpaRepository<ServicePrice, Integer>
{
    List<ServicePrice> findByCategory_Type(String type);
    List<ServicePrice> findByCategory_Id(int categoryId);

    @Modifying
    @Transactional
    @Query("UPDATE ServicePrice s SET s.status = :status WHERE s.id = :id")
    void updateStatusById(int id, String status);

    List<ServicePrice> findByCategory_TypeAndStatus(String type, String status);
}
