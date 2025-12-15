import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FeedbackService } from '../../../services/feedbackService/feedback-service';

@Component({
  selector: 'app-feedback-display',
  imports: [CommonModule],
  templateUrl: './feedback-display.html',
})
export class FeedbackDisplayComponent implements OnChanges {
  @Input() consultationId!: number;
  @Input() feedbackData: any = null; // Can pass data directly or fetch by ID

  feedback: any = null;
  isLoading = false;

  // Helper for stars
  stars = [1, 2, 3, 4, 5];

  constructor(private feedbackService: FeedbackService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['consultationId'] && this.consultationId) {
      if (!this.feedbackData) {
        this.fetchFeedback();
      } else {
        // Handle both response structures
        this.feedback = this.feedbackData.data || this.feedbackData;
      }
    }

    if (changes['feedbackData'] && this.feedbackData) {
      // Handle both response structures
      this.feedback = this.feedbackData.data || this.feedbackData;
    }

    // Subscribe to reactive feedback state for immediate updates
    this.feedbackService.feedback$.subscribe({
      next: (feedback) => {
        if (feedback && feedback.consultationId === this.consultationId) {
          this.feedback = feedback;
          this.isLoading = false;
        }
      },
    });
  }

  fetchFeedback() {
    this.isLoading = true;
    this.feedbackService.getFeedbackByConsultationId(this.consultationId).subscribe({
      next: (res: any) => {
        console.log('✅ Feedback fetched successfully', res);
        // Handle both response structures: res.data or direct response
        this.feedback = res.data || res;
        this.isLoading = false;
      },
      error: (err) => {
        console.log('⚠️ Feedback not found or error', err);
        this.feedback = null;
        this.isLoading = false;
      },
    });
  }
}
