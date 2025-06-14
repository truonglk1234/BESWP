package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.BlogDetail;
import com.group1.project.swp_project.dto.BlogSummary;
import com.group1.project.swp_project.dto.CreateBlogRequest;
import com.group1.project.swp_project.entity.Blog;
import com.group1.project.swp_project.entity.User;
import com.group1.project.swp_project.repository.BlogRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    //Lấy tất cả bài viết dạng tóm tắt

    public List<BlogSummary> getAllBlogSummaries() {
        return blogRepository.findAll().stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    //Lấy chi tiết bài viét

    public BlogDetail getBlogById(int id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Bài viết" +
                "không tồn tại."));
        return  convertToDetailDto(blog);
    }


    //Tạo bài viết mới
    @Transactional
    public Blog createBlog(CreateBlogRequest request, String creatorEmail){
        User creator = userRepository.findByEmail(creatorEmail).orElseThrow(()
                -> new RuntimeException("<Người dùng không hợp lệ."));


        Blog newBlog = Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .createdBy(creator)
                .build();

        return blogRepository.save(newBlog);
    }

    //Xóa bài viết
    public void deleteBlog(int id){
        if(!blogRepository.existsById(id)){
            throw  new RuntimeException("Bài viết không tồn tại!");

        }
        blogRepository.deleteById(id);
    }


    //Cập nhật vài viết
    public Blog updateBlog(int id, CreateBlogRequest request){
        Blog existingBlog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        existingBlog.setTitle(request.getTitle());
        existingBlog.setContent(request.getContent());
        existingBlog.setImageUrl(request.getImageUrl());
        return blogRepository.save(existingBlog);
    }


    private BlogSummary convertToSummaryDto(Blog blog){
        String authorName = (blog.getCreatedBy() != null && blog.getCreatedBy().getProfile() != null)
                ? blog.getCreatedBy().getProfile().getFullName()
                : "N/A";

        return BlogSummary.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .imageUrl(blog.getImageUrl())
                .createdAt(blog.getCreatedAt())
                .authorName(authorName)
                .build();
    }
    private BlogDetail convertToDetailDto(Blog blog) {
        // Lấy tên tác giả, xử lý trường hợp tác giả có thể null
        String authorName = (blog.getCreatedBy() != null && blog.getCreatedBy().getProfile() != null)
                ? blog.getCreatedBy().getProfile().getFullName()
                : "N/A";

        return BlogDetail.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .imageUrl(blog.getImageUrl())
                .createdAt(blog.getCreatedAt())
                .authorName(authorName)
                .build();
    }

    // API công khai chỉ lấy các bài đã được duyệt
    public List<BlogSummary> getPublishedBlogSummaries() {
        // 1. Gọi repository để lấy danh sách Blog
        List<Blog> publishedBlogs = blogRepository.findByStatus("Published");

        // 2. Chuyển danh sách đó thành stream và xử lý
        return publishedBlogs.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }


    // Duyệt một bài viết
    @Transactional
    public Blog approveBlog(int id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        blog.setStatus("Published");
        return blogRepository.save(blog);
    }

    // Từ chối một bài viết
    @Transactional
    public Blog rejectBlog(int id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        blog.setStatus("Rejected");
        return blogRepository.save(blog);
    }

    public List<BlogSummary> getPendingBlogs() {
        // 1. Gọi repository để tìm tất cả các blog có status là "Pending"
        List<Blog> pendingBlogs = blogRepository.findByStatus("Pending");

        // 2. Chuyển danh sách Entity sang danh sách DTO và trả về
        return pendingBlogs.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }
}
