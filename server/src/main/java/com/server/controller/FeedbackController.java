package com.server.controller;

import com.server.dto.FeedbackDTO;
import com.server.dto.FeedbackRequestDTO;
import com.server.response.ApiResponse;
import com.server.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackDTO>> createFeedback(@RequestBody FeedbackRequestDTO request,
            Principal principal) {
        FeedbackDTO feedback = feedbackService.createFeedback(request, principal.getName());
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.CREATED, "Feedback submitted successfully", feedback),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeedbackDTO>> updateFeedback(@PathVariable Long id,
            @RequestBody FeedbackRequestDTO request, Principal principal) {
        FeedbackDTO feedback = feedbackService.updateFeedback(id, request, principal.getName());
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "Feedback updated successfully", feedback),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeedbackDTO>> getFeedbackById(@PathVariable Long id) {
        FeedbackDTO feedback = feedbackService.getFeedbackById(id);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "Feedback fetched successfully", feedback),
                HttpStatus.OK);
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<FeedbackDTO>> getFeedbackByConsultationId(@PathVariable Long consultationId) {
        FeedbackDTO feedback = feedbackService.getFeedbackByConsultationId(consultationId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "Feedback fetched successfully", feedback),
                HttpStatus.OK);
    }

    @GetMapping("/consultation/{consultationId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> hasFeedback(@PathVariable Long consultationId) {
        boolean exists = feedbackService.hasFeedback(consultationId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "Check successful", exists), HttpStatus.OK);
    }

    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<ApiResponse<List<FeedbackDTO>>> getFeedbackByConsultantId(@PathVariable Long consultantId) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbackByConsultantId(consultantId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "Feedbacks fetched successfully", feedbacks),
                HttpStatus.OK);
    }

    @GetMapping("/consultant/{consultantId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConsultantStats(@PathVariable Long consultantId) {
        Map<String, Object> stats = feedbackService.getConsultantFeedbackStats(consultantId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "Stats fetched successfully", stats),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackDTO>>> getAllFeedback() {
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedback();
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK, "All feedbacks fetched successfully", feedbacks),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.NO_CONTENT, "Feedback deleted successfully"),
                HttpStatus.NO_CONTENT);
    }
}
