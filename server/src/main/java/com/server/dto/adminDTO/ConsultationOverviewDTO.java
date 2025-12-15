package com.server.dto.adminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationOverviewDTO {
    private Long totalConsultations;
    private Long activeConsultations;
    private Long completedConsultations;
    private Long pendingConsultations;
    private Long rejectedConsultations;
    private Long approvedConsultations;
}
