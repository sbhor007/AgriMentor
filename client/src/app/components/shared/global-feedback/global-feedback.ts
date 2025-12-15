import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { FeedbackService } from '../../../services/feedbackService/feedback-service';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-global-feedback',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './global-feedback.html',
})
export class GlobalFeedbackComponent implements OnInit {
  feedbacks: any[] = [];
  filteredFeedbacks: any[] = [];
  isLoading = true;

  // Filters
  searchTerm = new FormControl('');
  ratingFilter = new FormControl('all');

  constructor(private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    this.fetchFeedbacks();

    // Setup search
    this.searchTerm.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.applyFilters());

    // Setup rating filter
    this.ratingFilter.valueChanges.subscribe(() => this.applyFilters());
  }

  fetchFeedbacks() {
    this.isLoading = true;
    this.feedbackService.getAllFeedback().subscribe({
      next: (res: any) => {
        this.feedbacks = res.data || [];
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching feedbacks:', err);
        this.isLoading = false;
      },
    });
  }

  applyFilters() {
    let filtered = [...this.feedbacks];
    const search = (this.searchTerm.value || '').toLowerCase();
    const rating = this.ratingFilter.value;

    // Filter by Search (Farmer Name or Consultant Name)
    if (search) {
      filtered = filtered.filter(
        (f) =>
          (f.farmerName && f.farmerName.toLowerCase().includes(search)) ||
          (f.consultantName && f.consultantName.toLowerCase().includes(search))
      );
    }

    // Filter by Rating
    if (rating !== 'all') {
      const minRating = Number(rating);
      filtered = filtered.filter((f) => f.ratingOverall >= minRating);
    }

    // Sort by Date Descending
    filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

    this.filteredFeedbacks = filtered;
  }
}
