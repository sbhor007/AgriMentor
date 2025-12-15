import { Injectable } from '@angular/core';
import { ApiService } from '../../API/api-service';
import { ConsultationApi } from '../../API/consultationAPI/consultation-api';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ConsultationService {
  constructor(
    private api: ApiService,
    private consultationApi: ConsultationApi,
    private router: Router
  ) {}

  private listConsultationDataSubject = new BehaviorSubject<any | null>(null);
  listConsultationData$ = this.listConsultationDataSubject.asObservable();

  // ==================== Legacy Methods (Backward Compatibility) ====================

  getConsultationRequests() {
    if (!this.listConsultationDataSubject.getValue()) {
      this.getListConsultationRequestsData();
    }
    console.log('getConsultationRequests');
    console.log(this.listConsultationDataSubject.getValue());
    return this.listConsultationData$;
  }

  getListConsultationRequestsData() {
    console.log('🔵 Fetching consultation requests from API...');
    this.api.getConsultationRequestsByConsultant().subscribe({
      next: (res: any) => {
        console.log('✅ CONSULTANT::getListConsultationRequestsData::SUCCESS: ', res);
        this.listConsultationDataSubject.next(res.data);
      },
      error: (err: any) => {
        console.error('❌ CONSULTANT::getListConsultationRequestsData::ERROR: ', err);
      },
    });
  }

  acceptConsultationRequest(consultationId: any) {
    console.log('🟡 Accepting consultation request:', consultationId);
    this.api.acceptConsultationRequest(consultationId).subscribe({
      next: (res: any) => {
        console.log('✅ CONSULTANT::acceptConsultationRequest::SUCCESS: ', res);

        // Refresh data immediately after success
        setTimeout(() => {
          this.getListConsultationRequestsData();
        }, 500); // Small delay to ensure backend updates
      },
      error: (err: any) => {
        console.error('❌ CONSULTANT::acceptConsultationRequest::ERROR: ', err);
      },
    });
  }

  rejectConsultationRequest(consultationId: any) {
    console.log('🟡 Rejecting consultation request:', consultationId);
    this.api.rejectConsultationRequest(consultationId).subscribe({
      next: (res: any) => {
        console.log('✅ CONSULTANT::rejectConsultationRequest::SUCCESS: ', res);

        // Refresh data immediately after success
        setTimeout(() => {
          this.getListConsultationRequestsData();
        }, 500); // Small delay to ensure backend updates
      },
      error: (err: any) => {
        console.error('❌ CONSULTANT::rejectConsultationRequest::ERROR: ', err);
      },
    });
  }

  // SCHEDULE FARM VISIT
  scheduleFarmVisit(farmVisitData: any, consultationId: number) {
    console.log('🟡 Scheduling farm visit with data:', farmVisitData);
    this.api.scheduleFarmVisit(farmVisitData, consultationId).subscribe({
      next: (res: any) => {
        console.log('✅ CONSULTANT::scheduleFarmVisit::SUCCESS: ', res);

        setTimeout(() => {
          this.getListConsultationRequestsData();
        }, 500); // Small delay to ensure backend updates
      },
      error: (err: any) => {
        console.error('❌ CONSULTANT::scheduleFarmVisit::ERROR: ', err);
      },
    });
  }

  // CREATE CONSULTATION REPORT
  createConsultationReport(reportData: any) {
    console.log('🟡 Creating consultation report with data:', reportData);
    this.api.createConsultationReport(reportData).subscribe({
      next: (res: any) => {
        console.log('✅ CONSULTANT::createConsultationReport::SUCCESS: ', res);

        setTimeout(() => {
          this.getListConsultationRequestsData();
        }, 500); // Small delay to ensure backend updates
      },
      error: (err: any) => {
        console.error('❌ CONSULTANT::createConsultationReport::ERROR: ', err);
      },
    });
  }

  // ==================== New Consultation API Methods ====================

  /**
   * Get consultation by ID
   */
  getConsultationById(id: number): Observable<any> {
    console.log(`🔵 Fetching consultation with ID: ${id}`);
    return this.consultationApi.getConsultationById(id).pipe(
      tap((res: any) => console.log('✅ getConsultationById::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationById::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get all consultations for a farmer
   */
  getConsultationsByFarmerId(farmerId: number): Observable<any> {
    console.log(`🔵 Fetching consultations for farmer ID: ${farmerId}`);
    return this.consultationApi.getConsultationsByFarmerId(farmerId).pipe(
      tap((res: any) => console.log('✅ getConsultationsByFarmerId::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByFarmerId::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get all consultations for a consultant
   */
  getConsultationsByConsultantId(consultantId: number): Observable<any> {
    console.log(`🔵 Fetching consultations for consultant ID: ${consultantId}`);
    return this.consultationApi.getConsultationsByConsultantId(consultantId).pipe(
      tap((res: any) => console.log('✅ getConsultationsByConsultantId::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByConsultantId::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get consultations by crop
   */
  getConsultationsByCropId(cropId: number): Observable<any> {
    console.log(`🔵 Fetching consultations for crop ID: ${cropId}`);
    return this.consultationApi.getConsultationsByCropId(cropId).pipe(
      tap((res: any) => console.log('✅ getConsultationsByCropId::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByCropId::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get recent consultations
   */
  getRecentConsultations(): Observable<any> {
    console.log('🔵 Fetching recent consultations');
    return this.consultationApi.getRecentConsultations().pipe(
      tap((res: any) => console.log('✅ getRecentConsultations::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getRecentConsultations::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Status-Based Methods ====================

  /**
   * Get consultations by status
   */
  getConsultationsByStatus(status: string): Observable<any> {
    console.log(`🔵 Fetching consultations with status: ${status}`);
    return this.consultationApi.getConsultationsByStatus(status).pipe(
      tap((res: any) => console.log('✅ getConsultationsByStatus::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByStatus::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get farmer's consultations by status
   */
  getConsultationsByFarmerAndStatus(farmerId: number, status: string): Observable<any> {
    console.log(`🔵 Fetching consultations for farmer ${farmerId} with status: ${status}`);
    return this.consultationApi.getConsultationsByFarmerAndStatus(farmerId, status).pipe(
      tap((res: any) => console.log('✅ getConsultationsByFarmerAndStatus::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByFarmerAndStatus::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get consultant's consultations by status
   */
  getConsultationsByConsultantAndStatus(consultantId: number, status: string): Observable<any> {
    console.log(`🔵 Fetching consultations for consultant ${consultantId} with status: ${status}`);
    return this.consultationApi.getConsultationsByConsultantAndStatus(consultantId, status).pipe(
      tap((res: any) => console.log('✅ getConsultationsByConsultantAndStatus::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByConsultantAndStatus::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get consultations with multiple statuses
   */
  getConsultationsByMultipleStatuses(statuses: string[]): Observable<any> {
    console.log(`🔵 Fetching consultations with statuses: ${statuses.join(', ')}`);
    return this.consultationApi.getConsultationsByMultipleStatuses(statuses).pipe(
      tap((res: any) => console.log('✅ getConsultationsByMultipleStatuses::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationsByMultipleStatuses::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Status Management Methods ====================

  /**
   * Approve a consultation
   */
  approveConsultation(id: number): Observable<any> {
    console.log(`🟡 Approving consultation ID: ${id}`);
    return this.consultationApi.approveConsultation(id).pipe(
      tap((res: any) => {
        console.log('✅ approveConsultation::SUCCESS:', res);
        this.refreshConsultationData();
      }),
      catchError((err: any) => {
        console.error('❌ approveConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Reject a consultation
   */
  rejectConsultation(id: number): Observable<any> {
    console.log(`🟡 Rejecting consultation ID: ${id}`);
    return this.consultationApi.rejectConsultation(id).pipe(
      tap((res: any) => {
        console.log('✅ rejectConsultation::SUCCESS:', res);
        this.refreshConsultationData();
      }),
      catchError((err: any) => {
        console.error('❌ rejectConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Complete a consultation
   */
  completeConsultation(id: number): Observable<any> {
    console.log(`🟡 Completing consultation ID: ${id}`);
    return this.consultationApi.completeConsultation(id).pipe(
      tap((res: any) => {
        console.log('✅ completeConsultation::SUCCESS:', res);
        this.refreshConsultationData();
      }),
      catchError((err: any) => {
        console.error('❌ completeConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Update consultation status
   */
  updateConsultationStatus(id: number, status: string): Observable<any> {
    console.log(`🟡 Updating consultation ID ${id} status to: ${status}`);
    return this.consultationApi.updateConsultationStatus(id, status).pipe(
      tap((res: any) => {
        console.log('✅ updateConsultationStatus::SUCCESS:', res);
        this.refreshConsultationData();
      }),
      catchError((err: any) => {
        console.error('❌ updateConsultationStatus::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Update Methods ====================

  /**
   * Update consultation topic and description
   */
  updateConsultation(id: number, topic: string, description: string): Observable<any> {
    console.log(`🟡 Updating consultation ID: ${id}`);
    return this.consultationApi.updateConsultation(id, topic, description).pipe(
      tap((res: any) => {
        console.log('✅ updateConsultation::SUCCESS:', res);
        this.refreshConsultationData();
      }),
      catchError((err: any) => {
        console.error('❌ updateConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Statistics Methods ====================

  /**
   * Get total consultation count for farmer
   */
  getFarmerConsultationCount(farmerId: number): Observable<any> {
    console.log(`🔵 Fetching consultation count for farmer ID: ${farmerId}`);
    return this.consultationApi.getFarmerConsultationCount(farmerId).pipe(
      tap((res: any) => console.log('✅ getFarmerConsultationCount::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getFarmerConsultationCount::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get total consultation count for consultant
   */
  getConsultantConsultationCount(consultantId: number): Observable<any> {
    console.log(`🔵 Fetching consultation count for consultant ID: ${consultantId}`);
    return this.consultationApi.getConsultantConsultationCount(consultantId).pipe(
      tap((res: any) => console.log('✅ getConsultantConsultationCount::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultantConsultationCount::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Get consultation count by status
   */
  getConsultationCountByStatus(status: string): Observable<any> {
    console.log(`🔵 Fetching consultation count for status: ${status}`);
    return this.consultationApi.getConsultationCountByStatus(status).pipe(
      tap((res: any) => console.log('✅ getConsultationCountByStatus::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ getConsultationCountByStatus::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Validation Methods ====================

  /**
   * Check if active consultation exists between farmer and consultant
   */
  hasActiveConsultation(farmerId: number, consultantId: number): Observable<any> {
    console.log(
      `🔵 Checking active consultation between farmer ${farmerId} and consultant ${consultantId}`
    );
    return this.consultationApi.hasActiveConsultation(farmerId, consultantId).pipe(
      tap((res: any) => console.log('✅ hasActiveConsultation::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ hasActiveConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  /**
   * Validate if consultation can be created
   */
  canCreateConsultation(farmerId: number, consultantId: number): Observable<any> {
    console.log(
      `🔵 Validating if consultation can be created between farmer ${farmerId} and consultant ${consultantId}`
    );
    return this.consultationApi.canCreateConsultation(farmerId, consultantId).pipe(
      tap((res: any) => console.log('✅ canCreateConsultation::SUCCESS:', res)),
      catchError((err: any) => {
        console.error('❌ canCreateConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Delete Method ====================

  /**
   * Delete a consultation
   */
  deleteConsultation(id: number): Observable<any> {
    console.log(`🟡 Deleting consultation ID: ${id}`);
    return this.consultationApi.deleteConsultation(id).pipe(
      tap((res: any) => {
        console.log('✅ deleteConsultation::SUCCESS:', res);
        this.refreshConsultationData();
      }),
      catchError((err: any) => {
        console.error('❌ deleteConsultation::ERROR:', err);
        throw err;
      })
    );
  }

  // ==================== Helper Methods ====================

  /**
   * Refresh consultation data after updates
   */
  private refreshConsultationData(): void {
    setTimeout(() => {
      this.getListConsultationRequestsData();
    }, 500);
  }
}
