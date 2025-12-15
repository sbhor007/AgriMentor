package com.server.dto.ConsultationReportDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for creating or updating a Consultation Report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationReportRequestDTO {
    private Long consultationId;
    private String reportText;
    private String recommendations;
    private String identifiedIssue;
    private LocalDateTime followUpDate;
}
