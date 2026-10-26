package com.example.springboot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dto.PatientLoginRequest;
import com.example.springboot.entity.PatientEntity;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.security.JwtService;
import com.example.springboot.service.PatientService;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PatientController(
            PatientService patientService,
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.patientService = patientService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Patient registration
    @PostMapping
    public ResponseEntity<?> addPatient(
            @RequestBody PatientEntity patient) {

        if (patient.getName() == null || patient.getName().isBlank()
                || patient.getPhone() == null || patient.getPhone().isBlank()
                || patient.getPassword() == null
                || patient.getPassword().isBlank()) {

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Name, phone number and password are required"
            ));
        }

        String phone = patient.getPhone().trim();
        patient.setName(patient.getName().trim());
        patient.setPhone(phone);

        if (patientRepository.findByPhone(phone).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "message", "Phone number is already registered"
            ));
        }

        patient.setPassword(
                passwordEncoder.encode(patient.getPassword())
        );

        PatientEntity savedPatient = patientService.savePatient(patient);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Patient registered successfully",
                "patientId", savedPatient.getId()
        ));
    }

    // Admin: all patients. Doctor: patients from assigned location.
    @GetMapping
    public ResponseEntity<?> getPatients(
            Authentication authentication) {

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Integer)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid authentication"));
        }

        int userId = (Integer) authentication.getPrincipal();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.ok(patientService.getAllPatients());
        }

        if ("DOCTOR".equals(user.getRole())) {
            return ResponseEntity.ok(
                    patientService.getPatientsByLocation(user.getLocation())
            );
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Access denied"));
    }

    // Patient login: name + phone + password
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> patientLogin(
            @RequestBody PatientLoginRequest request) {

        if (request.getName() == null || request.getName().isBlank()
                || request.getPhone() == null || request.getPhone().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Name, phone number and password are required"
            ));
        }

        PatientEntity patient = patientRepository.findByNameAndPhone(
                request.getName().trim(),
                request.getPhone().trim()
        ).orElse(null);

        if (patient == null
                || patient.getPassword() == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        patient.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid name, phone number or password"
            ));
        }

        String token = jwtService.generatePatientToken(patient.getId());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "patientId", patient.getId(),
                "token", token,
                "role", "PATIENT"
        ));
    }
}
