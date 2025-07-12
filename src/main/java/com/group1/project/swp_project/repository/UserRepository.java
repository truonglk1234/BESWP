package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Role;
import com.group1.project.swp_project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByPhone(String phone);

    Optional<Users> findById(Long id);

    boolean existsByPhone(String phone);

    Boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u JOIN u.role r WHERE r.roleName = :roleName")
    List<Users> findAllByRoleName(@Param("roleName") String roleName);

    List<Users> findAllByRole_IdOrderByIdAsc(int roleId);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.role.id = :roleId")
    long countByRoleId(@Param("roleId") int roleId);

    @Query("SELECT COUNT(u) FROM Users u " +
            "WHERE FUNCTION('MONTH', u.createdAt) = :month " +
            "AND FUNCTION('YEAR', u.createdAt) = :year")
    long countCreatedInMonth(@Param("month") int month, @Param("year") int year);

    @Query(value = """
                SELECT 
                    FORMAT(created_at, 'yyyy-MM') AS month,
                    COUNT(*) AS count
                FROM users
                WHERE role_id = 2
                GROUP BY FORMAT(created_at, 'yyyy-MM')
                ORDER BY FORMAT(created_at, 'yyyy-MM')
            """, nativeQuery = true)
    List<Object[]> getMonthlyUserStats();
}
