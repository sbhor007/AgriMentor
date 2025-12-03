package com.server.controller;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.server.dto.RatingRequest;
import com.server.entity.Feedback;
import com.server.entity.Rating;
import com.server.service.FeedbackService;
import com.server.service.RatingService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Slf4j
public class RatingController {
  @Autowired
  private RatingService ratingService;
  @Autowired
    private FeedbackService feedbackService;
  @PostMapping("/add")
  public ResponseEntity<?> addRating(@RequestBody RatingRequest request) {
	    try {
            Feedback feedback = feedbackService.getById(request.getFeedbackId());
            if (feedback == null) {
            	  log.warn("Invalid feedbackId received: {}", request.getFeedbackId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid feedback ID");
            }

            Rating rating = new Rating();
            rating.setStars(request.getStars());
            rating.setFeedback(feedback);

            Rating saved = ratingService.saveRating(rating);
            log.info("Rating saved successfully");
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
        	log.error("Error while saving rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save rating");
        }
  }
  
  @GetMapping("/average/{consultantId}")
  public ResponseEntity<?> getAverage(@PathVariable Long consultantId) {
      try {
          Double avg = ratingService.getAverageRating(consultantId);
          log.info("Average rating for consultantId {} = {}", consultantId, avg);
          return ResponseEntity.ok(avg);
      } catch (Exception e) {
    	  log.error("Error while calculating average rating: {}", e.getMessage());

          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Failed to get average rating");
      }
  }
  
}
