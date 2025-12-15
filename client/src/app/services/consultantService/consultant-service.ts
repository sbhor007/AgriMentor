import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiService } from '../../API/api-service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class ConsultantService {
  private consultantDataSubject = new BehaviorSubject<any | null>(null);
  consultantData$ = this.consultantDataSubject.asObservable();

  private verifiedConsultantDataSubject = new BehaviorSubject<any | null>(null);
  verifiedConsultantData$ = this.verifiedConsultantDataSubject.asObservable();

  constructor(private api: ApiService, private router: Router) {}

  getConsultantProfile() {
    if (!this.consultantDataSubject.getValue()) {
      this.getConsultantProfileData();
    }
    console.log('getConsultantProfile');
    console.log(this.consultantDataSubject.getValue());
    return this.consultantData$;
  }

  getConsultantProfileData() {
    this.api.getConsultantProfile().subscribe({
      next: (res: any) => {
        this.consultantDataSubject.next(res.data);
      },
      error: (err: any) => {
        console.log('CONSULTANT::ERROR: ', err);
      },
    });
  }

  /*get verified consultants */
  getVerifiedConsultants() {
    if (!this.verifiedConsultantDataSubject.getValue()) {
      this.getVerifiedConsultantsData();
    }
    console.log('getVerifiedConsultants');
    console.log(this.verifiedConsultantDataSubject.getValue());
    return this.verifiedConsultantData$;
  }

  getVerifiedConsultantsData() {
    this.api.getVerifiedConsultants().subscribe({
      next: (res: any) => {
        this.verifiedConsultantDataSubject.next(res.data);
        console.log('VERIFIED CONSULTANTS: ', res.data);
      },
      error: (err: any) => {
        console.log('FARMER::ERROR: ', err);
      },
    });
  }

  updateConsultantProfile(profileData: any) {
    console.log('inside consultant profile');

    this.api.updateConsultantProfile(profileData).subscribe({
      next: (res: any) => {
        this.getConsultantProfileData();
        console.log('CONSULTANT:updateProfile::SUCCESS: ', res);
        console.log(res);
      },
      error: (err: any) => {
        console.error('CONSULTANT:updateProfile::ERROR: ', err);
        console.error(err);
      },
    });
  }

  uploadProfilePicture(file: File): Observable<any> {
    return this.api.uploadProfilePicture(file);
  }
}
