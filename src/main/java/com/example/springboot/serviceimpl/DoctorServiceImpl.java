package com.example.springboot.serviceimpl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorServiceImpl(UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserEntity> getAllDoctors() {
        return userRepository.findByRole("DOCTOR");
    }

    @Override
    public UserEntity addDoctor(UserEntity doctor) {

        doctor.setRole("DOCTOR");

        doctor.setPassword(
            passwordEncoder.encode(doctor.getPassword())
        );

        return userRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(int id) {

        UserEntity doctor = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new RuntimeException("Only doctors can be deleted");
        }

        userRepository.deleteById(id);
    }


}
