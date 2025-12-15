import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FeedbackApi {
  private baseUrl = 'http://localhost:8080/api/v1/feedback';

  constructor(private http: HttpClient) {}

  createFeedback(data: any): Observable<any> {
    return this.http.post(this.baseUrl, data);
  }

  updateFeedback(id: number, data: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, data);
  }

  getFeedbackById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  getFeedbackByConsultationId(consultationId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultation/${consultationId}`);
  }

  hasFeedback(consultationId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultation/${consultationId}/exists`);
  }

  getFeedbackByConsultantId(consultantId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultant/${consultantId}`);
  }

  getConsultantStats(consultantId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultant/${consultantId}/stats`);
  }

  getAllFeedback(): Observable<any> {
    return this.http.get(this.baseUrl);
  }
}
