package com.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequestDTO {
    private Long consultationId;
    private String feedbackText;
    private Integer ratingCommunication;
    private Integer ratingExpertise;
    private Integer ratingTimeliness;
    private Boolean isAnonymous;
}
