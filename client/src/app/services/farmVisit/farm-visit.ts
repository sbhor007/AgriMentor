import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import {
  FarmVisitingApi,
  FarmVisitRequest,
  FarmVisitResponse,
  ApiResponse,
} from '../../API/farmVisitingAPI/farm-visiting-api';
import { toast } from 'ngx-sonner';

/**
 * Farm Visit Service
 * Manages farm visit state and business logic
 */
@Injectable({
  providedIn: 'root',
})
export class FarmVisit {
  // State management with BehaviorSubjects
  private farmVisitsSubject = new BehaviorSubject<FarmVisitResponse[]>([]);
  farmVisits$ = this.farmVisitsSubject.asObservable();

  private currentVisitSubject = new BehaviorSubject<FarmVisitResponse | null>(null);
  currentVisit$ = this.currentVisitSubject.asObservable();

  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  constructor(private farmVisitApi: FarmVisitingApi) {}

  // ==================== Farm Visit Scheduling Methods ====================

  /**
   * Schedule a new farm visit for a consultation
   */
  scheduleFarmVisit(
    consultationId: number,
    visitData: FarmVisitRequest
  ): Observable<ApiResponse<FarmVisitResponse>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Scheduling farm visit for consultation:', consultationId);

    return this.farmVisitApi.scheduleFarmVisit(consultationId, visitData).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit scheduled successfully:', response.data);
        toast.success(response.message || 'Farm visit scheduled successfully');

        // Add to current visits list
        const currentVisits = this.farmVisitsSubject.value;
        this.farmVisitsSubject.next([...currentVisits, response.data]);

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error scheduling farm visit:', error);
        toast.error(error.error?.message || 'Failed to schedule farm visit');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  // ==================== Farm Visit Retrieval Methods ====================

  /**
   * Get all farm visits for a consultation
   */
  getFarmVisitsByConsultationId(consultationId: number): void {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Fetching farm visits for consultation:', consultationId);

    this.farmVisitApi.getFarmVisitsByConsultationId(consultationId).subscribe({
      next: (response) => {
        console.log('✅ Service: Farm visits fetched successfully:', response.data);
        this.farmVisitsSubject.next(response.data);
        this.isLoadingSubject.next(false);
      },
      error: (error) => {
        console.error('❌ Service: Error fetching farm visits:', error);
        toast.error(error.error?.message || 'Failed to fetch farm visits');
        this.farmVisitsSubject.next([]);
        this.isLoadingSubject.next(false);
      },
    });
  }

  /**
   * Get all farm visits for a consultation (alternative method)
   */
  getAllVisitsForConsultation(consultationId: number): void {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Fetching all farm visits for consultation:', consultationId);

    this.farmVisitApi.getAllVisitsForConsultation(consultationId).subscribe({
      next: (response) => {
        console.log('✅ Service: All farm visits fetched successfully:', response.data);
        this.farmVisitsSubject.next(response.data);
        this.isLoadingSubject.next(false);
      },
      error: (error) => {
        console.error('❌ Service: Error fetching all farm visits:', error);
        toast.error(error.error?.message || 'Failed to fetch farm visits');
        this.farmVisitsSubject.next([]);
        this.isLoadingSubject.next(false);
      },
    });
  }

  /**
   * Get a specific farm visit by ID
   */
  getFarmVisitById(visitId: number): void {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Fetching farm visit by ID:', visitId);

    this.farmVisitApi.getFarmVisitById(visitId).subscribe({
      next: (response) => {
        console.log('✅ Service: Farm visit fetched successfully:', response.data);
        this.currentVisitSubject.next(response.data);
        this.isLoadingSubject.next(false);
      },
      error: (error) => {
        console.error('❌ Service: Error fetching farm visit:', error);
        toast.error(error.error?.message || 'Failed to fetch farm visit');
        this.currentVisitSubject.next(null);
        this.isLoadingSubject.next(false);
      },
    });
  }

  // ==================== Farm Visit Update Methods ====================

  /**
   * Update farm visit status
   */
  updateVisitStatus(
    visitId: number,
    status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED'
  ): Observable<ApiResponse<FarmVisitResponse>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Updating farm visit status:', visitId, status);

    return this.farmVisitApi.updateVisitStatus(visitId, status).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit status updated successfully:', response.data);
        toast.success(response.message || 'Visit status updated successfully');

        // Update in the visits list
        this.updateVisitInList(response.data);

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error updating visit status:', error);
        toast.error(error.error?.message || 'Failed to update visit status');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  /**
   * Update farm visit notes
   */
  updateVisitNotes(visitId: number, notes: string): Observable<ApiResponse<FarmVisitResponse>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Updating farm visit notes:', visitId);

    return this.farmVisitApi.updateVisitNotes(visitId, notes).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit notes updated successfully:', response.data);
        toast.success(response.message || 'Visit notes updated successfully');

        // Update in the visits list
        this.updateVisitInList(response.data);

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error updating visit notes:', error);
        toast.error(error.error?.message || 'Failed to update visit notes');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  /**
   * Reschedule a farm visit
   */
  rescheduleFarmVisit(
    visitId: number,
    visitData: FarmVisitRequest
  ): Observable<ApiResponse<FarmVisitResponse>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Rescheduling farm visit:', visitId);

    return this.farmVisitApi.rescheduleFarmVisit(visitId, visitData).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit rescheduled successfully:', response.data);
        toast.success(response.message || 'Visit rescheduled successfully');

        // Update in the visits list
        this.updateVisitInList(response.data);

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error rescheduling visit:', error);
        toast.error(error.error?.message || 'Failed to reschedule visit');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  // ==================== Farm Visit Action Methods ====================

  /**
   * Cancel a farm visit
   */
  cancelFarmVisit(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Cancelling farm visit:', visitId);

    return this.farmVisitApi.cancelFarmVisit(visitId).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit cancelled successfully:', response.data);
        toast.success(response.message || 'Visit cancelled successfully');

        // Update in the visits list
        this.updateVisitInList(response.data);

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error cancelling visit:', error);
        toast.error(error.error?.message || 'Failed to cancel visit');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  /**
   * Complete a farm visit
   */
  completeFarmVisit(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Completing farm visit:', visitId);

    return this.farmVisitApi.completeFarmVisit(visitId).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit completed successfully:', response.data);
        toast.success(response.message || 'Visit completed successfully');

        // Update in the visits list
        this.updateVisitInList(response.data);

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error completing visit:', error);
        toast.error(error.error?.message || 'Failed to complete visit');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  /**
   * Delete a farm visit
   */
  deleteFarmVisit(visitId: number): Observable<ApiResponse<null>> {
    this.isLoadingSubject.next(true);
    console.log('🔵 Service: Deleting farm visit:', visitId);

    return this.farmVisitApi.deleteFarmVisit(visitId).pipe(
      tap((response) => {
        console.log('✅ Service: Farm visit deleted successfully');
        toast.success(response.message || 'Visit deleted successfully');

        // Remove from the visits list
        const currentVisits = this.farmVisitsSubject.value;
        this.farmVisitsSubject.next(currentVisits.filter((visit) => visit.id !== visitId));

        this.isLoadingSubject.next(false);
      }),
      catchError((error) => {
        console.error('❌ Service: Error deleting visit:', error);
        toast.error(error.error?.message || 'Failed to delete visit');
        this.isLoadingSubject.next(false);
        throw error;
      })
    );
  }

  // ==================== Utility Methods ====================

  /**
   * Update a visit in the current visits list
   */
  private updateVisitInList(updatedVisit: FarmVisitResponse): void {
    const currentVisits = this.farmVisitsSubject.value;
    const updatedVisits = currentVisits.map((visit) =>
      visit.id === updatedVisit.id ? updatedVisit : visit
    );
    this.farmVisitsSubject.next(updatedVisits);

    // Also update current visit if it matches
    if (this.currentVisitSubject.value?.id === updatedVisit.id) {
      this.currentVisitSubject.next(updatedVisit);
    }
  }

  /**
   * Clear all farm visits from state
   */
  clearFarmVisits(): void {
    this.farmVisitsSubject.next([]);
    this.currentVisitSubject.next(null);
  }

  /**
   * Get current farm visits value (synchronous)
   */
  getCurrentVisits(): FarmVisitResponse[] {
    return this.farmVisitsSubject.value;
  }

  /**
   * Get current visit value (synchronous)
   */
  getCurrentVisit(): FarmVisitResponse | null {
    return this.currentVisitSubject.value;
  }

  /**
   * Check if a visit is overdue
   */
  isVisitOverdue(visit: FarmVisitResponse): boolean {
    return visit.isOverdue;
  }

  /**
   * Filter visits by status
   */
  filterVisitsByStatus(
    status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED'
  ): FarmVisitResponse[] {
    return this.farmVisitsSubject.value.filter((visit) => visit.visitStatus === status);
  }

  /**
   * Get overdue visits
   */
  getOverdueVisits(): FarmVisitResponse[] {
    return this.farmVisitsSubject.value.filter((visit) => visit.isOverdue);
  }

  /**
   * Get all farm visits for the logged-in consultant
   */
  getAllConsultantVisits(): Observable<ApiResponse<FarmVisitResponse[]>> {
    console.log('🔵 Service: Fetching all farm visits for consultant');
    return this.farmVisitApi.getAllConsultantVisits();
  }

  /**
   * Complete a visit (simplified method for dashboard)
   */
  completeVisit(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    return this.completeFarmVisit(visitId);
  }

  /**
   * Cancel a visit (simplified method for dashboard)
   */
  cancelVisit(visitId: number): Observable<ApiResponse<FarmVisitResponse>> {
    return this.cancelFarmVisit(visitId);
  }
}
