package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer> {
    List<Blog> findByStatus(String status);
    List<Blog> findByTopicId(Integer topicId);
    List<Blog> findByStatusAndTopicId(String status, Integer topicId);
    @Query("SELECT b FROM Blog b ORDER BY CASE WHEN b.status = 'Pending' THEN 0 ELSE 1 END, b.createdAt DESC")
    List<Blog> findAllOrderByPendingFirst();
    @Query("SELECT b FROM Blog b WHERE b.createdBy.email = :email ORDER BY CASE WHEN b.status = 'Pending' THEN 0 ELSE 1 END, b.createdAt DESC")
    List<Blog> findByCreatedByEmailOrderByPendingFirst(@Param("email") String email);
}
