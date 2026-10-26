package com.example.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.entity.MedicationEntity;

@Repository
public interface MedicationRepository
        extends JpaRepository<MedicationEntity, Integer> {

    List<MedicationEntity> findByPatientId(Integer patientId);
}
