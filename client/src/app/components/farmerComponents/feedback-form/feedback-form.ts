import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FeedbackService } from '../../../services/feedbackService/feedback-service';

@Component({
  selector: 'app-feedback-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './feedback-form.html',
})
export class FeedbackFormComponent implements OnInit {
  @Input() consultationId!: number;
  @Input() existingFeedback: any = null;
  @Output() submitted = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  feedbackForm!: FormGroup;
  isLoading = false;
  stars = [1, 2, 3, 4, 5];

  constructor(private fb: FormBuilder, private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.feedbackForm = this.fb.group({
      ratingCommunication: [
        this.existingFeedback?.ratingCommunication || 0,
        [Validators.required, Validators.min(1)],
      ],
      ratingExpertise: [
        this.existingFeedback?.ratingExpertise || 0,
        [Validators.required, Validators.min(1)],
      ],
      ratingTimeliness: [
        this.existingFeedback?.ratingTimeliness || 0,
        [Validators.required, Validators.min(1)],
      ],
      feedbackText: [
        this.existingFeedback?.feedbackText || '',
        [Validators.required, Validators.minLength(10), Validators.maxLength(1000)],
      ],
      isAnonymous: [this.existingFeedback?.isAnonymous || false],
    });
  }

  setRating(category: string, rating: number) {
    this.feedbackForm.patchValue({ [category]: rating });
  }

  onSubmit() {
    if (this.feedbackForm.invalid) {
      this.feedbackForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const formData = {
      consultationId: this.consultationId,
      ...this.feedbackForm.value,
    };

    const request$ = this.existingFeedback
      ? this.feedbackService.updateFeedback(this.existingFeedback.id, formData)
      : this.feedbackService.submitFeedback(formData);

    request$.subscribe({
      next: () => {
        this.isLoading = false;
        this.submitted.emit();
        alert('Feedback submitted successfully!');
      },
      error: (err) => {
        console.error('Error submitting feedback:', err);
        this.isLoading = false;
        alert('Failed to submit feedback. Please try again.');
      },
    });
  }

  onCancel() {
    this.cancelled.emit();
  }
}
