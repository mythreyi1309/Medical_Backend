package com.example.springboot.service;

import java.util.List;

import com.example.springboot.entity.MedicationEntity;

public interface MedicationService {

    MedicationEntity addMedication(MedicationEntity medication);

    List<MedicationEntity> getMedicationsByPatientId(Integer patientId);
}
