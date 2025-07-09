package com.group1.project.swp_project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group1.project.swp_project.dto.req.CreateTestBookingRequest;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.entity.TestBooking;
import com.group1.project.swp_project.entity.TestPackage;
import com.group1.project.swp_project.repository.TestBookingRepository;
import com.group1.project.swp_project.repository.TestPackageRepository;
import com.group1.project.swp_project.repository.UserRepository;

@Service
public class TestBookingService {

    private final TestBookingRepository bookingRepo;
    private final TestPackageRepository testPackageRepo;
    private final UserRepository userRepository; // Dùng Users thay cho Consultant

    public TestBookingService(TestBookingRepository bookingRepo,
            TestPackageRepository testPackageRepo,
            UserRepository userRepository) {
        this.bookingRepo = bookingRepo;
        this.testPackageRepo = testPackageRepo;
        this.userRepository = userRepository;
    }

    public TestBooking createBooking(Users user, CreateTestBookingRequest request) {
        TestPackage testPackage = testPackageRepo.findById(request.getTestPackageId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói xét nghiệm"));

        TestBooking booking = new TestBooking();
        booking.setUser(user);
        booking.setTestPackage(testPackage);
        booking.setScheduledDate(request.getScheduledDate());
        booking.setNote(request.getNote());
        booking.setStatus("pending");

        if (request.getConsultantId() != null) {
            Users consultant = userRepository.findById(request.getConsultantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tư vấn viên"));

            // Nếu muốn chắc chắn người này là CONSULTANT:
            if (!"CONSULTANT".equalsIgnoreCase(consultant.getRole().getRoleName())) {
                throw new RuntimeException("Người được chọn không phải tư vấn viên!");
            }

            booking.setConsultant(consultant);
        }

        return bookingRepo.save(booking);
    }

    public List<TestBooking> getBookingsByUser(Users user) {
        return bookingRepo.findByUser(user);
    }
}
