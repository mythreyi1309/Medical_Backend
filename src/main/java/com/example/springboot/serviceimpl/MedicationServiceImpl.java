package com.example.springboot.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springboot.entity.MedicationEntity;
import com.example.springboot.repository.MedicationRepository;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.service.MedicationService;

@Service
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;

    public MedicationServiceImpl(
            MedicationRepository medicationRepository,
            PatientRepository patientRepository) {

        this.medicationRepository = medicationRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public MedicationEntity addMedication(MedicationEntity medication) {

        // 1. Validate patient ID
        if (medication.getPatientId() == null) {
            throw new IllegalArgumentException(
                    "Patient ID is required");
        }

        // 2. Verify that the patient exists
        patientRepository.findById(medication.getPatientId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Patient not found"));

        // 3. Validate medicine name
        if (medication.getMedicineName() == null
                || medication.getMedicineName().isBlank()) {
            throw new IllegalArgumentException(
                    "Medicine name is required");
        }

        // 4. Validate dosage
        if (medication.getDosage() == null
                || medication.getDosage().isBlank()) {
            throw new IllegalArgumentException(
                    "Dosage is required");
        }

        // 5. Validate frequency
        if (medication.getFrequency() == null
                || medication.getFrequency().isBlank()) {
            throw new IllegalArgumentException(
                    "Frequency is required");
        }

        // 6. Validate treatment dates
        if (medication.getStartDate() == null
                || medication.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Treatment dates are required");
        }

        if (medication.getEndDate()
                .isBefore(medication.getStartDate())) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        // 7. Support both reminderTime and reminderTimes
        List<LocalTime> reminderTimes =
                medication.getReminderTimes();

        if (reminderTimes == null || reminderTimes.isEmpty()) {

            if (medication.getReminderTime() == null) {
                throw new IllegalArgumentException(
                        "At least one reminder time is required");
            }

            reminderTimes =
                    List.of(medication.getReminderTime());
        }

        // 8. Remove nulls, duplicate times, and sort
        List<LocalTime> normalizedTimes = reminderTimes.stream()
                .filter(time -> time != null)
                .distinct()
                .sorted()
                .toList();

        if (normalizedTimes.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one valid reminder time is required");
        }

        medication.setReminderTimes(normalizedTimes);

        // 9. Keep the first time for backward compatibility
        medication.setReminderTime(normalizedTimes.get(0));

        // 10. Ensure MySQL generates a new ID
        medication.setId(null);

        // 11. Mark the server record as synchronized
        medication.setSyncStatus("SYNCED");

        // 12. Set creation timestamp
        if (medication.getCreatedAt() == null) {
            medication.setCreatedAt(LocalDateTime.now());
        }

        // 13. Save medication and reminder times in MySQL
        return medicationRepository.save(medication);
    }

    @Override
    public List<MedicationEntity> getMedicationsByPatientId(
            Integer patientId) {

        if (patientId == null
                || !patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException(
                    "Patient not found");
        }

        return medicationRepository.findByPatientId(patientId);
    }
}
