package com.server.dto;

import com.server.enumeration.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Farm Visit Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmVisitResponse {
    private Long id;
    private Long consultationId;
    private String consultationTopic;
    private LocalDateTime scheduledDate;
    private String visitNotes;
    private VisitStatus visitStatus;
    private AddressDTO farmAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isOverdue;
}
