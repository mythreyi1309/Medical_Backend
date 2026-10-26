package com.example.springboot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.springboot.entity.UserEntity;
import com.example.springboot.service.DoctorService;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Get all doctors
    @GetMapping
    public List<UserEntity> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    // Add a doctor
    @PostMapping
    public UserEntity addDoctor(@RequestBody UserEntity doctor) {
        return doctorService.addDoctor(doctor);
    }

    // Delete a doctor
    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable int id) {
        doctorService.deleteDoctor(id);
        return "Doctor deleted successfully";
    }
}