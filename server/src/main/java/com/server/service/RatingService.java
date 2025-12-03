package com.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.entity.Rating;
import com.server.repository.RatingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RatingService {
     @Autowired
     private RatingRepository ratingRepository;
     
     public Rating saveRating(Rating rating) {
    	 
    	 try {
    	        Rating saved = ratingRepository.save(rating);
    	        log.info("Rating saved");
    	        return saved;

             
         } catch (Exception e) {
        	 log.error("Error saving rating: {}", e.getMessage());

             throw new RuntimeException("Failed to save rating.");
         }
     }
     
     public Double getAverageRating(Long consultantId) {
    	 try {
             Double avg = ratingRepository.getAverageByConsultant(consultantId);
             log.info("Average Calculated =",avg);
             return avg != null ? avg : 0.0;
         } catch (Exception e) {
        	   log.info("error in calculating average");
             throw new RuntimeException("Failed to calculate average rating.");
         }
     }
}
