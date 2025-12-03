package com.server.dto;

public class RatingRequest {
	  private Long feedbackId;
	    private int stars;
		public Long getFeedbackId() {
			return feedbackId;
		}
		public void setFeedbackId(Long feedbackId) {
			this.feedbackId = feedbackId;
		}
		public int getStars() {
			return stars;
		}
		public void setStars(int stars) {
			this.stars = stars;
		}
		public RatingRequest(Long feedbackId, int stars) {
			super();
			this.feedbackId = feedbackId;
			this.stars = stars;
		}
	    
}
