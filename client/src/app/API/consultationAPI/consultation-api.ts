import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ConsultationApi {
  private baseUrl: string = 'http://localhost:8080/api/v1/consultations';

  constructor(private http: HttpClient) {}

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  public setAuthToken(token: string): void {
    this.httpOptions.headers = this.httpOptions.headers.set('Authorization', `Bearer ${token}`);
  }

  // ==================== Consultation Retrieval Methods ====================

  /**
   * Get consultation by ID
   * GET /api/v1/consultations/{id}
   */
  getConsultationById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`, this.httpOptions);
  }

  /**
   * Get all consultations for a farmer
   * GET /api/v1/consultations/farmer/{farmerId}
   */
  getConsultationsByFarmerId(farmerId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/farmer/${farmerId}`, this.httpOptions);
  }

  /**
   * Get all consultations for a consultant
   * GET /api/v1/consultations/consultant/{consultantId}
   */
  getConsultationsByConsultantId(consultantId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultant/${consultantId}`, this.httpOptions);
  }

  /**
   * Get consultations by crop
   * GET /api/v1/consultations/crop/{cropId}
   */
  getConsultationsByCropId(cropId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/crop/${cropId}`, this.httpOptions);
  }

  /**
   * Get top 10 recent consultations
   * GET /api/v1/consultations/recent
   */
  getRecentConsultations(): Observable<any> {
    return this.http.get(`${this.baseUrl}/recent`, this.httpOptions);
  }

  // ==================== Status-Based Retrieval Methods ====================

  /**
   * Get consultations by status
   * GET /api/v1/consultations/status/{status}
   */
  getConsultationsByStatus(status: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/status/${status}`, this.httpOptions);
  }

  /**
   * Get farmer's consultations by status
   * GET /api/v1/consultations/farmer/{farmerId}/status/{status}
   */
  getConsultationsByFarmerAndStatus(farmerId: number, status: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/farmer/${farmerId}/status/${status}`, this.httpOptions);
  }

  /**
   * Get consultant's consultations by status
   * GET /api/v1/consultations/consultant/{consultantId}/status/{status}
   */
  getConsultationsByConsultantAndStatus(consultantId: number, status: string): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/consultant/${consultantId}/status/${status}`,
      this.httpOptions
    );
  }

  /**
   * Get consultations with multiple statuses
   * GET /api/v1/consultations/status/multiple?statuses=PENDING,APPROVED
   */
  getConsultationsByMultipleStatuses(statuses: string[]): Observable<any> {
    const params = new HttpParams().set('statuses', statuses.join(','));
    return this.http.get(`${this.baseUrl}/status/multiple`, { ...this.httpOptions, params });
  }

  /**
   * Get farmer's consultations with multiple statuses
   * GET /api/v1/consultations/farmer/{farmerId}/status/multiple?statuses=PENDING,APPROVED
   */
  getConsultationsByFarmerAndMultipleStatuses(
    farmerId: number,
    statuses: string[]
  ): Observable<any> {
    const params = new HttpParams().set('statuses', statuses.join(','));
    return this.http.get(`${this.baseUrl}/farmer/${farmerId}/status/multiple`, {
      ...this.httpOptions,
      params,
    });
  }

  /**
   * Get consultant's consultations with multiple statuses
   * GET /api/v1/consultations/consultant/{consultantId}/status/multiple?statuses=PENDING,APPROVED
   */
  getConsultationsByConsultantAndMultipleStatuses(
    consultantId: number,
    statuses: string[]
  ): Observable<any> {
    const params = new HttpParams().set('statuses', statuses.join(','));
    return this.http.get(`${this.baseUrl}/consultant/${consultantId}/status/multiple`, {
      ...this.httpOptions,
      params,
    });
  }

  // ==================== Date-Based Retrieval Methods ====================

  /**
   * Get consultations within a date range
   * GET /api/v1/consultations/date-range?startDate=...&endDate=...
   */
  getConsultationsByDateRange(startDate: string, endDate: string): Observable<any> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get(`${this.baseUrl}/date-range`, { ...this.httpOptions, params });
  }

  /**
   * Get consultations created after a specific date
   * GET /api/v1/consultations/after-date?date=...
   */
  getConsultationsAfterDate(date: string): Observable<any> {
    const params = new HttpParams().set('date', date);
    return this.http.get(`${this.baseUrl}/after-date`, { ...this.httpOptions, params });
  }

  /**
   * Get consultations by status created after a specific date
   * GET /api/v1/consultations/status/{status}/after-date?date=...
   */
  getConsultationsByStatusAndDateAfter(status: string, date: string): Observable<any> {
    const params = new HttpParams().set('date', date);
    return this.http.get(`${this.baseUrl}/status/${status}/after-date`, {
      ...this.httpOptions,
      params,
    });
  }

  // ==================== Status Management Methods ====================

  /**
   * Approve a consultation
   * PUT /api/v1/consultations/{id}/approve
   */
  approveConsultation(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/approve`, null, this.httpOptions);
  }

  /**
   * Reject a consultation
   * PUT /api/v1/consultations/{id}/reject
   */
  rejectConsultation(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/reject`, null, this.httpOptions);
  }

  /**
   * Complete a consultation
   * PUT /api/v1/consultations/{id}/complete
   */
  completeConsultation(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/complete`, null, this.httpOptions);
  }

  /**
   * Update consultation status
   * PUT /api/v1/consultations/{id}/status
   */
  updateConsultationStatus(id: number, status: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/status`, { status }, this.httpOptions);
  }

  // ==================== Update Methods ====================

  /**
   * Update consultation topic and description
   * PUT /api/v1/consultations/{id}
   */
  updateConsultation(id: number, topic: string, description: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, { topic, description }, this.httpOptions);
  }

  // ==================== Statistics Methods ====================

  /**
   * Get total consultation count for farmer
   * GET /api/v1/consultations/stats/farmer/{farmerId}/count
   */
  getFarmerConsultationCount(farmerId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats/farmer/${farmerId}/count`, this.httpOptions);
  }

  /**
   * Get total consultation count for consultant
   * GET /api/v1/consultations/stats/consultant/{consultantId}/count
   */
  getConsultantConsultationCount(consultantId: number): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/stats/consultant/${consultantId}/count`,
      this.httpOptions
    );
  }

  /**
   * Get consultation count by status
   * GET /api/v1/consultations/stats/status/{status}/count
   */
  getConsultationCountByStatus(status: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats/status/${status}/count`, this.httpOptions);
  }

  /**
   * Get consultant's consultation count by status
   * GET /api/v1/consultations/stats/consultant/{consultantId}/status/{status}/count
   */
  getConsultantConsultationCountByStatus(consultantId: number, status: string): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/stats/consultant/${consultantId}/status/${status}/count`,
      this.httpOptions
    );
  }

  /**
   * Get farmer's consultation count by status
   * GET /api/v1/consultations/stats/farmer/{farmerId}/status/{status}/count
   */
  getFarmerConsultationCountByStatus(farmerId: number, status: string): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/stats/farmer/${farmerId}/status/${status}/count`,
      this.httpOptions
    );
  }

  /**
   * Get consultation count by crop
   * GET /api/v1/consultations/stats/crop/{cropId}/count
   */
  getConsultationCountByCrop(cropId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats/crop/${cropId}/count`, this.httpOptions);
  }

  /**
   * Get consultation count after specific date
   * GET /api/v1/consultations/stats/after-date/count?date=...
   */
  getConsultationCountAfterDate(date: string): Observable<any> {
    const params = new HttpParams().set('date', date);
    return this.http.get(`${this.baseUrl}/stats/after-date/count`, { ...this.httpOptions, params });
  }

  // ==================== Validation Methods ====================

  /**
   * Check if active consultation exists between farmer and consultant
   * GET /api/v1/consultations/validate/active?farmerId=...&consultantId=...
   */
  hasActiveConsultation(farmerId: number, consultantId: number): Observable<any> {
    const params = new HttpParams()
      .set('farmerId', farmerId.toString())
      .set('consultantId', consultantId.toString());
    return this.http.get(`${this.baseUrl}/validate/active`, { ...this.httpOptions, params });
  }

  /**
   * Validate if consultation can be created
   * GET /api/v1/consultations/validate/can-create?farmerId=...&consultantId=...
   */
  canCreateConsultation(farmerId: number, consultantId: number): Observable<any> {
    const params = new HttpParams()
      .set('farmerId', farmerId.toString())
      .set('consultantId', consultantId.toString());
    return this.http.get(`${this.baseUrl}/validate/can-create`, { ...this.httpOptions, params });
  }

  // ==================== Delete Method ====================

  /**
   * Delete a consultation
   * DELETE /api/v1/consultations/{id}
   */
  deleteConsultation(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`, this.httpOptions);
  }
}
