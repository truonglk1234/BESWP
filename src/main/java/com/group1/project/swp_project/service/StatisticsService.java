package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.MonthlyTestStat;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private ExaminationBookingRepository bookingRepo;

    public List<MonthlyTestStat> getMonthlyTestStats() {
        List<Object[]> rawData = bookingRepo.countMonthlyExaminations();

        return rawData.stream()
                .map(row -> {
                    int year = (int) row[0];
                    int month = (int) row[1];
                    long count = ((Number) row[2]).longValue();
                    return new MonthlyTestStat("Tháng " + month + "/" + year, count);
                })
                .collect(Collectors.toList());
    }
}
