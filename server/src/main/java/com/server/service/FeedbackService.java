package com.server.service;

import java.lang.System.Logger;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.dto.FeedbackRequest;
import com.server.entity.Consultant;
import com.server.entity.Farmer;
import com.server.entity.Feedback;
import com.server.repository.FeedbackRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeedbackService {
   @Autowired
   private FeedbackRepository feedbackRepository;
   
   @Autowired
   private ConsultantService consultantService;
   
   @Autowired
   private FarmerService farmerService;
   
   //store feedback
   @Transactional
   public Feedback saveFeedback(FeedbackRequest feedback, String userrme, Long consultantId) {
	   log.info("Saving feedback for consultantId: {}", consultantId);
	   Consultant consultant = consultantService.getConsultantById(consultantId).orElse(null);
	   if(consultant == null) {
		   log.info("Consultant not found");
		throw new RuntimeException("Consultant Not found");   
	   }
	   log.info("before farmer get");
	   Farmer farmer = (Farmer) farmerService.findByEmail(userrme).orElse(null);
	   log.info("before farmer get: {}", farmer);
	   if(farmer == null) {
		   log.info("farmer not found");
			throw new RuntimeException("farmer Not found");   
		   }
	   
	   try {
		   
		   Feedback feedback2 = new Feedback();
		   
		   feedback2.setFarmer(farmer);
		   feedback2.setConsultant(consultant);
		   feedback2.setComment(feedback.getComment());
		   feedback2.setCreatedAt(LocalDateTime.now());
		   
         Feedback saved = feedbackRepository.save(feedback2);
         log.info("Feedback saved successfully ");
            return saved;
       } catch (Exception e) {
           log.error("Error while saving feedback: {}", e.getMessage());

           throw new RuntimeException("Failed to save feedback: " + e.getMessage());
       }
   }
   
   public List<Feedback> getFeedbackForConsultant(Long consultantId){
	   try {
		   log.info("Fetching feedback for consultantId: {}", consultantId);
           return feedbackRepository.findByConsultantId(consultantId);
       } catch (Exception e) {
    	   log.error("Error fetching feedback list: {}", e.getMessage());
           throw new RuntimeException("Unable to fetch feedback list.");
       }
   }
   
   public Feedback getById(Long id) {
	   try {
           return feedbackRepository.findById(id).orElse(null);
       } catch (Exception e) {
           log.error("Error fetching feedback by ID: {}", e.getMessage());
           throw new RuntimeException("Unable to fetch feedback details.");
       }
   }
   
   
   
   @Transactional
   public Feedback updateFeedback(Long id, Feedback updatedFeedback) {

	   try {
		   log.info("Updating feedback id: {}", id);
	        Feedback existing = feedbackRepository.findById(id).orElse(null);

	        if (existing == null) {
	            return null; // controller will handle this
	        }

	        existing.setComment(updatedFeedback.getComment());
	        existing.setCreatedAt(LocalDateTime.now());
//	        existing.setConsultantId(updatedFeedback.getConsultantId());
	        
//	        existing.setFarmerId(updatedFeedback.getFarmerId());

	        return feedbackRepository.save(existing);

	    } catch (Exception e) {
	    	 log.error("Error while updating feedback: {}", e.getMessage());
	        throw new RuntimeException("Error while updating feedback: " + e.getMessage());
	    }
	}

   @Transactional
   public boolean deleteFeedback(Long id) {
	   try {
           log.info("Deleting feedback id: {}", id);

           if (!feedbackRepository.existsById(id)) {
               log.warn("Feedback not found");
               return false;
           }

           feedbackRepository.deleteById(id);
           log.info("Feedback deleted successfully");
           return true;

	    } catch (Exception e) {
	        throw new RuntimeException("Error while deleting feedback: " + e.getMessage());
	    }
	}

   
}
