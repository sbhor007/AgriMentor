package com.server.dto.ConsultationReportDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Report Attachment metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAttachmentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    // Note: fileContent is excluded from DTO to avoid large payloads
    // Use separate endpoint to download actual file content
}
