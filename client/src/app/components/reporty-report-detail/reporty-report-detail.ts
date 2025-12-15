import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConsultationReportService } from '../../services/consultationReport/consultation-report-service';

@Component({
  selector: 'app-reporty-report-detail',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reporty-report-detail.html',
  styleUrl: './reporty-report-detail.css',
})
export class ReportyReportDetail implements OnInit, OnDestroy {
  report: any = null;
  isLoading = true;
  isEditing = false;
  isConsultant = false;
  reportForm!: FormGroup;
  reportId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private fb: FormBuilder,
    private reportService: ConsultationReportService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Detect user role from sessionStorage
    const userRole = sessionStorage.getItem('role');
    this.isConsultant = userRole === 'CONSULTANT';

    // Initialize form
    this.reportForm = this.fb.group({
      identifiedIssue: ['', Validators.required],
      recommendations: ['', Validators.required],
      reportText: ['', Validators.required],
    });

    // Get report ID from route
    this.route.paramMap.subscribe((params) => {
      const idStr = params.get('reportId');
      if (idStr) {
        this.reportId = Number(idStr);
        this.loadReportDetails();
      }
    });
  }

  loadReportDetails(): void {
    this.isLoading = true;
    this.reportService.getReportById(this.reportId).subscribe({
      next: (res: any) => {
        console.log('✅ Report loaded:', res);
        this.report = res.data;

        // Populate form with report data
        if (this.report) {
          this.reportForm.patchValue({
            identifiedIssue: this.report.identifiedIssue,
            recommendations: this.report.recommendations,
            reportText: this.report.reportText,
          });
        }

        this.isLoading = false;
        this.cdr.detectChanges(); // Manually trigger change detection
      },
      error: (err: any) => {
        console.error('❌ Error loading report:', err);
        this.isLoading = false;
        this.cdr.detectChanges(); // Manually trigger change detection
        alert('Failed to load report details');
      },
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      // Reset form to original values if canceling
      this.reportForm.patchValue({
        identifiedIssue: this.report.identifiedIssue,
        recommendations: this.report.recommendations,
        reportText: this.report.reportText,
      });
    }
  }

  updateReport(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    // Auto-generate followUpDate: 7 days from now
    const followUpDate = new Date();
    followUpDate.setDate(followUpDate.getDate() + 7);
    followUpDate.setHours(10, 0, 0, 0);

    const year = followUpDate.getFullYear();
    const month = String(followUpDate.getMonth() + 1).padStart(2, '0');
    const day = String(followUpDate.getDate()).padStart(2, '0');
    const hours = String(followUpDate.getHours()).padStart(2, '0');
    const minutes = String(followUpDate.getMinutes()).padStart(2, '0');
    const seconds = String(followUpDate.getSeconds()).padStart(2, '0');
    const followUpDateISO = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;

    const reportData = {
      consultationId: this.report.consultationId,
      identifiedIssue: this.reportForm.value.identifiedIssue,
      recommendations: this.reportForm.value.recommendations,
      reportText: this.reportForm.value.reportText,
      followUpDate: followUpDateISO,
    };

    console.log('🔵 Updating report:', reportData);

    this.reportService.updateReport(this.reportId, reportData).subscribe({
      next: (res: any) => {
        console.log('✅ Report updated successfully:', res);
        alert('Report updated successfully!');
        this.isEditing = false;
        this.loadReportDetails(); // Reload to show updated data
      },
      error: (err: any) => {
        console.error('❌ Error updating report:', err);
        alert('Failed to update report. Please try again.');
      },
    });
  }

  deleteReport(): void {
    if (!confirm('Are you sure you want to delete this report? This action cannot be undone.')) {
      return;
    }

    this.reportService.deleteReport(this.reportId, this.report.consultationId).subscribe({
      next: (res: any) => {
        console.log('✅ Report deleted successfully:', res);
        alert('Report deleted successfully!');
        this.goBack();
      },
      error: (err: any) => {
        console.error('❌ Error deleting report:', err);
        alert('Failed to delete report. Please try again.');
      },
    });
  }

  goBack(): void {
    this.location.back();
  }

  ngOnDestroy(): void {
    // Cleanup if needed
  }
}
