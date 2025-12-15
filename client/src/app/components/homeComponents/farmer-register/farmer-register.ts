import { CommonModule } from '@angular/common';
import { Component, ElementRef, QueryList, ViewChildren } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../../services/auth/auth-service';
import { toast } from 'ngx-sonner';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-farmer-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './farmer-register.html',
  styleUrl: './farmer-register.css',
})
export class FarmerRegister {
  @ViewChildren('otpInput') otpInputs!: QueryList<ElementRef<HTMLInputElement>>;

  farmerForm: FormGroup;
  currentStep: number = 1;
  otpCode: string[] = ['', '', '', '', '', ''];
  otpError: string = '';
  image = 'images/farmer.svg';

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.farmerForm = this.fb.group(
      {
        firstName: ['', [Validators.required, Validators.minLength(3)]],
        lastName: ['', [Validators.required, Validators.minLength(3)]],
        email: [
          '',
          [
            Validators.required,
            Validators.email,
            Validators.pattern('^[a-zA-Z0-9_.]+@[a-zA-Z0-9]+.[a-zA-Z]{2,}$'),
          ],
        ],
        phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
        password: ['', [Validators.required, this.passwordValidator]],
        confirmPassword: ['', Validators.required],
        terms: [false, Validators.requiredTrue],
        role: ['FARMER'],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  // Password match validator
  passwordMatchValidator(form: FormGroup) {
    const pass = form.get('password')?.value;
    const confirm = form.get('confirmPassword')?.value;
    return pass === confirm ? null : { passwordMismatch: true };
  }

  // Strong password validator
  passwordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';
    const errors: any = {};

    if (!/[a-z]/.test(value)) {
      errors.lowercase = 'At least one lowercase letter is required.';
    }

    if (!/[A-Z]/.test(value)) {
      errors.uppercase = 'At least one uppercase letter is required.';
    }

    if (!/[0-9]/.test(value)) {
      errors.number = 'At least one number is required.';
    }

    if (!/[@$!%*?&]/.test(value)) {
      errors.special = 'At least one special character (@ $ ! % * ? &) is required.';
    }

    if (value.length < 8) {
      errors.minlength = 'Password must be at least 8 characters long.';
    }

    return Object.keys(errors).length ? errors : null;
  }

  get f() {
    return this.farmerForm.controls;
  }

  getProgressWidth(): string {
    return `${((this.currentStep - 1) / 2) * 100}%`;
  }

  nextStep() {
    if (this.currentStep === 1) {
      // Validate Step 1 fields
      const step1Fields = [
        'firstName',
        'lastName',
        'email',
        'phone',
        'password',
        'confirmPassword',
        'terms',
      ];

      let isValid = true;
      step1Fields.forEach((field) => {
        const control = this.farmerForm.get(field);
        control?.markAsTouched();
        if (control?.invalid) {
          isValid = false;
        }
      });

      if (!isValid || this.farmerForm.hasError('passwordMismatch')) {
        return;
      }

      this.authService.isUserAlreadyExist(this.f['email'].value, 'FARMER').subscribe({
        next: (exists) => {
          if (exists) {
            toast.error('User already exists');
            return; // prevent moving to next step
          }

          // user does not exist → continue
          this.currentStep = 2;
          this.sendOtp();
        },
        error: () => {
          toast.error('Internal server error!');
        },
      });
    }
  }

  previousStep() {
    if (this.currentStep > 1 && this.currentStep < 3) {
      this.currentStep--;
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
    console.log('📱 Phone:', this.f['phone'].value);
    this.authService.sendOtp(this.f['email'].value);
    // Implement actual OTP sending logic here
    // Example: this.authService.sendOtp(this.f['email'].value);
  }

  resendOtp() {
    this.otpCode = ['', '', '', '', '', ''];
    this.otpError = '';
    this.otpInputs.toArray().forEach((input) => (input.nativeElement.value = ''));
    //     this.otpInputs.toArray().forEach((input: ElementRef<HTMLInputElement>) => {
    //   input.nativeElement.value = '';
    // });
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
    if (this.farmerForm.valid) {
      console.log('✅ Farmer registration data:', this.farmerForm.value);
      console.log('✅ OTP Verified:', this.otpCode.join(''));
      this.authService.registerUser(this.farmerForm.value);
    } else {
      console.log('❌ Form is not valid');
      toast.error('Please fill all required fields correctly');
    }
  }

  verifyAndSubmit() {
    // Verify OTP - if successful, it will automatically call submitRegistration()
    this.verifyOtp();
  }

  onSubmit() {
    // This is for the final submit
    this.verifyAndSubmit();
  }

  goToLogin() {
    console.log('Navigating to login...');
    // Implement navigation to login page
    // this.router.navigate(['/login']);
  }

  resetForm() {
    this.farmerForm.reset({
      role: 'FARMER',
      terms: false,
    });
    this.currentStep = 1;
    this.otpCode = ['', '', '', '', '', ''];
    this.otpError = '';
  }
}
