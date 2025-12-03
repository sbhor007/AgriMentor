package com.server.service;

import java.lang.System.Logger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.entity.Feedback;
import com.server.repository.FeedbackRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeedbackService {
   @Autowired
   private FeedbackRepository feedbackRepository;
   
   public Feedback saveFeedback(Feedback feedback) {
	   log.info("Saving feedback for consultantId: {}", feedback.getConsultantId());
	   try {
         Feedback saved = feedbackRepository.save(feedback);
         log.info("Feedback saved successfully ");
            return saved;
       } catch (Exception e) {
           log.error("Error while saving feedback: {}", e.getMessage());

           throw new RuntimeException("Failed to save feedback: " + e.getMessage());
       }
   }
   
   public List<Feedback> getFeedbackForConsultant(Long consultantId){
	   try {
           return feedbackRepository.findByConsultantId(consultantId);
       } catch (Exception e) {
           throw new RuntimeException("Unable to fetch feedback list.");
       }
   }
   
   public Feedback getById(Long id) {
       return feedbackRepository.findById(id).orElse(null);
   }
   
}
