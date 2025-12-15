package com.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    private Long id;
    private Long consultationId;
    private Long farmerId;
    private String farmerName;
    private Long consultantId;
    private String consultantName;
    
    private String feedbackText;
    private Integer ratingCommunication;
    private Integer ratingExpertise;
    private Integer ratingTimeliness;
    private Double ratingOverall;
    private Boolean isAnonymous;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
