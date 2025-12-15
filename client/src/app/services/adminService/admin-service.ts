import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { AdminApi } from '../../API/adminAPI/admin-api';
import {
  AdminDashboardStats,
  UserStatistics,
  ConsultationOverview,
  Activity,
  AdminUser,
} from '../../interfaces/admin.interfaces';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  // BehaviorSubjects for caching data
  private dashboardStatsSubject = new BehaviorSubject<AdminDashboardStats | null>(null);
  dashboardStats$ = this.dashboardStatsSubject.asObservable();

  private userStatsSubject = new BehaviorSubject<UserStatistics | null>(null);
  userStats$ = this.userStatsSubject.asObservable();

  constructor(private adminApi: AdminApi) {}

  // ==================== Dashboard Statistics ====================

  /**
   * Get comprehensive dashboard statistics
   */
  getDashboardStatistics(): Observable<any> {
    console.log('🔵 [AdminService] Requesting dashboard statistics via AdminApi');
    return this.adminApi.getDashboardStatistics().pipe(
      tap((response) => {
        if (response.data) {
          this.dashboardStatsSubject.next(response.data);
          console.log('✅ [AdminService] Dashboard stats cached');
        }
      }),
      catchError((error) => {
        console.error('❌ [AdminService] Error loading dashboard stats:', error);
        throw error;
      })
    );
  }

  /**
   * Get detailed user statistics
   */
  getUserStatistics(): Observable<any> {
    return this.adminApi.getUserStatistics().pipe(
      tap((response) => {
        if (response.data) {
          this.userStatsSubject.next(response.data);
        }
      })
    );
  }

  /**
   * Get consultation overview statistics
   */
  getConsultationStatistics(): Observable<any> {
    return this.adminApi.getConsultationStatistics();
  }

  // ==================== User Management ====================

  getAllUsers(role?: string): Observable<any> {
    return this.adminApi.getAllUsers(role);
  }

  getAllFarmers(): Observable<any> {
    return this.adminApi.getAllFarmers();
  }

  toggleUserStatus(username: string): Observable<any> {
    return this.adminApi.toggleUserStatus(username);
  }

  // ==================== Consultant Management ====================

  getAllConsultants(): Observable<any> {
    return this.adminApi.getAllConsultants();
  }

  verifyConsultant(username: string): Observable<any> {
    return this.adminApi.verifyConsultant(username);
  }

  rejectConsultant(username: string): Observable<any> {
    return this.adminApi.rejectConsultant(username);
  }

  // ==================== Consultation Management ====================

  getAllConsultations(): Observable<any> {
    return this.adminApi.getAllConsultations();
  }

  getConsultationOverview(): Observable<any> {
    return this.adminApi.getConsultationOverview();
  }

  getConsultationById(id: number): Observable<any> {
    return this.adminApi.getConsultationById(id);
  }

  // ==================== Activity Tracking ====================

  getRecentActivities(): Observable<any> {
    return this.adminApi.getRecentActivities();
  }

  // ==================== Helper Methods ====================

  clearDashboardCache(): void {
    this.dashboardStatsSubject.next(null);
    this.userStatsSubject.next(null);
  }

  refreshDashboardStats(): void {
    this.getDashboardStatistics().subscribe();
    this.getUserStatistics().subscribe();
  }
}
