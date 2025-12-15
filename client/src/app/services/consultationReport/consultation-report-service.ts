import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ConsultationReportApi } from '../../API/consultationReport/consultation-report-api';

/**
 * Service for Consultation Report business logic
 * Manages state and coordinates API calls for consultation reports
 */
@Injectable({
  providedIn: 'root',
})
export class ConsultationReportService {
  // State management for reports list
  private reportsSubject = new BehaviorSubject<any[] | null>(null);
  public reports$ = this.reportsSubject.asObservable();

  // State management for current consultation's reports
  private currentConsultationReportsSubject = new BehaviorSubject<any[] | null>(null);
  public currentConsultationReports$ = this.currentConsultationReportsSubject.asObservable();

  // State management for upcoming follow-ups
  private upcomingFollowUpsSubject = new BehaviorSubject<any[] | null>(null);
  public upcomingFollowUps$ = this.upcomingFollowUpsSubject.asObservable();

  constructor(private api: ConsultationReportApi) {}

  /**
   * Create a new consultation report
   */
  createReport(reportData: any): Observable<any> {
    console.log('🟡 SERVICE: Creating consultation report:', reportData);

    return new Observable((observer) => {
      this.api.createReport(reportData).subscribe({
        next: (res: any) => {
          console.log('✅ SERVICE: Report created successfully:', res);

          // Refresh the reports for this consultation
          if (reportData.consultationId) {
            this.loadReportsByConsultationId(reportData.consultationId);
          }

          observer.next(res);
          observer.complete();
        },
        error: (err: any) => {
          console.error('❌ SERVICE: Error creating report:', err);
          observer.error(err);
        },
      });
    });
  }

  /**
   * Update an existing consultation report
   */
  updateReport(reportId: number, reportData: any): Observable<any> {
    console.log('🟡 SERVICE: Updating report ID:', reportId, reportData);

    return new Observable((observer) => {
      this.api.updateReport(reportId, reportData).subscribe({
        next: (res: any) => {
          console.log('✅ SERVICE: Report updated successfully:', res);

          // Refresh the reports for this consultation
          if (reportData.consultationId) {
            this.loadReportsByConsultationId(reportData.consultationId);
          }

          observer.next(res);
          observer.complete();
        },
        error: (err: any) => {
          console.error('❌ SERVICE: Error updating report:', err);
          observer.error(err);
        },
      });
    });
  }

  /**
   * Get a consultation report by ID
   */
  getReportById(reportId: number): Observable<any> {
    console.log('🟡 SERVICE: Fetching report ID:', reportId);
    return this.api.getReportById(reportId);
  }

  /**
   * Load reports for a specific consultation and update state
   */
  loadReportsByConsultationId(consultationId: number): void {
    console.log('🟡 SERVICE: Loading reports for consultation ID:', consultationId);

    this.api.getReportsByConsultationId(consultationId).subscribe({
      next: (res: any) => {
        console.log('✅ SERVICE: Reports loaded successfully:', res);
        const reports = res.data || [];
        this.currentConsultationReportsSubject.next(reports);
      },
      error: (err: any) => {
        console.error('❌ SERVICE: Error loading reports:', err);
        this.currentConsultationReportsSubject.next([]);
      },
    });
  }

  /**
   * Get reports for a specific consultation (returns Observable)
   */
  getReportsByConsultationId(consultationId: number): Observable<any> {
    console.log('🟡 SERVICE: Getting reports for consultation ID:', consultationId);
    return this.api.getReportsByConsultationId(consultationId);
  }

  /**
   * Get the latest report for a consultation
   */
  getLatestReportByConsultationId(consultationId: number): Observable<any> {
    console.log('🟡 SERVICE: Getting latest report for consultation ID:', consultationId);
    return this.api.getLatestReportByConsultationId(consultationId);
  }

  /**
   * Load all reports created by the authenticated consultant
   */
  loadMyReports(): void {
    console.log('🟡 SERVICE: Loading my reports');

    this.api.getMyReports().subscribe({
      next: (res: any) => {
        console.log('✅ SERVICE: My reports loaded successfully:', res);
        const reports = res.data || [];
        this.reportsSubject.next(reports);
      },
      error: (err: any) => {
        console.error('❌ SERVICE: Error loading my reports:', err);
        this.reportsSubject.next([]);
      },
    });
  }

  /**
   * Get all reports created by the authenticated consultant (returns Observable)
   */
  getMyReports(): Observable<any> {
    console.log('🟡 SERVICE: Getting my reports');
    return this.api.getMyReports();
  }

  /**
   * Load upcoming follow-ups for the authenticated consultant
   */
  loadUpcomingFollowUps(): void {
    console.log('🟡 SERVICE: Loading upcoming follow-ups');

    this.api.getUpcomingFollowUps().subscribe({
      next: (res: any) => {
        console.log('✅ SERVICE: Upcoming follow-ups loaded successfully:', res);
        const followUps = res.data || [];
        this.upcomingFollowUpsSubject.next(followUps);
      },
      error: (err: any) => {
        console.error('❌ SERVICE: Error loading upcoming follow-ups:', err);
        this.upcomingFollowUpsSubject.next([]);
      },
    });
  }

  /**
   * Get upcoming follow-ups (returns Observable)
   */
  getUpcomingFollowUps(): Observable<any> {
    console.log('🟡 SERVICE: Getting upcoming follow-ups');
    return this.api.getUpcomingFollowUps();
  }

  /**
   * Get reports within a date range
   */
  getReportsByDateRange(startDate: string, endDate: string): Observable<any> {
    console.log('🟡 SERVICE: Getting reports by date range:', startDate, endDate);
    return this.api.getReportsByDateRange(startDate, endDate);
  }

  /**
   * Get report count for a consultation
   */
  getReportCount(consultationId: number): Observable<any> {
    console.log('🟡 SERVICE: Getting report count for consultation ID:', consultationId);
    return this.api.getReportCount(consultationId);
  }

  /**
   * Delete a consultation report
   */
  deleteReport(reportId: number, consultationId?: number): Observable<any> {
    console.log('🟡 SERVICE: Deleting report ID:', reportId);

    return new Observable((observer) => {
      this.api.deleteReport(reportId).subscribe({
        next: (res: any) => {
          console.log('✅ SERVICE: Report deleted successfully:', res);

          // Refresh the reports for this consultation if consultationId provided
          if (consultationId) {
            this.loadReportsByConsultationId(consultationId);
          }

          observer.next(res);
          observer.complete();
        },
        error: (err: any) => {
          console.error('❌ SERVICE: Error deleting report:', err);
          observer.error(err);
        },
      });
    });
  }

  /**
   * Clear the current consultation reports state
   */
  clearCurrentConsultationReports(): void {
    this.currentConsultationReportsSubject.next(null);
  }

  /**
   * Clear all state
   */
  clearAllState(): void {
    this.reportsSubject.next(null);
    this.currentConsultationReportsSubject.next(null);
    this.upcomingFollowUpsSubject.next(null);
  }
}
