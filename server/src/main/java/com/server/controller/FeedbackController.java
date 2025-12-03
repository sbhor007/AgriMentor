package com.server.controller;

import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.entity.Feedback;
import com.server.service.FeedbackService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/feedback")
@Slf4j
public class FeedbackController {
      @Autowired
      private FeedbackService feedbackService;
      
      @PostMapping("/add")
      public ResponseEntity<?> addFeedback(@RequestBody Feedback feedback){
    	  log.info("Received request to add feedback for consultantId: {}", feedback.getConsultantId());
    	  try {
			Feedback saved=feedbackService.saveFeedback(feedback);
            log.info("Feedback added successfully");
			return ResponseEntity.ok(saved);
    	  } catch (Exception e) {
              log.error("Error while adding feedback: {}", e.getMessage());

    		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch feedback");
		}
    	  
      }
      
      
      

      @GetMapping("/consultant/{id}")
      public ResponseEntity<?> getFeedbacks(@PathVariable Long id) {

          log.info("Fetching feedback list for consultantId: {}", id);

          try {
              List<Feedback> list = feedbackService.getFeedbackForConsultant(id);
              log.info("Fetched {} feedback entries", list.size());
              return ResponseEntity.ok(list);

          } catch (Exception e) {
              log.error("Error while fetching feedback list: {}", e.getMessage());
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                   .body("Failed to fetch feedback");
          }
      }
      
}
