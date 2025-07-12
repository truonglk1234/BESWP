package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ExaminationTypeCountDTO;
import com.group1.project.swp_project.dto.MonthlyRevenueDTO;
import com.group1.project.swp_project.dto.MonthlyTestStat;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private ExaminationBookingRepository bookingRepo;
    @Autowired
    private ExaminationBookingRepository examinationBookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;

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


    public List<ExaminationTypeCountDTO> getExaminationTypeCount() {
        List<Object[]> results = examinationBookingRepository.countExaminationsByType();
        List<ExaminationTypeCountDTO> dtoList = new ArrayList<>();

        for (Object[] row : results) {
            String serviceName = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            dtoList.add(new ExaminationTypeCountDTO(serviceName, count));
        }

        return dtoList;
    }


    public List<MonthlyRevenueDTO> getMonthlyRevenueStats() {
        List<Object[]> results = paymentRepository.getMonthlyRevenue();
        return results.stream()
                .map(row -> new MonthlyRevenueDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
}
