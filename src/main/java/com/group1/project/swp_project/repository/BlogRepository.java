package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer> {
    List<Blog> findByStatus(String status);
    List<Blog> findByTopicId(Integer topicId);
    List<Blog> findByStatusAndTopicId(String status, Integer topicId);

    List<Blog> findByCreatedByEmail(String createdByEmail);
}
