package com.example.springboot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.springboot.serviceimpl.MedicationDoseLogServiceImpl;

@Component
public class MedicationDoseScheduler {

    private final MedicationDoseLogServiceImpl doseLogService;

    public MedicationDoseScheduler(
            MedicationDoseLogServiceImpl doseLogService) {
        this.doseLogService = doseLogService;
    }

    // Check for overdue doses every minute.
    @Scheduled(fixedRate = 60000)
    public void checkOverdueDoses() {
        int updatedCount =
                doseLogService.markOverduePendingDosesAsMissed();

        if (updatedCount > 0) {
            System.out.println(
                    updatedCount + " overdue dose(s) marked as MISSED.");
        }
    }
}

