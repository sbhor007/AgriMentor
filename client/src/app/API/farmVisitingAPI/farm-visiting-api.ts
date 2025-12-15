import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * TypeScript interfaces matching backend DTOs
 */
export interface FarmVisitRequest {
  scheduledDate: string; // ISO 8601 format
  visitNotes?: string;
  visitStatus?: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED';
}

export interface AddressDTO {
  street: string;
  city: string;
  state: string;
  pinCode: string;
  country: string;
  latitude?: string;
  longitude?: string;
}

export interface FarmVisitResponse {
  id: number;
  consultationId: number;
  consultationTopic: string;
  scheduledDate: string;
  visitNotes: string;
  visitStatus: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED';
  farmAddress: AddressDTO;
  createdAt: string;
  updatedAt: string;
  isOverdue: boolean;
}

export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}

/**
 * API Service for Farm Visit operations
 * Handles HTTP requests to the backend farm visit endpoints
 */
@Injectable({
  providedIn: 'root',
})
export class FarmVisitingApi {
  private baseUrl: string = 'http://localhost:8080/api/v1/farmvisits';

  constructor(private http: HttpClient) {}

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  // ==================== Farm Visit Scheduling Methods ====================

  /**
   * Schedule a new farm visit for a consultation
   * POST /api/v1/farmvisits/schedule/{consultationId}
   */
  scheduleFarmVisit(
    consultationId: number,
    visitData: FarmVisitRequest
  ): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Scheduling farm visit for consultation:', consultationId, visitData);
    return this.http.post<ApiResponse<FarmVisitResponse>>(
      `${this.baseUrl}/schedule/${consultationId}`,
      visitData
    );
  }

  // ==================== Farm Visit Retrieval Methods ====================

  /**
   * Get all farm visits for a consultation (with authorization check)
   * GET /api/v1/farmvisits/consultation/{consultationId}
   */
  getFarmVisitsByConsultationId(
    consultationId: number
  ): Observable<ApiResponse<FarmVisitResponse[]>> {
    console.log('🔵 API: Fetching farm visits for consultation:', consultationId);
    return this.http.get<ApiResponse<FarmVisitResponse[]>>(
      `${this.baseUrl}/consultation/${consultationId}`
    );
  }

  /**
   * Get all farm visits for a consultation (without authorization - for internal use)
   * GET /api/v1/farmvisits/consultation/{consultationId}/all
   */
  getAllVisitsForConsultation(
    consultationId: number
  ): Observable<ApiResponse<FarmVisitResponse[]>> {
    console.log('🔵 API: Fetching all farm visits for consultation:', consultationId);
    return this.http.get<ApiResponse<FarmVisitResponse[]>>(
      `${this.baseUrl}/consultation/${consultationId}/all`
    );
  }

  /**
   * Get a specific farm visit by ID
   * GET /api/v1/farmvisits/{visitId}
   */
  getFarmVisitById(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Fetching farm visit by ID:', visitId);
    return this.http.get<ApiResponse<FarmVisitResponse>>(`${this.baseUrl}/${visitId}`);
  }

  // ==================== Farm Visit Update Methods ====================

  /**
   * Update farm visit status
   * PATCH /api/v1/farmvisits/{visitId}/status?status=COMPLETED
   */
  updateVisitStatus(
    visitId: number,
    status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED'
  ): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Updating farm visit status:', visitId, status);
    const params = new HttpParams().set('status', status);
    return this.http.patch<ApiResponse<FarmVisitResponse>>(
      `${this.baseUrl}/${visitId}/status`,
      null,
      { params }
    );
  }

  /**
   * Update farm visit notes
   * PATCH /api/v1/farmvisits/{visitId}/notes
   */
  updateVisitNotes(visitId: number, notes: string): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Updating farm visit notes:', visitId);
    return this.http.patch<ApiResponse<FarmVisitResponse>>(
      `${this.baseUrl}/${visitId}/notes`,
      notes
    );
  }

  /**
   * Reschedule a farm visit
   * PATCH /api/v1/farmvisits/{visitId}/reschedule
   */
  rescheduleFarmVisit(
    visitId: number,
    visitData: FarmVisitRequest
  ): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Rescheduling farm visit:', visitId, visitData);
    return this.http.patch<ApiResponse<FarmVisitResponse>>(
      `${this.baseUrl}/${visitId}/reschedule`,
      visitData
    );
  }

  // ==================== Farm Visit Action Methods ====================

  /**
   * Cancel a farm visit
   * PUT /api/v1/farmvisits/{visitId}/cancel
   */
  cancelFarmVisit(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Cancelling farm visit:', visitId);
    return this.http.put<ApiResponse<FarmVisitResponse>>(`${this.baseUrl}/${visitId}/cancel`, null);
  }

  /**
   * Complete a farm visit
   * PUT /api/v1/farmvisits/{visitId}/complete
   */
  completeFarmVisit(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    console.log('🔵 API: Completing farm visit:', visitId);
    return this.http.put<ApiResponse<FarmVisitResponse>>(
      `${this.baseUrl}/${visitId}/complete`,
      null
    );
  }

  /**
   * Delete a farm visit
   * DELETE /api/v1/farmvisits/{visitId}
   */
  deleteFarmVisit(visitId: number): Observable<ApiResponse<null>> {
    console.log('🔵 API: Deleting farm visit:', visitId);
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/${visitId}`);
  }

  /**
   * Get all farm visits for the logged-in consultant
   * GET /api/v1/farmvisits/consultant/all
   */
  getAllConsultantVisits(): Observable<ApiResponse<FarmVisitResponse[]>> {
    console.log('🔵 API: Fetching all farm visits for consultant');
    return this.http.get<ApiResponse<FarmVisitResponse[]>>(`${this.baseUrl}/consultant/all`);
  }
}
