package com.example.springboot.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springboot.entity.PatientEntity;
import com.example.springboot.repository.PatientRepository;
import com.example.springboot.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientEntity savePatient(PatientEntity patient) {

        if (patientRepository.findByPhone(patient.getPhone()).isPresent()) {
            throw new IllegalArgumentException(
                    "Patient with this phone number already exists"
            );
        }

        return patientRepository.save(patient);
    }

    @Override
    public List<PatientEntity> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public List<PatientEntity> getPatientsByLocation(String location) {
        return patientRepository.findByLocation(location);
    }
}
