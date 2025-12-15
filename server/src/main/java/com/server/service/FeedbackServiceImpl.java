package com.server.service;

import com.server.dto.FeedbackDTO;
import com.server.dto.FeedbackRequestDTO;
import com.server.entity.Consultant;
import com.server.entity.Consultation;
import com.server.entity.Farmer;
import com.server.entity.Feedback;
import com.server.enumeration.ConsultationRequestStatus;
import com.server.repository.ConsultationRepository;
import com.server.repository.FarmerRepository;
import com.server.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private FarmerRepository farmerRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public FeedbackDTO createFeedback(FeedbackRequestDTO request, String farmerEmail) {
        Consultation consultation = consultationRepository.findById(request.getConsultationId())
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        if (consultation.getConsultationRequestStatus() != ConsultationRequestStatus.COMPLETED) {
            throw new RuntimeException("Feedback can only be submitted for COMPLETED consultations");
        }

        Farmer farmer = farmerRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        if (!consultation.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You can only submit feedback for your own consultations");
        }

        if (feedbackRepository.existsByConsultationId(request.getConsultationId())) {
            throw new RuntimeException("Feedback already exists for this consultation");
        }

        Feedback feedback = new Feedback();
        feedback.setConsultation(consultation);
        feedback.setFarmer(farmer);
        feedback.setConsultant(consultation.getConsultant());
        feedback.setFeedbackText(request.getFeedbackText());
        feedback.setRatingCommunication(request.getRatingCommunication());
        feedback.setRatingExpertise(request.getRatingExpertise());
        feedback.setRatingTimeliness(request.getRatingTimeliness());
        feedback.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);

        Feedback savedFeedback = feedbackRepository.save(feedback);
        
        // Send email notification to consultant
        sendFeedbackNotificationAsync(savedFeedback);

        return mapToDTO(savedFeedback);
    }

    @Override
    public FeedbackDTO updateFeedback(Long feedbackId, FeedbackRequestDTO request, String farmerEmail) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        Farmer farmer = farmerRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        if (!feedback.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You can only update your own feedback");
        }
        
        // 7-day edit limit check
        long daysBetween = ChronoUnit.DAYS.between(feedback.getCreatedAt(), LocalDateTime.now());
        if (daysBetween > 7) {
            throw new RuntimeException("Feedback can only be edited within 7 days of submission");
        }

        feedback.setFeedbackText(request.getFeedbackText());
        feedback.setRatingCommunication(request.getRatingCommunication());
        feedback.setRatingExpertise(request.getRatingExpertise());
        feedback.setRatingTimeliness(request.getRatingTimeliness());
        feedback.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);
        
        // Re-calculate overall rating handled by @PreUpdate in entity
        
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return mapToDTO(updatedFeedback);
    }

    @Override
    public FeedbackDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        return mapToDTO(feedback);
    }

    @Override
    public FeedbackDTO getFeedbackByConsultationId(Long consultationId) {
        Feedback feedback = feedbackRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Feedback not found for this consultation"));
        return mapToDTO(feedback);
    }
    
    @Override
    public boolean hasFeedback(Long consultationId) {
        return feedbackRepository.existsByConsultationId(consultationId);
    }

    @Override
    public List<FeedbackDTO> getFeedbackByConsultantId(Long consultantId) {
        return feedbackRepository.findByConsultantId(consultantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDTO> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Double getConsultantAverageRating(Long consultantId) {
        Double avg = feedbackRepository.getAverageRatingByConsultantId(consultantId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
    
    @Override
    public Map<String, Object> getConsultantFeedbackStats(Long consultantId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", getConsultantAverageRating(consultantId));
        stats.put("totalReviews", feedbackRepository.countByConsultantId(consultantId));
        // Can add more stats here (breakdown by category if needed)
        return stats;
    }

    @Override
    public void deleteFeedback(Long id) {
        // Admin or Authorized delete
        feedbackRepository.deleteById(id);
    }
    
    private void sendFeedbackNotificationAsync(Feedback feedback) {
        try {
            Consultant consultant = feedback.getConsultant();
            String subject = "New Feedback Received";
            String body = "Dear " + consultant.getFirstName() + ",\n\n" +
                    "You have received a new feedback for consultation ID: " + feedback.getConsultation().getId() + ".\n" +
                    "Rating: " + String.format("%.1f", feedback.getRatingOverall()) + "/5\n\n" +
                    "Log in to the portal to view the details.\n\n" +
                    "Best Regards,\nE-Consultancy Team";
            
            emailService.sendEmail(consultant.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send feedback notification email: " + e.getMessage());
        }
    }

    private FeedbackDTO mapToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setConsultationId(feedback.getConsultation().getId());
        dto.setFarmerId(feedback.getFarmer().getId());
        dto.setConsultantId(feedback.getConsultant().getId());
        dto.setConsultantName(feedback.getConsultant().getFirstName() + " " + feedback.getConsultant().getLastName());
        
        // Handle Anonymity
        if (feedback.getIsAnonymous()) {
            dto.setFarmerName("Anonymous Farmer");
        } else {
            dto.setFarmerName(feedback.getFarmer().getFirstName() + " " + feedback.getFarmer().getLastName());
        }
        
        dto.setFeedbackText(feedback.getFeedbackText());
        dto.setRatingCommunication(feedback.getRatingCommunication());
        dto.setRatingExpertise(feedback.getRatingExpertise());
        dto.setRatingTimeliness(feedback.getRatingTimeliness());
        dto.setRatingOverall(feedback.getRatingOverall());
        dto.setIsAnonymous(feedback.getIsAnonymous());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setUpdatedAt(feedback.getUpdatedAt());
        
        return dto;
    }
}
