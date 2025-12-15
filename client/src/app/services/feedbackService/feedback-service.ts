import { Injectable } from '@angular/core';
import { FeedbackApi } from '../../API/feedbackAPI/feedback-api';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FeedbackService {
  private feedbackSubject = new BehaviorSubject<any>(null);
  feedback$ = this.feedbackSubject.asObservable();

  constructor(private api: FeedbackApi) {}

  submitFeedback(data: any): Observable<any> {
    return this.api.createFeedback(data).pipe(
      tap((res) => {
        // Update state immediately after submission
        const feedbackData = res.data || res;
        this.feedbackSubject.next(feedbackData);
        console.log('✅ Feedback submitted and state updated:', feedbackData);
      })
    );
  }

  updateFeedback(id: number, data: any): Observable<any> {
    return this.api.updateFeedback(id, data).pipe(
      tap((res) => {
        // Update state after editing
        const feedbackData = res.data || res;
        this.feedbackSubject.next(feedbackData);
        console.log('✅ Feedback updated and state refreshed:', feedbackData);
      })
    );
  }

  getFeedbackByConsultationId(consultationId: number): Observable<any> {
    return this.api.getFeedbackByConsultationId(consultationId).pipe(
      tap((res) => {
        // Update state when fetching
        const feedbackData = res.data || res;
        this.feedbackSubject.next(feedbackData);
        console.log('✅ Feedback fetched and state updated:', feedbackData);
      })
    );
  }

  clearFeedbackState(): void {
    this.feedbackSubject.next(null);
  }

  hasFeedback(consultationId: number): Observable<any> {
    return this.api.hasFeedback(consultationId);
  }

  getConsultantStats(consultantId: number): Observable<any> {
    return this.api.getConsultantStats(consultantId);
  }

  getFeedbackByConsultantId(consultantId: number): Observable<any> {
    return this.api.getFeedbackByConsultantId(consultantId);
  }

  getAllFeedback(): Observable<any> {
    return this.api.getAllFeedback();
  }
}
