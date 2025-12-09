package com.server.controller;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.server.dto.RatingRequest;
import com.server.entity.Feedback;
import com.server.entity.Rating;
import com.server.response.ApiResponse;
import com.server.service.FeedbackService;
import com.server.service.RatingService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@Slf4j
@RequestMapping("/api/v1/rating")
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
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Invalid feedback ID"));
            }

            Rating rating = new Rating();
            rating.setStars(request.getStars());
            rating.setFeedback(feedback);

            Rating saved = ratingService.saveRating(rating);
            log.info("Rating saved successfully");
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Rating saved", saved));

        } catch (Exception e) {
        	log.error("Error while saving rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save rating"));
        }
  }
  
  @GetMapping("/average/{consultantId}")
  public ResponseEntity<?> getAverage(@PathVariable Long consultantId) {
      try {
          Double avg = ratingService.getAverageRating(consultantId);
          log.info("Average rating for consultantId {} = {}", consultantId, avg);
          return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Average rating fetched", avg));
      } catch (Exception e) {
    	  log.error("Error while calculating average rating: {}", e.getMessage());

          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get average rating"));
      }
  }
  
  
  // ================== UPDATE RATING =====================
  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateRating(@PathVariable Long id, @RequestBody RatingRequest req) {

      log.info("Updating rating for id {}", id);

      try {
          Rating updated = ratingService.updateRating(id, req.getStars());

          if (updated == null) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                      .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Rating not found"));
          }

          return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Rating updated", updated));

      } catch (Exception e) {
          log.error("Error updating rating: {}", e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update rating"));
      }
  }
  
  // ================== DELETE RATING =====================
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteRating(@PathVariable Long id) {

      log.info("Deleting rating with id {}", id);

      try {
          boolean deleted = ratingService.deleteRating(id);

          if (!deleted) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                      .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Rating not found"));
          }

          return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Rating deleted"));

      } catch (Exception e) {
          log.error("Error deleting rating: {}", e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete rating"));
      }
  }
}

  
  

