package com.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "consultation_id", unique = true)
    @JsonBackReference(value = "consultation-feedback")
    private Consultation consultation;
    
    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;
    
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    
    @Column(columnDefinition = "TEXT")
    private String feedbackText;
    
    // Rating Categories (1-5)
    @Column(nullable = false)
    private Integer ratingCommunication;
    
    @Column(nullable = false)
    private Integer ratingExpertise;
    
    @Column(nullable = false)
    private Integer ratingTimeliness;
    
    @Column(nullable = false)
    private Double ratingOverall; // Average of the 3
    
    private Boolean isAnonymous = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateOverallRating();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateOverallRating();
    }
    
    private void calculateOverallRating() {
        if (ratingCommunication != null && ratingExpertise != null && ratingTimeliness != null) {
            this.ratingOverall = (ratingCommunication + ratingExpertise + ratingTimeliness) / 3.0;
        }
    }
}
