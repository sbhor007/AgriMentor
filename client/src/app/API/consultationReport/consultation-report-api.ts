import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

/**
 * API Service for Consultation Report operations
 * Handles HTTP requests to the backend consultation-reports endpoints
 */
@Injectable({
  providedIn: 'root',
})
export class ConsultationReportApi {
  private baseUrl: string = 'http://localhost:8080/api/v1/consultation-reports';

  constructor(private http: HttpClient) {}

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  /**
   * Set the authentication token for API requests
   */
  public setAuthToken(token: string): void {
    this.httpOptions.headers = this.httpOptions.headers.set('Authorization', `Bearer ${token}`);
  }

  /**
   * Create a new consultation report
   * POST /api/v1/consultation-reports
   */
  createReport(reportData: any): Observable<any> {
    console.log('🔵 API: Creating consultation report:', reportData);
    return this.http.post(`${this.baseUrl}`, reportData);
  }

  /**
   * Update an existing consultation report
   * PUT /api/v1/consultation-reports/{reportId}
   */
  updateReport(reportId: number, reportData: any): Observable<any> {
    console.log('🔵 API: Updating consultation report ID:', reportId, reportData);
    return this.http.put(`${this.baseUrl}/${reportId}`, reportData);
  }

  /**
   * Get a consultation report by ID
   * GET /api/v1/consultation-reports/{reportId}
   */
  getReportById(reportId: number): Observable<any> {
    console.log('🔵 API: Fetching consultation report ID:', reportId);
    return this.http.get(`${this.baseUrl}/${reportId}`);
  }

  /**
   * Get all reports for a specific consultation
   * GET /api/v1/consultation-reports/consultation/{consultationId}
   */
  getReportsByConsultationId(consultationId: number): Observable<any> {
    console.log('🔵 API: Fetching reports for consultation ID:', consultationId);
    return this.http.get(`${this.baseUrl}/consultation/${consultationId}`);
  }

  /**
   * Get the latest report for a consultation
   * GET /api/v1/consultation-reports/consultation/{consultationId}/latest
   */
  getLatestReportByConsultationId(consultationId: number): Observable<any> {
    console.log('🔵 API: Fetching latest report for consultation ID:', consultationId);
    return this.http.get(`${this.baseUrl}/consultation/${consultationId}/latest`);
  }

  /**
   * Get all reports created by the authenticated consultant
   * GET /api/v1/consultation-reports/my-reports
   */
  getMyReports(): Observable<any> {
    console.log('🔵 API: Fetching my reports');
    return this.http.get(`${this.baseUrl}/my-reports`);
  }

  /**
   * Get upcoming follow-ups for the authenticated consultant
   * GET /api/v1/consultation-reports/upcoming-followups
   */
  getUpcomingFollowUps(): Observable<any> {
    console.log('🔵 API: Fetching upcoming follow-ups');
    return this.http.get(`${this.baseUrl}/upcoming-followups`);
  }

  /**
   * Get reports within a date range
   * GET /api/v1/consultation-reports/date-range?startDate=...&endDate=...
   */
  getReportsByDateRange(startDate: string, endDate: string): Observable<any> {
    console.log('🔵 API: Fetching reports by date range:', startDate, endDate);
    return this.http.get(
      `${this.baseUrl}/date-range?startDate=${startDate}&endDate=${endDate}`,
      this.httpOptions
    );
  }

  /**
   * Get report count for a consultation
   * GET /api/v1/consultation-reports/consultation/{consultationId}/count
   */
  getReportCount(consultationId: number): Observable<any> {
    console.log('🔵 API: Fetching report count for consultation ID:', consultationId);
    return this.http.get(`${this.baseUrl}/consultation/${consultationId}/count`);
  }

  /**
   * Delete a consultation report
   * DELETE /api/v1/consultation-reports/{reportId}
   */
  deleteReport(reportId: number): Observable<any> {
    console.log('🔵 API: Deleting consultation report ID:', reportId);
    return this.http.delete(`${this.baseUrl}/${reportId}`);
  }
}
