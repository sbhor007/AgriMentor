package com.server.service;

import com.server.dto.FeedbackDTO;
import com.server.dto.FeedbackRequestDTO;

import java.util.List;
import java.util.Map;

public interface FeedbackService {
    FeedbackDTO createFeedback(FeedbackRequestDTO request, String farmerEmail);

    FeedbackDTO updateFeedback(Long feedbackId, FeedbackRequestDTO request, String farmerEmail);

    FeedbackDTO getFeedbackById(Long id);

    FeedbackDTO getFeedbackByConsultationId(Long consultationId);
    
    boolean hasFeedback(Long consultationId);

    List<FeedbackDTO> getFeedbackByConsultantId(Long consultantId);

    List<FeedbackDTO> getAllFeedback();
    
    Double getConsultantAverageRating(Long consultantId);
    
    Map<String, Object> getConsultantFeedbackStats(Long consultantId);

    void deleteFeedback(Long id);
}
