import { CommonModule } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnInit,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth/auth-service';
import { toast } from 'ngx-sonner';
import { LocationService } from '../../../services/location/location-service';

@Component({
  selector: 'app-consultant-register',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './consultant-register.html',
  styleUrl: './consultant-register.css',
})
export class ConsultantRegister implements OnInit {
  @ViewChildren('otpInput') otpInputs!: QueryList<ElementRef<HTMLInputElement>>;

  consultantForm: FormGroup;
  currentStep: number = 1;
  otpCode: string[] = ['', '', '', '', '', ''];
  otpError: string = '';
  image = 'images/consultant.svg';
  locationCoordinates: any = {
    Latitude: 0,
    Longitude: 0,
  };

  qualifications = [
    'B.Sc Agriculture',
    'M.Sc Agriculture',
    'PhD Agriculture',
    'Soil Science',
    'Plant Pathology',
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private locationService: LocationService
  ) {
    this.consultantForm = this.fb.group(
      {
        // Step 1: Basic Information
        firstName: ['', [Validators.required, Validators.minLength(3)]],
        lastName: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
        experienceYears: ['', [Validators.required, Validators.min(1)]],
        qualifications: ['', Validators.required],
        experienceArea: ['', Validators.required],
        specialization: ['', Validators.required],
        bio: ['', Validators.required],
        verificationDocument: [null, Validators.required],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required],

        // Step 2: Address Details
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        postalCode: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
        country: ['', Validators.required],
        terms: [false, Validators.requiredTrue],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  ngOnInit(): void {
    this.getCurrentLocation();
  }

  passwordMatchValidator(form: FormGroup) {
    const pass = form.get('password')?.value;
    const confirm = form.get('confirmPassword')?.value;
    return pass === confirm ? null : { passwordMismatch: true };
  }

  get f() {
    return this.consultantForm.controls;
  }

  getProgressWidth(): string {
    return `${((this.currentStep - 1) / 2) * 100}%`;
  }

  nextStep() {
    if (this.currentStep === 1) {
      const step1Fields = [
        'firstName',
        'lastName',
        'email',
        'phone',
        'experienceYears',
        'qualifications',
        'experienceArea',
        'specialization',
        'bio',
        'verificationDocument',
        'password',
        'confirmPassword',
      ];

      let isValid = true;

      step1Fields.forEach((field) => {
        const control = this.consultantForm.get(field);
        control?.markAsTouched();
        if (control?.invalid) isValid = false;
      });

      if (!isValid || this.consultantForm.hasError('passwordMismatch')) return;

      // ---------------------------
      // FIX: async API inside subscribe causes UI not updating
      // ---------------------------
      this.authService.isUserAlreadyExist(this.f['email'].value, 'CONSULTANT').subscribe({
        next: (exists) => {
          if (exists) {
            toast.error('Consultant already exists');
            return;
          }

          // User does NOT exist → GO NEXT
          this.currentStep = 2;
          this.sendOtp();

          // IMPORTANT FIX: force Angular to detect UI update
          this.cdr.detectChanges();
        },
        error: () => {
          toast.error('Internal server error!');
        },
      });
    } else if (this.currentStep === 2) {
      const step2Fields = ['street', 'city', 'state', 'postalCode', 'country', 'terms'];
      let isValid = true;

      step2Fields.forEach((field) => {
        const control = this.consultantForm.get(field);
        control?.markAsTouched();
        if (control?.invalid) isValid = false;
      });

      if (isValid) {
        this.currentStep = 3;
        this.cdr.detectChanges(); // also optional here
      }
    }
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  onFileSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      if (file.size <= 10 * 1024 * 1024) {
        this.consultantForm.patchValue({ verificationDocument: file });
      } else {
        this.consultantForm.get('verificationDocument')?.setErrors({ fileTooLarge: true });
      }
    }
  }

  // OTP Methods
  onOtpInput(event: Event, index: number) {
    const input = event.target as HTMLInputElement;
    const value = input.value;

    if (value && /^[0-9]$/.test(value)) {
      this.otpCode[index] = value;
      this.otpError = '';

      // Move to next input
      if (index < 5) {
        const nextInput = this.otpInputs.toArray()[index + 1];
        nextInput?.nativeElement.focus();
      }
    } else {
      input.value = '';
      this.otpCode[index] = '';
    }
  }

  onOtpKeydown(event: KeyboardEvent, index: number) {
    const input = event.target as HTMLInputElement;

    // Handle backspace
    if (event.key === 'Backspace' && !input.value && index > 0) {
      const prevInput = this.otpInputs.toArray()[index - 1];
      prevInput?.nativeElement.focus();
      this.otpCode[index - 1] = '';
    }
  }

  sendOtp() {
    console.log('📧 Sending OTP to:', this.f['email'].value);
    this.authService.sendOtp(this.f['email'].value);
  }

  resendOtp() {
    this.otpCode = ['', '', '', '', '', ''];
    this.otpError = '';
    this.otpInputs.toArray().forEach((input) => (input.nativeElement.value = ''));
    this.otpInputs.first?.nativeElement.focus();
    console.log('📧 Resending OTP...');
    this.sendOtp();
  }

  verifyOtp(): void {
    const enteredOtp = this.otpCode.join('');

    if (enteredOtp.length !== 6) {
      this.otpError = 'Please enter complete OTP code';
      return;
    }

    // Call the async verification
    this.authService.verifyOtp(this.f['email'].value, enteredOtp).subscribe({
      next: (res) => {
        console.log('OTP verified successfully:', res);
        toast.success('OTP verified successfully');
        this.otpError = '';

        // Proceed with registration after successful verification
        this.submitRegistration();
      },
      error: (err) => {
        console.error('Failed to verify OTP:', err);
        this.otpError = 'Invalid or expired OTP code';
        toast.error(
          'Failed to verify OTP: ' + (err.error?.message || err.message || 'Unknown error')
        );
      },
    });
  }

  submitRegistration(): void {
    if (this.consultantForm.valid) {
      const consultantData = new FormData();

      consultantData.append('firstName', this.f['firstName'].value);
      consultantData.append('lastName', this.f['lastName'].value);
      consultantData.append('email', this.f['email'].value);
      consultantData.append('password', this.f['password'].value);
      consultantData.append('phone', this.f['phone'].value);
      consultantData.append('role', 'CONSULTANT');
      consultantData.append('expertiseArea', this.f['experienceArea'].value);
      consultantData.append('experienceYears', this.f['experienceYears'].value);
      consultantData.append('qualifications', this.f['qualifications'].value);
      consultantData.append('specialization', this.f['specialization'].value);
      consultantData.append('bio', this.f['bio'].value);

      // File
      if (this.f['verificationDocument'].value) {
        consultantData.append('verificationDocument', this.f['verificationDocument'].value);
      }

      // Address → Use Spring nested binding (address.propertyName)
      consultantData.append('address.street', this.f['street'].value);
      consultantData.append('address.city', this.f['city'].value);
      consultantData.append('address.state', this.f['state'].value);
      consultantData.append('address.pinCode', this.f['postalCode'].value);
      consultantData.append('address.country', this.f['country'].value);
      consultantData.append('address.latitude', String(this.locationCoordinates.Latitude));
      consultantData.append('address.longitude', String(this.locationCoordinates.Longitude));

      // Debug: show actual FormData
      console.log('🔍 REAL FORMDATA CONTENT:');
      for (let p of consultantData.entries()) {
        console.log(p[0] + ': ', p[1]);
      }

      // Call API
      this.authService.registerConsultant(consultantData);
    }
  }

  getCurrentLocation() {
    this.locationService
      .getCurrentLocation()
      .then((location) => {
        console.log('Latitude:', location.latitude);
        console.log('Longitude:', location.longitude);
        console.log('Accuracy:', location.accuracy, 'meters');
        console.log(location);
        this.locationCoordinates = {
          Latitude: location.latitude,
          Longitude: location.longitude,
        };
        this.locationService.getAddress(location.latitude, location.longitude);
      })
      .catch((error) => {
        console.error('Error:', error);
      });
  }

  onSubmit() {
    if (this.currentStep === 3) {
      // Verify OTP - if successful, it will automatically call submitRegistration()
      this.verifyOtp();
    }
  }
}
