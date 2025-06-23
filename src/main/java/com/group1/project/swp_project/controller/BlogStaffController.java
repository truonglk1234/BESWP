package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.BlogDetail;
import com.group1.project.swp_project.dto.BlogSummary;
import com.group1.project.swp_project.dto.req.CreateBlogRequest;
import com.group1.project.swp_project.entity.Blog;
import com.group1.project.swp_project.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "6. Tạo Blog cho Saff", description = "Các API để tạo và quản lý bài viết")
@RestController
@RequestMapping("/api/auth/staff/blogs")
@SecurityRequirement(name = "Bearer Authentication")
public class BlogStaffController {

    @Autowired
    private BlogService blogService;

    @Operation(
            summary = "Xóa một bài viết",
            description = "Xóa vĩnh viễn một bài viết. Yêu cầu vai trò ADMIN, MANAGER, hoặc STAFF."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Staff')")
    public ResponseEntity<Void> deleteBlog(@PathVariable int id) {
        // Tương tự, cần kiểm tra quyền sở hữu trước khi xóa
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    // API tạo bài viết mới:  vai trò đều được tạo


    @Operation(
            summary = "Tạo một bài viết mới",
            description = "Tạo một bài viết mới với trạng thái 'Chờ duyệt'. Yêu cầu vai trò STAFF."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('Staff')")
    public ResponseEntity<Blog> createBlog(@RequestBody @Valid CreateBlogRequest request, Authentication authentication) {
        String creatorEmail = authentication.getName();
        Blog createdBlog = blogService.createBlog(request, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
    }

    @Operation(
            summary = "Xem chi tiết một bài viết bất kỳ",
            description = "Xem chi tiết bài viết theo ID, không giới hạn trạng thái. Yêu cầu vai trò ADMIN, MANAGER hoặc STAFF."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Staff')")
    public ResponseEntity<BlogDetail> getBlogDetail(@PathVariable int id) {
        return ResponseEntity.ok(blogService.getBlogById(id));
    }

    @Operation(
            summary = "Danh sách blog của staff",
            description = "các bài viết của staff sau khi viết sẽ được hiện ra "
    )
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('Staff')")
    public ResponseEntity<List<BlogSummary>> getMyBlogs(Authentication authentication) {
        String email = authentication.getName(); // lấy email từ token
        List<BlogSummary> blogList = blogService.getBlogsByStaffEmail(email);
        return ResponseEntity.ok(blogList);
    }

    @Operation(
            summary = "Sửa bài viết",
            description = "Staff đc quyền sửa bài viết khi đã tạo bài"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Staff')")
    public ResponseEntity<BlogDetail> updateOwnBlog(
            @PathVariable int id,
            @RequestBody @Valid CreateBlogRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Blog updated = blogService.updateBlogOfStaff(id, request, email);
        BlogDetail detail = blogService.convertToDetailDto(updated);
        return ResponseEntity.ok(detail);
    }
}
