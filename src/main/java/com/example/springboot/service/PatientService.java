package com.example.springboot.service;

import java.util.List;

import com.example.springboot.entity.PatientEntity;

public interface PatientService {

	PatientEntity savePatient(PatientEntity patient);
 
	List<PatientEntity> getAllPatients();
	List<PatientEntity> getPatientsByLocation(String location);

}
