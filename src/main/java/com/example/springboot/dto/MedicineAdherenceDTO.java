package com.example.springboot.dto;

public class MedicineAdherenceDTO {

    private Integer medicationId;
    private String medicineName;
    private String dosage;

    private long totalTaken;
    private long totalMissed;

    public MedicineAdherenceDTO() {
    }

    public Integer getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
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
    
    private long totalPrescribed;

    public long getTotalPrescribed() {
        return totalPrescribed;
    }

    public void setTotalPrescribed(long totalPrescribed) {
        this.totalPrescribed = totalPrescribed;
    }
}