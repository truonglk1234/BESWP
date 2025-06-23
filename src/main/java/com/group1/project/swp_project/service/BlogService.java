package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.BlogDetail;
import com.group1.project.swp_project.dto.BlogSummary;
import com.group1.project.swp_project.dto.req.CreateBlogRequest;
import com.group1.project.swp_project.entity.Blog;
import com.group1.project.swp_project.entity.Topic;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.BlogRepository;
import com.group1.project.swp_project.repository.TopicRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    // Lấy tất cả bài viết dạng tóm tắt

    public List<BlogSummary> getAllBlogSummaries() {
        return blogRepository.findAll().stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết bài viét

    public BlogDetail getBlogById(int id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Bài viết" +
                "không tồn tại."));
        return convertToDetailDto(blog);
    }

    // Tạo bài viết mới
    @Transactional
    public Blog createBlog(CreateBlogRequest request, String creatorEmail) {
        Users creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Người dùng không hợp lệ."));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Chủ đề không tồn tại."));

        Blog newBlog = Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .createdBy(creator)
                .topic(topic)
                .status("Pending")
                .build();

        return blogRepository.save(newBlog);
    }

    // Xóa bài viết
    public void deleteBlog(int id) {
        if (!blogRepository.existsById(id)) {
            throw new RuntimeException("Bài viết không tồn tại!");

        }
        blogRepository.deleteById(id);
    }

    // Cập nhật vài viết
    public Blog updateBlog(int id, CreateBlogRequest request) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        existingBlog.setTitle(request.getTitle());
        existingBlog.setContent(request.getContent());
        return blogRepository.save(existingBlog);
    }

    private BlogSummary convertToSummaryDto(Blog blog) {
        String authorName = (blog.getCreatedBy() != null && blog.getCreatedBy().getProfile() != null)
                ? blog.getCreatedBy().getProfile().getFullName()
                : "N/A";

        return BlogSummary.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .createdAt(blog.getCreatedAt())
                .authorName(authorName)
                .topicName(blog.getTopic() != null ? blog.getTopic().getName() : null)
                .status(blog.getStatus())
                .build();
    }


    public BlogDetail convertToDetailDto(Blog blog) {
        String authorName = (blog.getCreatedBy() != null && blog.getCreatedBy().getProfile() != null)
                ? blog.getCreatedBy().getProfile().getFullName()
                : "N/A";

        return BlogDetail.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .createdAt(blog.getCreatedAt())
                .authorName(authorName)
                .topicName(blog.getTopic() != null ? blog.getTopic().getName() : null)
                .status(blog.getStatus())
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


    public List<BlogSummary>getAllBlogs(){
        return blogRepository.findAll().stream().
                map(this::convertToSummaryDto).
                collect(Collectors.toList());
    }

    public BlogDetail getBlogByIdDetail(int id) {
        return blogRepository.findById(id)
                .map(this::convertToDetailDto)
                .orElse(null);
    }

    public List<BlogSummary> filterBlogs(String status, Integer topicId) {
        List<Blog> result;

        if (status != null && topicId != null) {
            result = blogRepository.findByStatusAndTopicId(status, topicId);
        } else if (status != null) {
            result = blogRepository.findByStatus(status);
        } else if (topicId != null) {
            result = blogRepository.findByTopicId(topicId);
        } else {
            result = blogRepository.findAll();
        }

        return result.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }




    public List<BlogSummary> getBlogsByStaffEmail(String email) {
        List<Blog> blogs = blogRepository.findByCreatedByEmail((email));
        return blogs.stream().map(this::convertToSummaryDto).collect(Collectors.toList());
    }



    @Transactional
    public Blog updateBlogOfStaff(int blogId, CreateBlogRequest request, String staffEmail) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        // Kiểm tra quyền sở hữu: chỉ người tạo mới được sửa
        if (!blog.getCreatedBy().getEmail().equals(staffEmail)) {
            throw new RuntimeException("Bạn không có quyền sửa bài viết này.");
        }
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        // Cập nhật chủ đề nếu có
        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Chủ đề không tồn tại"));
            blog.setTopic(topic);
        }
        // Đặt lại trạng thái là "Pending" sau khi sửa (nếu bạn muốn duyệt lại)
        blog.setStatus("Pending");
        return blogRepository.save(blog);
    }
}
