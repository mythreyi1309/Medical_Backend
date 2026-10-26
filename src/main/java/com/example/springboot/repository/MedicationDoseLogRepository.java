package com.example.springboot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.entity.MedicationDoseLogEntity;

@Repository
public interface MedicationDoseLogRepository
        extends JpaRepository<MedicationDoseLogEntity, Integer> {

    List<MedicationDoseLogEntity> findByPatientId(Integer patientId);

    List<MedicationDoseLogEntity> findByPatientIdAndScheduledDate(
            Integer patientId, LocalDate scheduledDate);

    List<MedicationDoseLogEntity> findByMedicationId(Integer medicationId);

    Optional<MedicationDoseLogEntity>
    findByMedicationIdAndScheduledDateAndScheduledTime(
            Integer medicationId,
            LocalDate scheduledDate,
            LocalTime scheduledTime);
    
    @Modifying
    @Query("""
        UPDATE MedicationDoseLogEntity d
        SET d.status = 'MISSED',
            d.updatedAt = :now
        WHERE d.status = 'PENDING'
          AND FUNCTION('TIMESTAMP', d.scheduledDate, d.scheduledTime)
              <= :cutoff
        """)
    int markOverduePendingDosesAsMissed(
            @Param("cutoff") LocalDateTime cutoff,
            @Param("now") LocalDateTime now);
    
    List<MedicationDoseLogEntity> findByStatus(String status);
    
    List<MedicationDoseLogEntity>
    findByPatientIdAndScheduledDateBetween(
            Integer patientId,
            LocalDate startDate,
            LocalDate endDate);
}

