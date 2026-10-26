package com.example.springboot.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dto.AdherenceReportDTO;
import com.example.springboot.entity.PatientEntity;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.AdherenceReportService;

@RestController
@RequestMapping("/adherence-report")
public class AdherenceReportController {

    private final AdherenceReportService adherenceReportService;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AdherenceReportController(
            AdherenceReportService adherenceReportService,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.adherenceReportService = adherenceReportService;
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
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(role -> role.equals("ROLE_ADMIN")
                        || role.equals("ROLE_DOCTOR"))
                .map(role -> role.substring(5))
                .findFirst()
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Only admins and doctors can view adherence reports"));
    }

    private void checkReportAccess(
            Integer patientId,
            Authentication authentication) {

        String role = getRole(authentication);
        Integer principalId = getPrincipalId(authentication);

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found"));

        if ("ADMIN".equals(role)) {
            return;
        }

        UserEntity doctor = userRepository.findById(principalId)
                .orElseThrow(() ->
                        new AccessDeniedException("Doctor account not found"));

        if ("DOCTOR".equals(role)
                && doctor.getLocation() != null
                && doctor.getLocation().equals(patient.getLocation())) {
            return;
        }

        throw new AccessDeniedException(
                "You are not allowed to view this patient's adherence report");
    }

    @GetMapping("/patient/{patientId}")
    public AdherenceReportDTO getPatientAdherenceReport(
            @PathVariable Integer patientId,
            Authentication authentication) {

        checkReportAccess(patientId, authentication);

        return adherenceReportService.generateReport(patientId);
    }
}
