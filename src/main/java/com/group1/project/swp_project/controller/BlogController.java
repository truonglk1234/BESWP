package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.BlogDetail;
import com.group1.project.swp_project.dto.BlogSummary;
import com.group1.project.swp_project.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "4. Blog (Công khai)", description = "Các API để xem bài viết")
@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    @Autowired
    private BlogService blogService;

    // Api cho mọi người xem danh sách bài viết
    @Operation(summary = "Lấy danh sách các bài viết đã đăng", description = "Trả về danh sách tóm tắt của tất cả các bài viết đã được duyệt và đăng tải. Mọi người đều có thể truy cập.")
    @GetMapping
    public ResponseEntity<List<BlogSummary>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getPublishedBlogSummaries());
    }

    @Operation(summary = "Lấy một bài viết theo ID", description = "Trả về nội dung đầy đủ của một bài viết đã được đăng tải. Mọi người đều có thể truy cập.")
    @GetMapping("/{id}")
    public ResponseEntity<BlogDetail> getBlogById(@PathVariable int id) {
        return ResponseEntity.ok(blogService.getBlogById(id));
    }



}
