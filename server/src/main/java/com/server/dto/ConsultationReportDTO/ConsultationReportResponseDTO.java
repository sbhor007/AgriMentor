package com.server.dto.ConsultationReportDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning Consultation Report data to clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationReportResponseDTO {
    private Long id;
    private Long consultationId;
    private String consultationTopic;
    private String farmerName;
    private String consultantName;
    private String reportText;
    private String recommendations;
    private String identifiedIssue;
    private LocalDateTime followUpDate;
    private LocalDateTime createdAt;
    private List<ReportAttachmentDTO> attachments;
}
