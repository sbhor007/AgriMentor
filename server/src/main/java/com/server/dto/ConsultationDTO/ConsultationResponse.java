package com.server.dto.ConsultationDTO;

import com.server.dto.CropDTO;
import com.server.enumeration.ConsultationRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponse {
    private Long id;
    private String topic;
    private String description;
    private ConsultationRequestStatus consultationRequestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    private CropDTO crop;
    private Object farmer;  // or create FarmerShortDTO
    private Object consultant;  // or create ConsultantShortDTO
    private Object farmAddress;
    private Object farmVisits;
    private Object consultationReports;
}
