
package com.example.springboot.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot.dto.AdherenceReportDTO;
import com.example.springboot.dto.MedicineAdherenceDTO;
import com.example.springboot.entity.MedicationDoseLogEntity;
import com.example.springboot.entity.MedicationEntity;
import com.example.springboot.entity.PatientEntity;
import com.example.springboot.repository.MedicationDoseLogRepository;
import com.example.springboot.repository.MedicationRepository;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.service.AdherenceReportService;

@Service
public class AdherenceReportServiceImpl
        implements AdherenceReportService {

    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;
    private final MedicationDoseLogRepository doseLogRepository;
    private final MedicationDoseLogServiceImpl doseLogService;

    public AdherenceReportServiceImpl(
            PatientRepository patientRepository,
            MedicationRepository medicationRepository,
            MedicationDoseLogRepository doseLogRepository,
            MedicationDoseLogServiceImpl doseLogService) {

        this.patientRepository = patientRepository;
        this.medicationRepository = medicationRepository;
        this.doseLogRepository = doseLogRepository;
        this.doseLogService = doseLogService;
    }

    @Override
    @Transactional
    public AdherenceReportDTO generateReport(Integer patientId) {

        if (patientId == null) {
            throw new IllegalArgumentException(
                    "Patient ID is required");
        }

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));

        List<MedicationEntity> medications =
                medicationRepository.findByPatientId(patientId);

        // Create missing dose logs and mark overdue doses as missed.
        doseLogService.createScheduledDoseLogsUntilToday(patientId);

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        AdherenceReportDTO report = new AdherenceReportDTO();
        report.setPatientId(patient.getId());
        report.setPatientName(patient.getName());
        report.setEndDate(today);

        List<MedicineAdherenceDTO> medicineReports =
                new ArrayList<>();

        long overallPrescribed = 0;
        long overallTaken = 0;
        long overallMissed = 0;

        LocalDate earliestStartDate = null;

        for (MedicationEntity medication : medications) {

            LocalDate start = medication.getStartDate();
            LocalDate end = medication.getEndDate();

            if (start == null || end == null || start.isAfter(end)) {
                continue;
            }

            if (earliestStartDate == null
                    || start.isBefore(earliestStartDate)) {
                earliestStartDate = start;
            }

            // Never include dates before the prescription starts
            // or after its end date or today's date.
            LocalDate effectiveEnd =
                    end.isBefore(today) ? end : today;

            long prescribed = 0;
            long taken = 0;
            long missed = 0;

            List<LocalTime> reminderTimes =
                    medication.getReminderTimes();

            if (reminderTimes == null || reminderTimes.isEmpty()) {
                if (medication.getReminderTime() != null) {
                    reminderTimes =
                            List.of(medication.getReminderTime());
                } else {
                    reminderTimes = List.of();
                }
            }

            if (!start.isAfter(effectiveEnd)
                    && !reminderTimes.isEmpty()) {

                List<MedicationDoseLogEntity> logs =
                        doseLogRepository
                                .findByPatientIdAndScheduledDateBetween(
                                        patientId, start, effectiveEnd);

                for (LocalDate date = start;
                        !date.isAfter(effectiveEnd);
                        date = date.plusDays(1)) {

                    for (LocalTime time : reminderTimes) {

                        // Exclude future reminder times, including
                        // later doses scheduled for today.
                        if (LocalDateTime.of(date, time).isAfter(now)) {
                            continue;
                        }

                        prescribed++;

                        for (MedicationDoseLogEntity log : logs) {

                            if (!medication.getId().equals(
                                    log.getMedicationId())) {
                                continue;
                            }

                            if (!date.equals(log.getScheduledDate())
                                    || !time.equals(
                                            log.getScheduledTime())) {
                                continue;
                            }

                            if ("TAKEN".equalsIgnoreCase(
                                    log.getStatus())) {
                                taken++;
                            } else if ("MISSED".equalsIgnoreCase(
                                    log.getStatus())) {
                                missed++;
                            }

                            break;
                        }
                    }
                }
            }

            MedicineAdherenceDTO medicineReport =
                    new MedicineAdherenceDTO();

            medicineReport.setMedicationId(medication.getId());
            medicineReport.setMedicineName(
                    medication.getMedicineName());
            medicineReport.setDosage(medication.getDosage());
            medicineReport.setTotalPrescribed(prescribed);
            medicineReport.setTotalTaken(taken);
            medicineReport.setTotalMissed(missed);

            medicineReports.add(medicineReport);

            overallPrescribed += prescribed;
            overallTaken += taken;
            overallMissed += missed;
        }

        report.setStartDate(earliestStartDate);
        report.setTotalPrescribed(overallPrescribed);
        report.setTotalTaken(overallTaken);
        report.setTotalMissed(overallMissed);
        report.setMedicines(medicineReports);

        return report;
    }
}
