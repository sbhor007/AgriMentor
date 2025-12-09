package com.server.controller;

import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.dto.FeedbackRequest;
import com.server.entity.Feedback;
import com.server.response.ApiResponse;
import com.server.service.FeedbackService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/feedback")
@Slf4j
public class FeedbackController {
      @Autowired
      private FeedbackService feedbackService;
      
      @PostMapping("/add/{consultantId}")
      public ResponseEntity<?> addFeedback(@RequestBody FeedbackRequest feedback, @PathVariable Long consultantId,Authentication authentication){
    	  log.info("Received request to add feedback for consultantId: {}", consultantId);
    	  if (authentication == null || !authentication.isAuthenticated()) {
              log.warn("Unauthorized access attempt to get consultant profile");
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                      .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
          }
    	  String username = authentication.getName();
    	 
    	  try {
			Feedback saved=feedbackService.saveFeedback(feedback,username,consultantId);
            log.info("Feedback added successfully");
			return ResponseEntity.ok(new ApiResponse(HttpStatus.OK,"feedback store",saved));
    	  } catch (Exception e) {
              log.error("Error while adding feedback: {}", e.getMessage());
    		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to fetch feedback " + e.getMessage()));
		}
    	  
      }
      
      
      

      @GetMapping("/consultant/{id}")
      public ResponseEntity<?> getFeedbacks(@PathVariable Long id) {

          log.info("Fetching feedback list for consultantId: {}", id);

          try {
              List<Feedback> list = feedbackService.getFeedbackForConsultant(id);
              log.info("Fetched {} feedback entries", list.size());
              return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Feedback fetched", list));

          } catch (Exception e) {
              log.error("Error while fetching feedback list: {}", e.getMessage());
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                   .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch feedback"));
          }
      }
      
      
      
      
    
      // ============ UPDATE FEEDBACK ============
      @PutMapping("/update/{id}")
      public ResponseEntity<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback updated) {
          log.info("Updating feedback id: {}", id);

          try {
              Feedback result = feedbackService.updateFeedback(id, updated);

              if (result == null) {
                  return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                       .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Feedback not found"));
              }

              return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Feedback updated", result));

          } catch (Exception e) {
              log.error("Error updating feedback: {}", e.getMessage());
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                   .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update feedback"));
          }
      }

      
      
      
      
      
      
      
      
      
      
      
      
//      @PutMapping("/update/{id}")
//      public ResponseEntity<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback updated) {
//    	  log.info("Updating feedback with id: {}", id);
//
//    	    try {
//    	        Feedback result = feedbackService.updateFeedback(id, updated);
//
//    	        if (result == null) {
//    	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Feedback not found"));
//    	        }
//
//    	        return ResponseEntity.ok((new ApiResponse<>(HttpStatus.OK, "Feedback updated", result));
//
//    	    } catch (Exception e) {
//    	        log.error("Error updating feedback: {}", e.getMessage());
//    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//    	                             .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update feedback"));
//    	    }
//      }

      
      @DeleteMapping("/delete/{id}")
      public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
          log.info("Deleting feedback id: {}", id);

          try {
              boolean deleted = feedbackService.deleteFeedback(id);

              if (!deleted) {
                  return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                       .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Feedback not found"));
              }

              return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Feedback deleted"));

          } catch (Exception e) {
              log.error("Error deleting feedback: {}", e.getMessage());
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                   .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete feedback"));
          }
      }
      
}
