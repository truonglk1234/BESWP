package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.BlogSummary;
import com.group1.project.swp_project.dto.BlogSummary;
import com.group1.project.swp_project.dto.CreateBlogRequest;
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


@Tag(name = "5. Quản lý Blog", description = "Các API để tạo và quản lý bài viết")
@RestController
@RequestMapping("/api/management/blogs")
@SecurityRequirement(name = "Bearer Authentication")
public class BlogManagementController {

    @Autowired
    private BlogService blogService;

    // API tạo bài viết mới:  vai trò đều được tạo
    @Operation(
            summary = "Tạo một bài viết mới",
            description = "Tạo một bài viết mới với trạng thái 'Chờ duyệt'. Yêu cầu vai trò STAFF."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<Blog> createBlog(@RequestBody @Valid CreateBlogRequest request, Authentication authentication) {
        String creatorEmail = authentication.getName();
        Blog createdBlog = blogService.createBlog(request, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
    }

    // API cập nhật bài viết: Cả 3 vai trò đều được sửa
    @Operation(
            summary = "Cập nhật một bài viết đã có",
            description = "Cập nhật tiêu đề, nội dung, hoặc hình ảnh của một bài viết. Yêu cầu vai trò ADMIN, MANAGER, hoặc STAFF."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")

    public ResponseEntity<Blog> updateBlog(@PathVariable int id, @RequestBody @Valid CreateBlogRequest request) {
        // Cần thêm logic kiểm tra xem người sửa có phải là người tạo bài không, hoặc là admin/manager
        return ResponseEntity.ok(blogService.updateBlog(id, request));
    }

    // API xóa bài viết: Cả 3 vai trò đều được xóa
    @Operation(
            summary = "Xóa một bài viết",
            description = "Xóa vĩnh viễn một bài viết. Yêu cầu vai trò ADMIN, MANAGER, hoặc STAFF."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Void> deleteBlog(@PathVariable int id) {
        // Tương tự, cần kiểm tra quyền sở hữu trước khi xóa
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    // API lấy danh sách các bài viết đang chờ duyệt: Chỉ Manager và Admin
    @Operation(
            summary = "Lấy danh sách bài viết chờ duyệt",
            description = "Trả về danh sách các bài viết có trạng thái 'Chờ duyệt'. Yêu cầu vai trò ADMIN hoặc MANAGER."
    )
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<BlogSummary>> getPendingBlogs() {
        return ResponseEntity.ok(blogService.getPendingBlogs());
    }

    // API để duyệt một bài viết: Chỉ Manager
    @Operation(
            summary = "Phê duyệt một bài viết",
            description = "Thay đổi trạng thái của bài viết từ 'Chờ duyệt' sang 'Đã đăng'. Yêu cầu vai trò MANAGER."
    )
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole( 'MANAGER')")
    public ResponseEntity<Blog> approveBlog(@PathVariable int id) {
        return ResponseEntity.ok(blogService.approveBlog(id));
    }

    // API để từ chối một bài viết: Chỉ Manager
    @Operation(
            summary = "Từ chối một bài viết",
            description = "Thay đổi trạng thái của bài viết từ 'Chờ duyệt' sang 'Bị từ chối'. Yêu cầu vai trò ADMIN hoặc MANAGER."
    )
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<Blog> rejectBlog(@PathVariable int id) {
        return ResponseEntity.ok(blogService.rejectBlog(id));
    }
}