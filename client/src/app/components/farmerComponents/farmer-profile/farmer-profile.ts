import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { FarmerService } from '../../../services/farmerService/farmer-service';
import { CommonModule } from '@angular/common';

interface AddressDTO {
  street: string;
  city: string;
  state: string;
  pinCode: string;
  country: string;
  latitude?: string;
  longitude?: string;
}

interface FarmerProfileUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  soilType: string;
  farmAreaHectares: number;
  address: AddressDTO;
}

@Component({
  selector: 'app-farmer-profile',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './farmer-profile.html',
  styleUrl: './farmer-profile.css',
})
export class FarmerProfile implements OnInit, OnDestroy {
  farmerProfile: any = null;
  editMode = false;
  isLoading = false;
  saveSuccess = false;

  profileForm: FormGroup;
  private subscription!: Subscription;

  profilePreview: any = null;
  selectedPhotoFile: File | null = null;

  soilTypes = [
    { value: 'ALLUVIAL', label: 'Alluvial' },
    { value: 'BLACK', label: 'Black' },
    { value: 'RED', label: 'Red' },
    { value: 'LATERITE', label: 'Laterite' },
    { value: 'MOUNTAIN', label: 'Mountain' },
    { value: 'DESERT', label: 'Desert' },
    { value: 'PEATY', label: 'Peaty' },
    { value: 'SALINE', label: 'Saline' },
    { value: 'LOAMY', label: 'Loamy' },
    { value: 'CLAYEY', label: 'Clayey' },
    { value: 'SILTY', label: 'Silty' },
    { value: 'CHALKY', label: 'Chalky' },
  ];

  // Format soil type for display (e.g., 'BLACK_SOIL' -> 'Black Soil')
  formatSoilType(soilType: string): string {
    if (!soilType) return '';
    return soilType
      .toLowerCase()
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  constructor(
    private farmerService: FarmerService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      soilType: ['', Validators.required],
      farmAreaHectares: ['', [Validators.required, Validators.min(0)]],
      street: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pinCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      country: ['India', Validators.required],
      latitude: [''],
      longitude: [''],
    });

    console.log('👤 FarmerProfile Constructor Initialized');
  }

  ngOnInit(): void {
    console.log('📌 FarmerProfile ngOnInit — fetching profile');
    this.getFarmerProfile();
  }

  getFarmerProfile() {
    this.isLoading = true;
    this.subscription = this.farmerService.getFarmerProfile().subscribe((state: any) => {
      console.log('🟢 Received farmer profile from service:', state);
      this.farmerProfile = state;
      this.patchFormValues();

      if (this.farmerProfile?.profilePhotoUrl) {
        this.profilePreview = this.farmerProfile.profilePhotoUrl;
      }

      this.isLoading = false;
      this.cdr.detectChanges();
    });
  }

  patchFormValues() {
    if (!this.farmerProfile) return;

    const formData: any = {
      firstName: this.farmerProfile.firstName || '',
      lastName: this.farmerProfile.lastName || '',
      email: this.farmerProfile.email || '',
      phone: this.farmerProfile.phone || '',
      soilType: this.farmerProfile.soilType || '',
      farmAreaHectares: this.farmerProfile.farmAreaHectares || 0,
    };

    if (this.farmerProfile.addressDTO || this.farmerProfile.address) {
      const address = this.farmerProfile.addressDTO || this.farmerProfile.address;
      formData.street = address.street || '';
      formData.city = address.city || '';
      formData.state = address.state || '';
      formData.pinCode = address.pinCode || '';
      formData.country = address.country || 'India';
      formData.latitude = address.latitude || '';
      formData.longitude = address.longitude || '';
    }

    this.profileForm.patchValue(formData);
  }

  toggleEdit() {
    this.editMode = !this.editMode;
    this.saveSuccess = false;

    if (this.editMode && this.farmerProfile) {
      this.patchFormValues();
    } else {
      this.patchFormValues();
      this.selectedPhotoFile = null;
      if (this.farmerProfile?.profilePhotoUrl) {
        this.profilePreview = this.farmerProfile.profilePhotoUrl;
      }
    }

    this.cdr.detectChanges();
  }

  onPhotoSelect(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Please select a valid image file');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      alert('File size must be less than 5MB');
      return;
    }

    this.selectedPhotoFile = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.profilePreview = reader.result;
      this.cdr.detectChanges();
    };

    reader.readAsDataURL(file);
  }

  removePhoto() {
    this.profilePreview = null;
    this.selectedPhotoFile = null;
    this.cdr.detectChanges();
  }

  saveChanges() {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    // If there's a new photo selected, upload it first
    if (this.selectedPhotoFile) {
      console.log('📸 Uploading profile picture first...');
      this.farmerService.uploadProfilePicture(this.selectedPhotoFile).subscribe({
        next: (uploadRes: any) => {
          console.log('✅ Profile picture uploaded successfully:', uploadRes);
          // After successful upload, update profile data
          this.updateProfileData();
        },
        error: (uploadErr: any) => {
          console.error('❌ Error uploading profile picture:', uploadErr);
          this.isLoading = false;
          alert('Failed to upload profile picture. Please try again.');
        },
      });
    } else {
      // No new photo, just update profile data
      this.updateProfileData();
    }
  }

  private updateProfileData() {
    const formValue = this.profileForm.value;

    const updateRequest: FarmerProfileUpdateRequest = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      phone: formValue.phone,
      soilType: formValue.soilType,
      farmAreaHectares: parseFloat(formValue.farmAreaHectares),
      address: {
        street: formValue.street,
        city: formValue.city,
        state: formValue.state,
        pinCode: formValue.pinCode,
        country: formValue.country,
        latitude: formValue.latitude || undefined,
        longitude: formValue.longitude || undefined,
      },
    };

    console.log('💾 Saving updated data:', updateRequest);

    this.farmerService.updateFarmerProfile(updateRequest);
    this.getFarmerProfile();

    this.isLoading = false;
    this.editMode = false;
    this.selectedPhotoFile = null;
    this.cdr.detectChanges();
  }

  get f() {
    return this.profileForm.controls;
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
