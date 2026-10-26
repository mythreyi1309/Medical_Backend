package com.example.springboot.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot.entity.MedicationDoseLogEntity;
import com.example.springboot.entity.MedicationEntity;
import com.example.springboot.repository.MedicationDoseLogRepository;
import com.example.springboot.repository.MedicationRepository;
import com.example.springboot.service.MedicationDoseLogService;

@Service
public class MedicationDoseLogServiceImpl
        implements MedicationDoseLogService {

    private static final long MISSED_AFTER_MINUTES = 180;

    private final MedicationDoseLogRepository doseLogRepository;
    private final MedicationRepository medicationRepository;

    public MedicationDoseLogServiceImpl(
            MedicationDoseLogRepository doseLogRepository,
            MedicationRepository medicationRepository) {
        this.doseLogRepository = doseLogRepository;
        this.medicationRepository = medicationRepository;
    }

    @Override
    @Transactional
    public MedicationDoseLogEntity createDoseLog(
            Integer medicationId,
            LocalDate scheduledDate,
            LocalTime scheduledTime) {

        if (medicationId == null
                || scheduledDate == null
                || scheduledTime == null) {
            throw new IllegalArgumentException(
                    "Medication ID, date, and time are required");
        }

        MedicationEntity medication = medicationRepository
                .findById(medicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Medication not found"));

        if (scheduledDate.isBefore(medication.getStartDate())
                || scheduledDate.isAfter(medication.getEndDate())) {
            throw new IllegalArgumentException(
                    "Date is outside the medication treatment period");
        }

        List<LocalTime> allowedTimes = medication.getReminderTimes();

        if (allowedTimes == null || allowedTimes.isEmpty()) {
            if (medication.getReminderTime() == null) {
                throw new IllegalArgumentException(
                        "Medication has no configured reminder times");
            }
            allowedTimes = List.of(medication.getReminderTime());
        }

        if (!allowedTimes.contains(scheduledTime)) {
            throw new IllegalArgumentException(
                    "Scheduled time is not in this medication's reminder times");
        }

        return doseLogRepository
                .findByMedicationIdAndScheduledDateAndScheduledTime(
                        medicationId, scheduledDate, scheduledTime)
                .orElseGet(() -> {
                    MedicationDoseLogEntity log =
                            new MedicationDoseLogEntity();

                    log.setMedicationId(medication.getId());
                    log.setPatientId(medication.getPatientId());
                    log.setScheduledDate(scheduledDate);
                    log.setScheduledTime(scheduledTime);
                    log.setStatus("PENDING");
                    log.setUpdatedAt(LocalDateTime.now());

                    return doseLogRepository.save(log);
                });
    }

    @Override
    @Transactional
    public MedicationDoseLogEntity markAsTaken(Integer doseId) {
        MedicationDoseLogEntity log = findDoseLog(doseId);

        if (!"PENDING".equals(log.getStatus())) {
            throw new IllegalStateException(
                    "Only pending doses can be marked as taken");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = getScheduledDateTime(log)
                .plusMinutes(MISSED_AFTER_MINUTES);

        if (!now.isBefore(deadline)) {
            log.setStatus("MISSED");
            log.setUpdatedAt(now);
            doseLogRepository.saveAndFlush(log);

            // Return the saved MISSED record. Do not change it to TAKEN.
            return log;
        }

        log.setStatus("TAKEN");
        log.setTakenAt(now);
        log.setUpdatedAt(now);

        return doseLogRepository.save(log);
    }

    @Override
    @Transactional
    public MedicationDoseLogEntity markAsMissed(Integer doseId) {
        MedicationDoseLogEntity log = findDoseLog(doseId);

        if (!"PENDING".equals(log.getStatus())) {
            throw new IllegalStateException(
                    "Only pending doses can be marked as missed");
        }

        log.setStatus("MISSED");
        log.setUpdatedAt(LocalDateTime.now());

        return doseLogRepository.save(log);
    }

    /**
     * Marks all pending doses MISSED when their 30-minute
     * deadline has passed. Called by the scheduled task.
     */
    @Transactional
    public int markOverduePendingDosesAsMissed() {
        LocalDateTime now = LocalDateTime.now();
        List<MedicationDoseLogEntity> pendingLogs =
                doseLogRepository.findByStatus("PENDING");

        int updatedCount = 0;

        for (MedicationDoseLogEntity log : pendingLogs) {
            LocalDateTime deadline = getScheduledDateTime(log)
                    .plusMinutes(MISSED_AFTER_MINUTES);

            if (!now.isBefore(deadline)) {
                log.setStatus("MISSED");
                log.setUpdatedAt(now);
                doseLogRepository.save(log);
                updatedCount++;
            }
        }

        return updatedCount;
    }

    @Override
    public List<MedicationDoseLogEntity> getPatientDoseLogs(
            Integer patientId) {
        return doseLogRepository.findByPatientId(patientId);
    }

    @Override
    public List<MedicationDoseLogEntity> getPatientDoseLogsByDate(
            Integer patientId, LocalDate date) {
        return doseLogRepository
                .findByPatientIdAndScheduledDate(patientId, date);
    }

    private LocalDateTime getScheduledDateTime(
            MedicationDoseLogEntity log) {
        return LocalDateTime.of(
                log.getScheduledDate(),
                log.getScheduledTime());
    }

    private MedicationDoseLogEntity findDoseLog(Integer doseId) {
        return doseLogRepository.findById(doseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Dose log not found"));
    }
    
    @Transactional
    public int createScheduledDoseLogsUntilToday(Integer patientId) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int createdCount = 0;

        List<MedicationEntity> medications =
                medicationRepository.findByPatientId(patientId);

        for (MedicationEntity medication : medications) {

            LocalDate startDate = medication.getStartDate();
            LocalDate endDate = medication.getEndDate();

            if (startDate == null || endDate == null
                    || startDate.isAfter(today)
                    || startDate.isAfter(endDate)) {
                continue;
            }

            LocalDate lastDate =
                    endDate.isBefore(today) ? endDate : today;

            List<LocalTime> reminderTimes =
                    medication.getReminderTimes();

            if (reminderTimes == null || reminderTimes.isEmpty()) {
                if (medication.getReminderTime() == null) {
                    continue;
                }

                reminderTimes =
                        List.of(medication.getReminderTime());
            }

            for (LocalDate date = startDate;
                    !date.isAfter(lastDate);
                    date = date.plusDays(1)) {

                for (LocalTime time : reminderTimes) {

                    // Do not create a log for a future dose.
                    if (LocalDateTime.of(date, time).isAfter(now)) {
                        continue;
                    }

                    boolean exists = doseLogRepository
                            .findByMedicationIdAndScheduledDateAndScheduledTime(
                                    medication.getId(), date, time)
                            .isPresent();

                    if (!exists) {
                        MedicationDoseLogEntity log =
                                new MedicationDoseLogEntity();

                        log.setMedicationId(medication.getId());
                        log.setPatientId(medication.getPatientId());
                        log.setScheduledDate(date);
                        log.setScheduledTime(time);
                        log.setStatus("PENDING");
                        log.setUpdatedAt(now);

                        doseLogRepository.save(log);
                        createdCount++;
                    }
                }
            }
        }

        // Update overdue doses after generating any missing logs.
        createdCount += markOverduePendingDosesAsMissed();

        return createdCount;
    }
}
