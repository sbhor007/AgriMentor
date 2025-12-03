package com.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmerId;
    private Long consultantId;
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "feedback", cascade = CascadeType.ALL)
    private Rating rating;

	public Long getFarmerId() {
		return farmerId;
	}

	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}

	public Long getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Long consultantId) {
		this.consultantId = consultantId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Feedback(Long farmerId, Long consultantId, String comment, LocalDateTime createdAt, Rating rating) {
		super();
		this.farmerId = farmerId;
		this.consultantId = consultantId;
		this.comment = comment;
		this.createdAt = createdAt;
		this.rating = rating;
	}

	public Feedback() {
   }

  

}
