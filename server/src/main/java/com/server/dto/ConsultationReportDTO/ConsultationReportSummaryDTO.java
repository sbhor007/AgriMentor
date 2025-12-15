package com.server.dto.ConsultationReportDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for listing Consultation Reports in summary view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationReportSummaryDTO {
    private Long id;
    private Long consultationId;
    private String consultationTopic;
    private String identifiedIssue;
    private LocalDateTime followUpDate;
    private LocalDateTime createdAt;
    private Integer attachmentCount;
}
