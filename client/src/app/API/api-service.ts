import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private baseUrl: string = 'http://localhost:8080/api/v1/';

  constructor(private http: HttpClient) {}

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  public setAuthToken(token: string): void {
    this.httpOptions.headers = this.httpOptions.headers.set('Authorization', `Bearer ${token}`);
  }

  registerUser(userData: any): Observable<any> {
    return this.http.post(this.baseUrl + 'auth/register/farmer', userData, this.httpOptions);
  }

  registerConsultant(userData: any): Observable<any> {
    return this.http.post(this.baseUrl + 'auth/register/consultant', userData);
  }

  login(userData: any): Observable<any> {
    return this.http.post(this.baseUrl + 'auth/login', userData, this.httpOptions);
  }

  isUserAlreadyExistst(username: string, role: string): Observable<any> {
    return this.http.get(
      `${this.baseUrl}auth/isUserAlreadyExist?username=${username}&role=${role}`
    );
  }

  getVerifiedConsultants(): Observable<any> {
    return this.http.get(this.baseUrl + 'consultants/verified');
  }

  /**
   * Farmer API endpoints
   */
  getFarmer(): Observable<any> {
    return this.http.get(this.baseUrl + 'farmers/profile');
  }

  getConsultationsRequests(): Observable<any> {
    return this.http.get(this.baseUrl + 'farmers/consultation/request/all');
  }

  updateFarmerProfile(farmerProfile: any) {
    return this.http.put(this.baseUrl + 'farmers/update', farmerProfile);
  }

  createConsultationRequest(requestData: any): Observable<any> {
    return this.http.post(this.baseUrl + 'farmers/consultation/request', requestData);
  }

  /*
   * Consultant API endpoints
   */
  getAllConsultants(): Observable<any> {
    return this.http.get(this.baseUrl + 'admin/all/consultants');
  }

  getConsultantProfile(): Observable<any> {
    return this.http.get(this.baseUrl + 'consultants/profile');
  }

  updateConsultantProfile(consultantProfile: any) {
    return this.http.put(this.baseUrl + 'consultants/profile/update', consultantProfile);
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    // Don't set Content-Type header for FormData - browser will set it automatically with boundary
    return this.http.post(this.baseUrl + 'consultants/profile-picture', formData);
  }

  uploadFarmerProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(this.baseUrl + 'farmers/profile-picture', formData);
  }

  /*
   * Admin API endpoints
   */
  verifyConsultant(username: string): Observable<any> {
    return this.http.post(`${this.baseUrl}admin/verify/consultant/${username}`, null);
  }

  /*
   * consultation API endpoints
   */
  getConsultationRequestsByConsultant(): Observable<any> {
    return this.http.get(this.baseUrl + 'consultants/consultation/request/all');
  }

  acceptConsultationRequest(consultationId: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}consultants/consultation/request/${consultationId}/accept`,
      null
    );
  }

  rejectConsultationRequest(consultationId: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}consultants/consultation/request/${consultationId}/reject`,
      null
    );
  }

  /*
   * farm visit API endpoints
   */
  // scheduleFarmVisit(farmVisitData: any,consultationId: number): Observable<any> {
  //   return this.http.post(`${this.baseUrl}farmers/farm/visit/schedule/${consultationId}`, farmVisitData);
  // }

  scheduleFarmVisit(farmVisitData: any, consultationId: number): Observable<any> {
    console.log('🔵 Scheduling farm visit with data:', farmVisitData);
    return this.http.post(`${this.baseUrl}farmvisits/schedule/${consultationId}`, farmVisitData);
  }

  /*
   * consultation report API endpoints
   */
  createConsultationReport(reportData: any): Observable<any> {
    console.log('🔵 Creating consultation report with data:', reportData);
    return this.http.post(`${this.baseUrl}consultation-reports`, reportData);
  }

  //decode latitude and longitude
  getAddress(lat: number, lng: number): Observable<any> {
    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&addressdetails=1`;

    return this.http.get(url, {
      headers: {
        'Accept-Language': 'en', // Get English result
        'User-Agent': 'AngularApp/1.0', // Required by Nominatim
      },
    });
  }
}
