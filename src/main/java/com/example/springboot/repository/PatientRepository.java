package com.example.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.entity.PatientEntity;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Integer> {

    Optional<PatientEntity> findByPhone(String phone);

    Optional<PatientEntity> findByNameAndPhone(String name, String phone);

    List<PatientEntity> findByLocation(String location);
}

