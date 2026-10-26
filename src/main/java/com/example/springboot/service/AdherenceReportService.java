
package com.example.springboot.service;

import com.example.springboot.dto.AdherenceReportDTO;

public interface AdherenceReportService {

    AdherenceReportDTO generateReport(Integer patientId);
}


