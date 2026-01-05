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
    	        log.info("Rating saved with ID: {}", saved.getId());
    	        return saved;

             
         } catch (Exception e) {
        	 log.error("Error saving rating: {}", e.getMessage());

             throw new RuntimeException("Failed to save rating.");
         }
     }
     
     public Double getAverageRating(Long consultantId) {
    	 log.info("Inside getAverageRating");
    	 try {             Double avg = ratingRepository.findAverageByFeedback_Consultant_Id(consultantId);
             log.info("Average Calculated =",avg);
             
             return avg != null ? avg : 0.0;
            
         } catch (Exception e) {
        	   log.info("error in calculating average", e.getMessage());
             throw new RuntimeException("Failed to calculate average rating.");
         }
     }
     
     public Rating getById(Long id) {
    	  try {
              return ratingRepository.findById(id).orElse(null);
          } catch (Exception e) {
              log.error("Failed to fetch rating by ID {}", id);
              throw new RuntimeException("Could not fetch rating");
          }
    	}

     
     public Rating updateRating(Long id, int newStars) {
    	    try {
    	    	 log.info("Updating rating with id: {}", id);
    	        Rating existing = ratingRepository.findById(id).orElse(null);

    	        if (existing == null) {
    	        	 log.warn("Rating not found for id {}", id);
    	            return null; // controller handles this
    	        }

    	        existing.setStars(newStars);

    	        return ratingRepository.save(existing);

    	    } catch (Exception e) {
    	        throw new RuntimeException("Error while updating rating: " + e.getMessage());
    	    }
    	}


     
     
  // ================== DELETE =====================
     public boolean deleteRating(Long id) {
         try {
             log.info("Deleting rating with id: {}", id);

             if (!ratingRepository.existsById(id)) {
                 log.warn("Rating not found for deletion. ID {}", id);
                 return false;
             }

             ratingRepository.deleteById(id);
             log.info("Rating deleted successfully");
             return true;

         } catch (Exception e) {
             log.error("Error while deleting rating: {}", e.getMessage());
             throw new RuntimeException("Error while deleting rating");
         }
     }
 }
     
     


