import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ConsultantService } from '../../../services/consultantService/consultant-service';
import { FeedbackService } from '../../../services/feedbackService/feedback-service';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-consultant',
  imports: [CommonModule],
  templateUrl: './consultant.html',
  styleUrl: './consultant.css',
})
export class Consultant implements OnInit, OnDestroy {
  consultants: any[] = [];
  filteredConsultants: any[] = [];
  isLoading = true;
  searchTerm = '';
  private subscription!: Subscription;

  constructor(
    private consultantService: ConsultantService,
    private feedbackService: FeedbackService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadConsultants();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  loadConsultants(): void {
    this.isLoading = true;
    this.subscription = this.consultantService.getVerifiedConsultants().subscribe((state: any) => {
      console.log('Consultants loaded:', state);

      if (Array.isArray(state) && state.length > 0) {
        this.consultants = state;
        // Load ratings for all consultants
        this.loadConsultantRatings();
      } else {
        this.consultants = [];
        this.filteredConsultants = [];
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadConsultantRatings(): void {
    // Create an array of observables to fetch feedback for each consultant
    // Use catchError to handle individual failures without breaking forkJoin
    const feedbackRequests = this.consultants.map((consultant) =>
      this.feedbackService.getConsultantStats(consultant.id).pipe(
        catchError((error: any) => {
          console.warn(`Failed to load stats for consultant ${consultant.id}:`, error);
          // Return default stats if this consultant's stats fail
          return of({ data: { averageRating: 0, totalFeedbacks: 0 } });
        })
      )
    );

    // Execute all requests in parallel
    forkJoin(feedbackRequests).subscribe({
      next: (results: any[]) => {
        // Update each consultant with their rating data
        this.consultants = this.consultants.map((consultant, index) => {
          const stats = results[index];
          const data = stats?.data || stats;

          return {
            ...consultant,
            rating: data?.averageRating || 0,
            reviewCount: data?.totalFeedbacks || 0,
            experience: consultant.experience || 0,
          };
        });

        this.filteredConsultants = this.consultants;
        console.log('Consultants with ratings:', this.consultants);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading consultant ratings:', error);
        // If stats API fails, use consultants without ratings
        this.consultants = this.consultants.map((consultant) => ({
          ...consultant,
          rating: 0,
          reviewCount: 0,
          experience: consultant.experience || 0,
        }));
        this.filteredConsultants = this.consultants;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onSearch(event: any): void {
    this.searchTerm = event.target.value.toLowerCase();
    this.filteredConsultants = this.consultants.filter((consultant) => {
      const fullName = `${consultant.firstName} ${consultant.lastName}`.toLowerCase();
      const email = consultant.email?.toLowerCase() || '';
      const specialization = consultant.specialization?.toLowerCase() || '';

      return (
        fullName.includes(this.searchTerm) ||
        email.includes(this.searchTerm) ||
        specialization.includes(this.searchTerm)
      );
    });
  }

  handleCardAction(event: { action: string; user: any }): void {
    console.log('Card action:', event);

    if (event.action === 'view') {
      // Navigate to consultant profile or details
      this.router.navigate(['/farmer/consultant/profile', event.user.id]);
    } else if (event.action === 'request') {
      // Navigate to consultation request form with consultant pre-selected
      this.router.navigate(['/farmer/consultation-request'], {
        queryParams: { consultantId: event.user.id },
      });
    }
  }
}
