import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminApi {
  // baseUrl = environment.apiUrl + '/admin'; // Assuming environment file exists, or hardcode for now as per previous service
  private baseUrl = 'http://localhost:8080/api/v1/admin';

  constructor(private http: HttpClient) {}

  // ==================== Dashboard Statistics ====================

  getDashboardStatistics(): Observable<any> {
    return this.http.get(`${this.baseUrl}/dashboard/statistics`);
  }

  getUserStatistics(): Observable<any> {
    return this.http.get(`${this.baseUrl}/statistics/users`);
  }

  getConsultationStatistics(): Observable<any> {
    return this.http.get(`${this.baseUrl}/statistics/consultations`);
  }

  // ==================== User Management ====================

  getAllUsers(role?: string): Observable<any> {
    let params = new HttpParams();
    if (role) {
      params = params.set('role', role);
    }
    return this.http.get(`${this.baseUrl}/users/all`, { params });
  }

  getAllFarmers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/farmers/all`);
  }

  toggleUserStatus(username: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/users/${username}/toggle-status`, {});
  }

  // ==================== Consultant Management ====================

  getAllConsultants(): Observable<any> {
    return this.http.get(`${this.baseUrl}/all/consultants`);
  }

  verifyConsultant(username: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/verify/consultant/${username}`, {});
  }

  rejectConsultant(username: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/reject/consultant/${username}`, {});
  }

  // ==================== Consultation Management ====================

  getAllConsultations(): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultations/all`);
  }

  getConsultationOverview(): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultations/overview`);
  }

  getConsultationById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultations/${id}`);
  }

  // ==================== Activity Tracking ====================

  getRecentActivities(): Observable<any> {
    return this.http.get(`${this.baseUrl}/activities/recent`);
  }
}
