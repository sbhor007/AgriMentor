import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultantService } from '../../../services/consultantService/consultant-service';
import { FeedbackService } from '../../../services/feedbackService/feedback-service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-consultant-profile',
  imports: [CommonModule],
  templateUrl: './consultant-profile.html',
  styleUrl: './consultant-profile.css',
})
export class ConsultantProfile implements OnInit, OnDestroy {
  consultantId!: number;
  consultant: any = null;
  feedbacks: any[] = [];
  isLoading = true;
  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private consultantService: ConsultantService,
    private feedbackService: FeedbackService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Get consultant ID from route params
    this.route.params.subscribe((params) => {
      this.consultantId = +params['id'];
      if (this.consultantId) {
        this.loadConsultantProfile();
        // Don't call loadConsultantFeedback here - it will be called by loadConsultantStats
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  loadConsultantProfile(): void {
    console.log('🔄 loadConsultantProfile: Setting isLoading = true');
    this.isLoading = true;

    // Get all consultants and find the specific one
    const sub = this.consultantService.getVerifiedConsultants().subscribe({
      next: (consultants: any) => {
        console.log('✅ Consultants received:', consultants);
        if (Array.isArray(consultants)) {
          this.consultant = consultants.find((c) => c.id === this.consultantId);
          console.log('🔍 Found consultant:', this.consultant);

          if (this.consultant) {
            // Load rating stats
            console.log('📊 Calling loadConsultantStats...');
            this.loadConsultantStats();
          } else {
            console.error('❌ Consultant not found');
            console.log('🔄 Setting isLoading = false (consultant not found)');
            this.isLoading = false;
            this.cdr.detectChanges();
          }
        }
      },
      error: (error) => {
        console.error('❌ Error loading consultant:', error);
        console.log('🔄 Setting isLoading = false (error)');
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.subscriptions.push(sub);
  }

  loadConsultantStats(): void {
    console.log('📊 loadConsultantStats: Fetching stats for consultant', this.consultantId);
    const sub = this.feedbackService.getConsultantStats(this.consultantId).subscribe({
      next: (stats: any) => {
        console.log('✅ Stats received:', stats);
        const data = stats?.data || stats;

        this.consultant.rating = data?.averageRating || 0;
        this.consultant.reviewCount = data?.totalFeedbacks || 0;

        // Stats endpoint only gives us counts, not the actual feedback
        // So we need to load the feedback list separately
        console.log('📝 Stats loaded, now loading feedback list...');
        this.loadFeedbackList();
      },
      error: (error) => {
        console.error('❌ Error loading stats:', error);
        // If stats API returns 403 or fails, try to get feedback list instead
        if (error.status === 403 || error.status === 401) {
          console.log('🔐 Stats API forbidden (403/401), fetching feedback list instead...');
          this.loadFeedbackList();
        } else {
          console.log('⚠️ Other error, setting defaults');
          this.consultant.rating = 0;
          this.consultant.reviewCount = 0;
          console.log('🔄 Setting isLoading = false (stats error)');
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      },
    });

    this.subscriptions.push(sub);
  }

  loadFeedbackList(): void {
    console.log('📝 loadFeedbackList: Fetching feedback for consultant', this.consultantId);
    const sub = this.feedbackService.getFeedbackByConsultantId(this.consultantId).subscribe({
      next: (response: any) => {
        console.log('✅ Feedback response received:', response);
        console.log('📦 response.data:', response.data);
        console.log('📦 response type:', typeof response);
        console.log('📦 response.data type:', typeof response.data);

        // Handle different response structures
        let feedbackData;
        if (response.data) {
          feedbackData = response.data;
        } else if (Array.isArray(response)) {
          feedbackData = response;
        } else {
          feedbackData = [];
        }

        // Ensure it's an array
        this.feedbacks = Array.isArray(feedbackData) ? feedbackData : [];
        console.log('📋 Processed feedbacks:', this.feedbacks);
        console.log('📊 Feedbacks count:', this.feedbacks.length);

        // Calculate rating from feedback list
        if (this.feedbacks.length > 0) {
          const totalRating = this.feedbacks.reduce(
            (sum: number, fb: any) => sum + (fb.ratingOverall || 0),
            0
          );
          this.consultant.rating = totalRating / this.feedbacks.length;
          this.consultant.reviewCount = this.feedbacks.length;
          console.log(
            '⭐ Calculated rating:',
            this.consultant.rating,
            'from',
            this.feedbacks.length,
            'reviews'
          );
        } else {
          this.consultant.rating = 0;
          this.consultant.reviewCount = 0;
          console.log('⚠️ No feedbacks found');
        }

        console.log('🔄 Setting isLoading = false (feedback loaded)');
        this.isLoading = false;
        this.cdr.detectChanges();
        console.log('✅ Final isLoading state:', this.isLoading);
      },
      error: (error: any) => {
        console.error('❌ Error loading feedback list:', error);
        this.feedbacks = [];
        this.consultant.rating = 0;
        this.consultant.reviewCount = 0;
        console.log('🔄 Setting isLoading = false (feedback error)');
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.subscriptions.push(sub);
  }

  loadConsultantFeedback(): void {
    // Try to load feedback list directly
    this.loadFeedbackList();
  }

  requestConsultation(): void {
    this.router.navigate(['/farmer/consultation-request'], {
      queryParams: { consultantId: this.consultantId },
    });
  }

  goBack(): void {
    this.router.navigate(['/farmer/consultants']);
  }

  getStarArray(): number[] {
    return [1, 2, 3, 4, 5];
  }
}
