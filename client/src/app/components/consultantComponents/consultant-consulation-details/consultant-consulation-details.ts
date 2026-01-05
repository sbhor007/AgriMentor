import { CommonModule, Location } from '@angular/common';
import { Component, Input, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ConsultationService } from '../../../services/consultationService/consultation-service';
import { ConsultationReportService } from '../../../services/consultationReport/consultation-report-service';
import { FarmVisit } from '../../../services/farmVisit/farm-visit';

import { FarmVisitiongSchedule } from '../farm-visitiong-schedule/farm-visitiong-schedule';
import { FeedbackDisplayComponent } from '../feedback-display/feedback-display';
import { toast } from 'ngx-sonner';

// Custom validator to ensure date is in the future
function futureDateValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null; // Don't validate empty values (let required validator handle that)
  }

  const selectedDate = new Date(control.value);
  const today = new Date();
  today.setHours(0, 0, 0, 0); // Reset time to start of day for fair comparison

  if (selectedDate <= today) {
    return { pastDate: true };
  }

  return null;
}

@Component({
  selector: 'app-consultant-consulation-details',
  imports: [CommonModule, ReactiveFormsModule, FarmVisitiongSchedule, FeedbackDisplayComponent],
  templateUrl: './consultant-consulation-details.html',
  styleUrl: './consultant-consulation-details.css',
})
export class ConsultantConsulationDetails implements OnInit, OnDestroy {
  activeTab = 'details';
  consultation: any = null;
  isLoading = true;

  showAddVisitModal = false;
  showAddReportModal = false;
  showEditVisitModal = false;
  selectedVisit: any = null;

  reports: any[] = [];
  visits: any[] = [];

  reportForm!: FormGroup;
  minDate: string; // Minimum date for the date input

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private consultationService: ConsultationService,
    private reportService: ConsultationReportService,
    private farmVisitService: FarmVisit,
    private cdr: ChangeDetectorRef,
    private location: Location
  ) {
    // Set minimum date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minDate = tomorrow.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    // Initialize form without followUpDate (auto-generated on submit)
    this.reportForm = this.fb.group({
      identifiedIssue: ['', Validators.required],
      recommendations: ['', Validators.required],
      reportText: ['', Validators.required],
    });

    // Get ID from route and fetch data
    this.route.paramMap.subscribe((params) => {
      const idStr = params.get('id');
      if (idStr) {
        const id = Number(idStr);
        this.fetchConsultationDetails(id);
      }
    });
  }

  viewReportDetails(reportId: number): void {
    this.router.navigate(['/consultant/report', reportId]);
  }

  ngOnDestroy() {
    // Clean up report service state when leaving the component
    this.reportService.clearCurrentConsultationReports();
  }

  fetchConsultationDetails(id: number) {
    this.isLoading = true;

    // Subscribe to the BehaviorSubject observable for consultation data
    // Subscribe to the BehaviorSubject observable for consultation data
    this.consultationService.listConsultationData$.subscribe({
      next: (data) => {
        // If data is null/undefined, it might be initial state.
        // We trigger a fetch if we haven't already (logic for that should ideally be in service, but here we trigger it).
        // However, we shouldn't get stuck. If data is null, we request it.
        // We do NOT return here if we want to avoid getting stuck if the service never updates.
        // But assuming the service works, we wait for the update.
        if (!data) {
          this.consultationService.getConsultationRequests();
          return;
        }

        // Find the specific consultation from the list
        const found = data.find((c: any) => c.id === id);

        // Update state only if changed or initial load
        if (found) {
          this.consultation = found;

          // Load related data
          this.reportService.loadReportsByConsultationId(this.consultation.id);
          this.farmVisitService.getFarmVisitsByConsultationId(this.consultation.id);
        } else {
          // Handle case where id is not found in the loaded data
          console.warn(`Consultation with ID ${id} not found in loaded data.`);
          // Optionally redirect or show error, but for now just stop loading so user sees something (empty state)
        }

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('🔴 Error fetching consultation details:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    // Subscribe to reports state separately to avoid nesting complexity
    this.reportService.currentConsultationReports$.subscribe({
      next: (reports) => {
        if (reports) {
          this.reports = reports;
          this.cdr.detectChanges();
        }
      },
    });

    // Subscribe to farm visits state
    this.farmVisitService.farmVisits$.subscribe({
      next: (visits) => {
        if (visits) {
          this.visits = visits.sort((a, b) => {
            const dateA = new Date(a.scheduledDate).getTime();
            const dateB = new Date(b.scheduledDate).getTime();
            return dateA - dateB;
          });
          this.cdr.detectChanges();
        }
      },
    });
  }

  goBack() {
    this.location.back();
  }

  // open modals
  openAddVisit() {
    this.showAddVisitModal = true;
  }

  openAddReport() {
    this.showAddReportModal = true;
  }

  // close modals
  closeAddVisit() {
    this.showAddVisitModal = false;
  }

  closeAddReport() {
    this.showAddReportModal = false;
  }

  // when visit scheduled
  handleScheduledVisit(data: any) {
    console.log('🔵 handleScheduledVisit called with data:', data);
    this.showAddVisitModal = false;
    // The farm visit service already updates the state, so visits will be updated automatically
    this.cdr.detectChanges();
  }

  // Update visit status
  updateVisitStatus(visitId: number, status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED') {
    console.log('🔵 Updating visit status:', visitId, status);
    this.farmVisitService.updateVisitStatus(visitId, status).subscribe({
      next: () => {
        console.log('✅ Visit status updated successfully');
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('❌ Error updating visit status:', error);
      },
    });
  }

  // Complete visit
  completeVisit(visitId: number) {
    this.farmVisitService.completeFarmVisit(visitId).subscribe({
      next: () => {
        console.log('✅ Visit completed successfully');
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('❌ Error completing visit:', error);
      },
    });
  }

  // Cancel visit
  cancelVisit(visitId: number) {
    if (confirm('Are you sure you want to cancel this visit?')) {
      this.farmVisitService.cancelFarmVisit(visitId).subscribe({
        next: () => {
          console.log('✅ Visit cancelled successfully');
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('❌ Error cancelling visit:', error);
        },
      });
    }
  }

  // Delete visit
  deleteVisit(visitId: number) {
    if (confirm('Are you sure you want to delete this visit? This action cannot be undone.')) {
      this.farmVisitService.deleteFarmVisit(visitId).subscribe({
        next: () => {
          console.log('✅ Visit deleted successfully');
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('❌ Error deleting visit:', error);
        },
      });
    }
  }

  // Open edit visit modal
  openEditVisit(visit: any) {
    this.selectedVisit = visit;
    this.showEditVisitModal = true;
  }

  // Close edit visit modal
  closeEditVisit() {
    this.showEditVisitModal = false;
    this.selectedVisit = null;
  }

  // add report
  submitReport() {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    // Auto-generate followUpDate: 7 days from now in ISO format
    const followUpDate = new Date();
    followUpDate.setDate(followUpDate.getDate() + 7);
    followUpDate.setHours(10, 0, 0, 0); // Set to 10:00:00

    // Format as ISO string without timezone (YYYY-MM-DDTHH:mm:ss)
    const year = followUpDate.getFullYear();
    const month = String(followUpDate.getMonth() + 1).padStart(2, '0');
    const day = String(followUpDate.getDate()).padStart(2, '0');
    const hours = String(followUpDate.getHours()).padStart(2, '0');
    const minutes = String(followUpDate.getMinutes()).padStart(2, '0');
    const seconds = String(followUpDate.getSeconds()).padStart(2, '0');
    const followUpDateISO = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;

    // Prepare report data matching ConsultationReportRequestDTO
    const reportData = {
      consultationId: this.consultation.id,
      reportText: this.reportForm.value.reportText,
      recommendations: this.reportForm.value.recommendations,
      identifiedIssue: this.reportForm.value.identifiedIssue,
      followUpDate: followUpDateISO,
    };

    console.log('🔵 Submitting consultation report:', reportData);

    // Call the dedicated report service to save the report
    this.reportService.createReport(reportData).subscribe({
      next: (res: any) => {
        console.log('✅ Report created successfully:', res);

        // Refresh consultation data to get updated reports
        this.consultationService.getListConsultationRequestsData();

        toast.success('Report created successfully!');
        // Close modal and reset form
        this.showAddReportModal = false;
        this.reportForm.reset();
      },
      error: (err: any) => {
        console.error('❌ Error creating report:', err);
        // You can add user-friendly error notification here
        toast.error('Failed to create report. Please try again.');
      },
    });
  }

  // Helper method to check if there are any incomplete farm visits
  hasIncompleteFarmVisits(): boolean {
    return this.visits.some(
      (visit) => visit.visitStatus === 'SCHEDULED' || visit.visitStatus === 'MISSED'
    );
  }

  // Helper method to check if there are any pending farm visits (used in template)
  hasPendingFarmVisit(): boolean {
    return this.visits.some(
      (visit) => visit.visitStatus === 'SCHEDULED' || visit.visitStatus === 'MISSED'
    );
  }

  // Complete consultation with validation
  completeConsultation() {
    // Check if there are any incomplete farm visits
    if (this.hasIncompleteFarmVisits()) {
      const incompletVisits = this.visits.filter(
        (visit) => visit.visitStatus === 'SCHEDULED' || visit.visitStatus === 'MISSED'
      );

      toast.error(
        `Cannot complete consultation. You have ${incompletVisits.length} incomplete farm visit(s).\n\n` +
          `Please complete or cancel all scheduled/missed farm visits before marking the consultation as complete.\n\n` +
          `Incomplete visits:\n` +
          incompletVisits
            .map(
              (v, i) =>
                `${i + 1}. ${new Date(v.scheduledDate).toLocaleDateString()} - Status: ${
                  v.visitStatus
                }`
            )
            .join('\n')
      );
      return;
    }

    // Confirm completion
    if (!confirm('Are you sure you want to mark this consultation as completed?')) {
      return;
    }

    // Call the service to complete the consultation
    this.consultationService.completeConsultation(this.consultation.id).subscribe({
      next: (res: any) => {
        console.log('✅ Consultation completed successfully:', res);
        toast.success('Consultation has been marked as completed successfully!');

        // Refresh consultation data
        this.consultationService.getListConsultationRequestsData();
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('❌ Error completing consultation:', err);
        toast.error('Failed to complete consultation. Please try again.');
      },
    });
  }
}
