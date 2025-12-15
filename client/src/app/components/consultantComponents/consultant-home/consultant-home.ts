import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { ConsultationService } from '../../../services/consultationService/consultation-service';
import { ConsultantService } from '../../../services/consultantService/consultant-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-consultant-home',
  imports: [CommonModule],
  templateUrl: './consultant-home.html',
  styleUrl: './consultant-home.css',
})
export class ConsultantHome implements OnInit, OnDestroy {
  today = new Date();
  consultantProfile: any = null;
  consultantId: number | null = null;
  editMode: boolean = false;
  isLoading: boolean = true;
  private subscriptions: Subscription[] = [];

  // Statistics
  totalConsultations: number = 0;
  pendingCount: number = 0;
  approvedCount: number = 0;
  completedCount: number = 0;
  todaysPendingCount: number = 0;

  summaryCards = [
    {
      title: 'Total Consultation',
      value: '0',
      sub: '',
      color: 'border-green-400',
    },
    {
      title: 'Pending Requests',
      value: '0',
      sub: '0 new today',
      color: 'border-blue-400',
    },
    {
      title: 'Approved',
      value: '0',
      sub: 'Active consultations',
      color: 'border-yellow-400',
    },
    {
      title: 'Completed',
      value: '0',
      sub: 'Successfully finished',
      color: 'border-green-500',
    },
  ];

  // consultations - will be populated from API
  consultationRequests: any[] = [];

  constructor(
    private consultationService: ConsultationService,
    private consultantService: ConsultantService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    console.log('🏥 ConsultantHome Constructor Initialized');
  }

  ngOnInit() {
    console.log('📌 ngOnInit() called — initializing consultant dashboard');
    this.loadConsultantData();
  }

  /**
   * Load all consultant data
   */
  loadConsultantData() {
    this.isLoading = true;

    // First, get consultant profile to get the ID
    const profileSub = this.consultantService.getConsultantProfile().subscribe({
      next: (profile: any) => {
        console.log('🟢 Received consultant profile:', profile);
        this.consultantProfile = profile;

        if (profile && profile.id) {
          this.consultantId = profile.id;
          console.log('✅ Consultant ID:', this.consultantId);

          // Now load consultations and statistics
          this.loadConsultationData();
          this.loadStatistics();
        } else {
          console.warn('⚠️ No consultant ID found in profile');
          this.isLoading = false;
        }

        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('❌ Error fetching consultant profile:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.subscriptions.push(profileSub);
  }

  /**
   * Load consultation requests
   */
  loadConsultationData() {
    console.log('🔵 Fetching consultation requests...');
    const consultationSub = this.consultationService.getConsultationRequests().subscribe({
      next: (state: any) => {
        console.log('🟢 Received consultation requests from service:', state);

        if (state && Array.isArray(state)) {
          // Sort by date - newest first
          this.consultationRequests = this.sortByNewest(state);
          console.log('✅ Sorted consultation requests (newest first):', this.consultationRequests);

          // Calculate today's pending count
          this.calculateTodaysPending();
        } else {
          this.consultationRequests = [];
          console.warn('⚠️ No consultation requests or invalid data format');
        }

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('❌ Error fetching consultation requests:', err);
        this.consultationRequests = [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.subscriptions.push(consultationSub);
  }

  /**
   * Load statistics from API
   */
  loadStatistics() {
    if (!this.consultantId) {
      console.warn('⚠️ Cannot load statistics without consultant ID');
      return;
    }

    console.log('🔵 Fetching consultation statistics...');

    // Get total count
    const totalSub = this.consultationService
      .getConsultantConsultationCount(this.consultantId)
      .subscribe({
        next: (response: any) => {
          this.totalConsultations = response.data || 0;
          this.updateSummaryCard('Total Consultation', this.totalConsultations.toString());
          console.log('✅ Total consultations:', this.totalConsultations);
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error('❌ Error fetching total count:', err),
      });

    // Get pending count
    const pendingSub = this.consultationService
      .getConsultationsByConsultantAndStatus(this.consultantId, 'PENDING')
      .subscribe({
        next: (response: any) => {
          const pendingRequests = response.data || [];
          this.pendingCount = Array.isArray(pendingRequests) ? pendingRequests.length : 0;
          this.updateSummaryCard('Pending Requests', this.pendingCount.toString());
          console.log('✅ Pending consultations:', this.pendingCount);
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error('❌ Error fetching pending count:', err),
      });

    // Get approved count
    const approvedSub = this.consultationService
      .getConsultationsByConsultantAndStatus(this.consultantId, 'APPROVED')
      .subscribe({
        next: (response: any) => {
          const approvedRequests = response.data || [];
          this.approvedCount = Array.isArray(approvedRequests) ? approvedRequests.length : 0;
          this.updateSummaryCard('Approved', this.approvedCount.toString());
          console.log('✅ Approved consultations:', this.approvedCount);
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error('❌ Error fetching approved count:', err),
      });

    // Get completed count
    const completedSub = this.consultationService
      .getConsultationsByConsultantAndStatus(this.consultantId, 'COMPLETED')
      .subscribe({
        next: (response: any) => {
          const completedRequests = response.data || [];
          this.completedCount = Array.isArray(completedRequests) ? completedRequests.length : 0;
          this.updateSummaryCard('Completed', this.completedCount.toString());
          console.log('✅ Completed consultations:', this.completedCount);
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error('❌ Error fetching completed count:', err),
      });

    this.subscriptions.push(totalSub, pendingSub, approvedSub, completedSub);
  }

  /**
   * Calculate today's pending consultations
   */
  calculateTodaysPending() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    this.todaysPendingCount = this.consultationRequests.filter((request) => {
      if (request.consultationRequestStatus !== 'PENDING') return false;

      const requestDate = this.parseDate(request.createdAt || request.date || request.requestDate);
      requestDate.setHours(0, 0, 0, 0);

      return requestDate.getTime() === today.getTime();
    }).length;

    // Update the pending card subtitle
    const pendingCard = this.summaryCards.find((card) => card.title === 'Pending Requests');
    if (pendingCard) {
      pendingCard.sub =
        this.todaysPendingCount > 0
          ? `${this.todaysPendingCount} new today`
          : 'No new requests today';
    }

    console.log("✅ Today's pending count:", this.todaysPendingCount);
  }

  /**
   * Update summary card value
   */
  updateSummaryCard(title: string, value: string) {
    const card = this.summaryCards.find((c) => c.title === title);
    if (card) {
      card.value = value;
    }
  }

  /**
   * Sort consultations by date - newest first
   */
  sortByNewest(consultations: any[]): any[] {
    return consultations.sort((a, b) => {
      // Try to parse dates from different possible fields
      const dateA = this.parseDate(a.createdAt || a.date || a.requestDate);
      const dateB = this.parseDate(b.createdAt || b.date || b.requestDate);

      // Sort descending (newest first)
      return dateB.getTime() - dateA.getTime();
    });
  }

  /**
   * Parse date from various formats
   */
  parseDate(dateValue: any): Date {
    if (!dateValue) {
      return new Date(0); // Return epoch if no date
    }

    if (dateValue instanceof Date) {
      return dateValue;
    }

    // Try to parse string date
    const parsed = new Date(dateValue);
    return isNaN(parsed.getTime()) ? new Date(0) : parsed;
  }

  /**
   * Navigate to view all consultations
   */
  viewAllConsultations() {
    console.log('Navigating to all consultations');
    this.router.navigate(['/consultant/consultation-request']);
  }

  /**
   * Navigate to consultation details
   */
  viewConsultationDetails(consultation: any) {
    console.log('Viewing consultation details:', consultation);
    if (consultation && consultation.id) {
      this.router.navigate(['/consultant/consultation-details', consultation.id]);
    }
  }

  ngOnDestroy() {
    // Unsubscribe from all subscriptions
    this.subscriptions.forEach((sub) => {
      if (sub) {
        sub.unsubscribe();
      }
    });
    console.log('🧹 All subscriptions cleaned up in ngOnDestroy()');
  }
}
