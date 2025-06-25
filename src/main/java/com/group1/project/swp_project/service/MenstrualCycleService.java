package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.req.MenstrualCycleRequest;
import com.group1.project.swp_project.entity.MenstrualCycle;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.MenstrualCycleRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MenstrualCycleService {
    @Autowired
    private MenstrualCycleRepository repo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private NotificationService notificationService;

    public MenstrualCycle getCurrent(Integer userId) {
        return repo.findByUserIdOrderByCycleStartDateDesc(userId)
                .stream().findFirst().orElse(null);
    }

    public List<MenstrualCycle> getHistory(Integer userId) {
        return repo.findByUserIdOrderByCycleStartDateDesc(userId);
    }

    public MenstrualCycle saveOrUpdate(MenstrualCycleRequest req, Integer userId) {
        Users user = userRepo.findById(userId).orElseThrow();

        // Chuyển String sang LocalDate/LocalTime
        LocalDate startDate = LocalDate.parse(req.getCycleStartDate());
        LocalTime pillTime = req.getPillTime() != null
                ? LocalTime.parse(req.getPillTime().length() == 5 ? req.getPillTime() + ":00" : req.getPillTime())
                : LocalTime.of(20, 0);

        // Tìm bản ghi theo user + ngày
        MenstrualCycle mc = repo.findByUserIdAndCycleStartDate(userId, startDate)
                .orElse(new MenstrualCycle());

        mc.setUser(user);
        mc.setCycleStartDate(startDate);
        mc.setCycleLength(req.getCycleLength());
        mc.setPeriodLength(req.getPeriodLength());
        mc.setRemindOvulation(Boolean.TRUE.equals(req.getRemindOvulation()));
        mc.setRemindHighFertile(Boolean.TRUE.equals(req.getRemindHighFertile()));
        mc.setRemindPill(Boolean.TRUE.equals(req.getRemindPill()));
        mc.setPillTime(pillTime);
        mc.setNote(req.getNote());
        mc.setCreatedAt(LocalDateTime.now());

        repo.save(mc);


        // ===== Tạo thông báo sau khi lưu chu kỳ mới =====

        if (mc.getRemindOvulation() != null && mc.getRemindOvulation()) {
            String ovulationDay = mc.getCycleStartDate()
                    .plusDays(mc.getCycleLength() - 14)
                    .toString(); // yyyy-MM-dd

            notificationService.createNotification(
                    userId,
                    "health",
                    "Bạn đã khai báo chu kỳ mới. Ngày rụng trứng dự kiến: " + ovulationDay,
                    "/profile/health"
            );
        }

        if (mc.getRemindHighFertile() != null && mc.getRemindHighFertile()) {
            notificationService.createNotification(
                    userId,
                    "health",
                    "Bạn đã bật thông báo khả năng mang thai cao. Hệ thống sẽ nhắc bạn khi vào giai đoạn dễ thụ thai.",
                    "/profile/health"
            );
        }

        if (mc.getRemindPill() != null && mc.getRemindPill()) {
            notificationService.createNotification(
                    userId,
                    "health",
                    "Bạn đã bật nhắc uống thuốc tránh thai mỗi ngày lúc " + mc.getPillTime(),
                    "/profile/health"
            );
        }

        return mc;
    }
}
