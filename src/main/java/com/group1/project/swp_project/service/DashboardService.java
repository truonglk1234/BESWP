package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.DashboardSummary;
import com.group1.project.swp_project.repository.BlogRepository;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private ExaminationBookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public DashboardSummary getDashboardSummary() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        DashboardSummary dto = new DashboardSummary();

        // Users
        dto.setTotalUsers(userRepository.count());
        dto.setCustomers(userRepository.countByRoleId(2));
        dto.setConsultants(userRepository.countByRoleId(3));
        dto.setStaff(userRepository.countByRoleId(4));
        dto.setNewAccountsThisMonth(userRepository.countCreatedInMonth(month, year));

        // Blogs
        dto.setPublishedBlogsThisMonth(blogRepository.countPublishedBlogsInMonth(month, year));

        // Examination
        dto.setExaminationCountThisMonth(bookingRepository.countExaminationsInMonth(month, year));

        // Income
        Long incomeLong = (long) paymentRepository.getTotalIncomeInMonth(month, year);
        BigDecimal income = incomeLong != null ? BigDecimal.valueOf(incomeLong) : BigDecimal.ZERO;
        dto.setTotalIncomeThisMonth(income);

        return dto;
    }
}
