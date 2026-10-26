package com.example.springboot.dto;

import java.time.LocalDate;
import java.util.List;

public class AdherenceReportDTO {

    private Integer patientId;
    private String patientName;

    private LocalDate startDate;
    private LocalDate endDate;

    private long totalTaken;
    private long totalMissed;

    private List<MedicineAdherenceDTO> medicines;

    public AdherenceReportDTO() {
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public long getTotalTaken() {
        return totalTaken;
    }

    public void setTotalTaken(long totalTaken) {
        this.totalTaken = totalTaken;
    }

    public long getTotalMissed() {
        return totalMissed;
    }

    public void setTotalMissed(long totalMissed) {
        this.totalMissed = totalMissed;
    }

    public List<MedicineAdherenceDTO> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<MedicineAdherenceDTO> medicines) {
        this.medicines = medicines;
    }
    
    private long totalPrescribed;

    public long getTotalPrescribed() {
        return totalPrescribed;
    }

    public void setTotalPrescribed(long totalPrescribed) {
        this.totalPrescribed = totalPrescribed;
    }
}
