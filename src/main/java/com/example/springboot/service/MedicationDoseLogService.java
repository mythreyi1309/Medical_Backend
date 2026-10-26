
package com.example.springboot.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.springboot.entity.MedicationDoseLogEntity;

public interface MedicationDoseLogService {

    MedicationDoseLogEntity createDoseLog(
            Integer medicationId,
            LocalDate scheduledDate,
            LocalTime scheduledTime);

    MedicationDoseLogEntity markAsTaken(Integer doseId);

    MedicationDoseLogEntity markAsMissed(Integer doseId);

    List<MedicationDoseLogEntity> getPatientDoseLogs(
            Integer patientId);

    List<MedicationDoseLogEntity> getPatientDoseLogsByDate(
            Integer patientId,
            LocalDate date);
}
