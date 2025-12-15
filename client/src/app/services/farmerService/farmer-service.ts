import { Injectable } from '@angular/core';
import { ApiService } from '../../API/api-service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FarmerService {
  private farmerDataSubject = new BehaviorSubject<any | null>(null);
  farmerData$ = this.farmerDataSubject.asObservable();

  private consultationRequestSubject = new BehaviorSubject<any | null>(null);
  consultationRequest$ = this.consultationRequestSubject.asObservable();

  private verifiedConsultantDataSubject = new BehaviorSubject<any | null>(null);
  verifiedConsultantData$ = this.verifiedConsultantDataSubject.asObservable();

  constructor(private api: ApiService) {}

  getFarmerProfile() {
    if (!this.farmerDataSubject.getValue()) {
      this.getFarmerProfileData();
    }
    console.log('getFarmerProfile');
    console.log(this.farmerDataSubject.getValue());
    return this.farmerData$;
  }

  getVerifiedConsultants() {
    if (!this.verifiedConsultantDataSubject.getValue()) {
      this.getVerifiedConsultantsData();
    }
    console.log('getVerifiedConsultants');
    console.log(this.verifiedConsultantDataSubject.getValue());
    return this.verifiedConsultantData$;
  }

  getConsultationRequest() {
    if (!this.consultationRequestSubject.getValue()) {
      this.getConsultationRequestData();
    }
    this.getConsultationRequestData();
    console.log('getConsultationRequest');
    console.log(this.consultationRequestSubject.getValue());
    return this.consultationRequest$;
  }

  // api calls
  getFarmerProfileData() {
    this.api.getFarmer().subscribe({
      next: (res: any) => {
        this.farmerDataSubject.next(res.data);
      },
      error: (err: any) => {
        console.log('FARMER::ERROR: ', err);
      },
    });
  }

  updateFarmerProfile(farmerProfile: any) {
    this.api.updateFarmerProfile(farmerProfile).subscribe({
      next: (res: any) => {
        this.farmerDataSubject.next(res.data);
        toast.success(res.message);
        this.getFarmerProfileData();
      },
      error: (err: any) => {
        console.log('FARMER::ERROR: ', err);
      },
    });
  }

  createConsultationRequest(requestData: any) {
    this.api.createConsultationRequest(requestData).subscribe({
      next: (res: any) => {
        console.log('Consultation request created:', res);
        toast.success(res.message);
      },
      error: (err: any) => {
        console.log('Error creating consultation request:', err);
        toast.error('Failed to create consultation request');
      },
    });
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

  getConsultationRequestData() {
    this.api.getConsultationsRequests().subscribe({
      next: (res: any) => {
        this.consultationRequestSubject.next(res.data);
        toast.success(res.message);
      },
      error: (err: any) => {
        console.log('FARMER::ERROR: ', err);
      },
    });
  }

  uploadProfilePicture(file: File) {
    return this.api.uploadFarmerProfilePicture(file);
  }
}
