package com.example.springboot.controller;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.springboot.entity.MedicationEntity;
import com.example.springboot.entity.PatientEntity;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.MedicationService;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    private final MedicationService medicationService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public MedicationController(
            MedicationService medicationService,
            UserRepository userRepository,
            PatientRepository patientRepository) {

        this.medicationService = medicationService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    private String getRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
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

    private Integer getPrincipalId(Authentication authentication) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof Integer)) {
            throw new AccessDeniedException("Invalid authentication");
        }

        return (Integer) authentication.getPrincipal();
    }

    private void checkPatientViewAccess(
            Integer patientId,
            Authentication authentication) {

        String role = getRole(authentication);
        Integer principalId = getPrincipalId(authentication);

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));

        // Patients can view only their own medications.
        if ("PATIENT".equals(role)) {
            if (principalId.equals(patient.getId())) {
                return;
            }

            throw new AccessDeniedException(
                    "Patients can only view their own medications");
        }

        // Resolve the staff ID from the staff table, not the patient table.
        UserEntity user = userRepository.findById(principalId)
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"));

        // Admin can view medications for all patients.
        if ("ADMIN".equals(role)) {
            return;
        }

        // Doctor can view medications only in their assigned location.
        if ("DOCTOR".equals(role)
                && user.getLocation() != null
                && user.getLocation().equals(patient.getLocation())) {
            return;
        }

        throw new AccessDeniedException(
                "You are not allowed to view this patient's medications");
    }

    // Only the authenticated patient can create their own schedule.
    @PostMapping
    public MedicationEntity addMedication(
            @RequestBody MedicationEntity medication,
            Authentication authentication) {

        String role = getRole(authentication);
        Integer principalId = getPrincipalId(authentication);

        if (!"PATIENT".equals(role)) {
            throw new AccessDeniedException(
                    "Only patients can create medication schedules");
        }

        if (medication.getPatientId() == null
                || !principalId.equals(medication.getPatientId())) {
            throw new AccessDeniedException(
                    "You can create schedules only for yourself");
        }

        patientRepository.findById(principalId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));

        return medicationService.addMedication(medication);
    }

    // Patient: own records. Doctor: assigned location.
    // Admin: all patient records.
    @GetMapping("/patient/{patientId}")
    public List<MedicationEntity> getMedicationsByPatient(
            @PathVariable Integer patientId,
            Authentication authentication) {

        checkPatientViewAccess(patientId, authentication);

        return medicationService.getMedicationsByPatientId(patientId);
    }
}
