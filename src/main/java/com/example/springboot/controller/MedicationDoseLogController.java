package com.example.springboot.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.springboot.entity.MedicationDoseLogEntity;
import com.example.springboot.entity.MedicationEntity;
import com.example.springboot.entity.PatientEntity;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.MedicationDoseLogRepository;
import com.example.springboot.repository.MedicationRepository;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.MedicationDoseLogService;

@RestController
@RequestMapping("/medication-doses")
public class MedicationDoseLogController {

    private final MedicationDoseLogService doseLogService;
    private final MedicationDoseLogRepository doseLogRepository;
    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicationDoseLogController(
            MedicationDoseLogService doseLogService,
            MedicationDoseLogRepository doseLogRepository,
            MedicationRepository medicationRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.doseLogService = doseLogService;
        this.doseLogRepository = doseLogRepository;
        this.medicationRepository = medicationRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    private Integer getPrincipalId(Authentication authentication) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof Integer)) {
            throw new AccessDeniedException("Authentication required");
        }

        return (Integer) authentication.getPrincipal();
    }

    private String getRole(Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Authentication required");
        }

        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(role -> role.equals("ROLE_PATIENT")
                        || role.equals("ROLE_ADMIN")
                        || role.equals("ROLE_DOCTOR"))
                .map(role -> role.substring(5))
                .findFirst()
                .orElseThrow(() ->
                        new AccessDeniedException("Invalid role"));
    }

    private void checkPatientAccess(
            Integer patientId,
            Authentication authentication) {

        String role = getRole(authentication);
        Integer principalId = getPrincipalId(authentication);

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));

        if ("PATIENT".equals(role)) {
            if (principalId.equals(patient.getId())) {
                return;
            }

            throw new AccessDeniedException(
                    "Patients can access only their own records");
        }

        UserEntity user = userRepository.findById(principalId)
                .orElseThrow(() ->
                        new AccessDeniedException("Staff user not found"));

        if ("ADMIN".equals(role)) {
            return;
        }

        if ("DOCTOR".equals(role)
                && user.getLocation() != null
                && user.getLocation().equals(patient.getLocation())) {
            return;
        }

        throw new AccessDeniedException(
                "You are not allowed to access this patient");
    }

    // Create a dose log only for the authenticated patient's own medication.
    @PostMapping
    public MedicationDoseLogEntity createDoseLog(
            @RequestParam Integer medicationId,
            @RequestParam String scheduledDate,
            @RequestParam(required = false) String scheduledTime,
            Authentication authentication) {

        String role = getRole(authentication);

        if (!"PATIENT".equals(role)) {
            throw new AccessDeniedException(
                    "Only patients can create dose records");
        }

        Integer patientId = getPrincipalId(authentication);

        MedicationEntity medication = medicationRepository
                .findById(medicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medication not found"));

        if (!patientId.equals(medication.getPatientId())) {
            throw new AccessDeniedException(
                    "You can create dose records only for your own medication");
        }

        LocalDate date;
        LocalTime time;

        try {
        	    date = LocalDate.parse(scheduledDate);

        	    if (scheduledTime == null || scheduledTime.isBlank()) {
        	        if (medication.getReminderTimes() != null
        	                && !medication.getReminderTimes().isEmpty()) {
        	            time = medication.getReminderTimes().get(0);
        	        } else {
        	            time = medication.getReminderTime();
        	        }
        	    } else {
        	        time = LocalTime.parse(scheduledTime);
        	    }
        	} catch (DateTimeParseException exception) {
        	    throw new IllegalArgumentException(
        	            "Use date format YYYY-MM-DD and time format HH:mm:ss");
        	}
        

        return doseLogService.createDoseLog(medicationId, date, time);
    }

    // Only the patient who owns the dose can mark it as taken.
    @PutMapping("/{doseId}/taken")
    public MedicationDoseLogEntity markAsTaken(
            @PathVariable Integer doseId,
            Authentication authentication) {

        MedicationDoseLogEntity log = findDoseLog(doseId);

        if (!"PATIENT".equals(getRole(authentication))) {
            throw new AccessDeniedException(
                    "Only patients can update dose status");
        }

        checkPatientAccess(log.getPatientId(), authentication);

        return doseLogService.markAsTaken(doseId);
    }

    // Only the patient who owns the dose can mark it as missed.
    @PutMapping("/{doseId}/missed")
    public MedicationDoseLogEntity markAsMissed(
            @PathVariable Integer doseId,
            Authentication authentication) {

        MedicationDoseLogEntity log = findDoseLog(doseId);

        if (!"PATIENT".equals(getRole(authentication))) {
            throw new AccessDeniedException(
                    "Only patients can update dose status");
        }

        checkPatientAccess(log.getPatientId(), authentication);

        return doseLogService.markAsMissed(doseId);
    }

    // Patient: own records. Doctor: assigned location. Admin: all patients.
    @GetMapping("/patient/{patientId}")
    public List<MedicationDoseLogEntity> getPatientDoseLogs(
            @PathVariable Integer patientId,
            @RequestParam(required = false) String date,
            Authentication authentication) {

        checkPatientAccess(patientId, authentication);

        if (date != null && !date.isBlank()) {
            try {
                return doseLogService.getPatientDoseLogsByDate(
                        patientId, LocalDate.parse(date));
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException(
                        "Date must use YYYY-MM-DD format");
            }
        }

        return doseLogService.getPatientDoseLogs(patientId);
    }

    private MedicationDoseLogEntity findDoseLog(Integer doseId) {
        return doseLogRepository.findById(doseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Dose log not found"));
    }
}
