package com.group1.project.swp_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Users;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUser(Users user); // đúng vì Profile có trường user
}