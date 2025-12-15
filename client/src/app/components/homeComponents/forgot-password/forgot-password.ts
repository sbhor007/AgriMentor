import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth-service';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  // Form states
  currentStep = signal<'email' | 'otp' | 'reset' | 'success'>('email');
  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  // Forms
  emailForm: FormGroup;
  otpForm: FormGroup;
  resetForm: FormGroup;

  // Data
  userEmail = signal('');

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService) {
    // Initialize forms
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });

    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
    });

    this.resetForm = this.fb.group(
      {
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  // Custom validator for password matching
  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  // Submit email
  submitEmail() {
    if (this.emailForm.invalid) {
      this.errorMessage.set('Please enter a valid email address');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const email = this.emailForm.value.email;

    this.authService.sendForgotPasswordOtp(email).subscribe({
      next: (res) => {
        console.log('Forgot password OTP sent:', res);
        this.userEmail.set(email);
        this.successMessage.set('OTP sent to your email!');
        this.currentStep.set('otp');

        // Clear success message after animation
        setTimeout(() => this.successMessage.set(''), 3000);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to send OTP:', err);
        this.errorMessage.set('Failed to send OTP. Please try again.');
        this.isLoading.set(false);
      },
    });
  }

  // Verify OTP
  verifyOTP() {
    if (this.otpForm.invalid) {
      this.errorMessage.set('Please enter a valid 6-digit OTP');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const otp = this.otpForm.value.otp;

    this.authService.verifyOtp(this.userEmail(), otp).subscribe({
      next: (res) => {
        console.log('OTP verified:', res);
        this.successMessage.set('OTP verified successfully!');
        this.currentStep.set('reset');

        setTimeout(() => this.successMessage.set(''), 3000);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('OTP verification failed:', err);
        this.errorMessage.set('Invalid OTP. Please try again.');
        this.isLoading.set(false);
      },
    });
  }

  // Reset password
  resetPassword() {
    if (this.resetForm.invalid) {
      if (this.resetForm.hasError('passwordMismatch')) {
        this.errorMessage.set('Passwords do not match');
      } else {
        this.errorMessage.set('Please fill all fields correctly');
      }
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const newPassword = this.resetForm.value.password;
    const otp = this.otpForm.value.otp;

    this.authService.resetPassword(this.userEmail(), otp, newPassword).subscribe({
      next: (res) => {
        console.log('Password reset successful:', res);
        this.currentStep.set('success');

        // Redirect to login after 3 seconds
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Password reset failed:', err);
        this.errorMessage.set('Failed to reset password. Please try again.');
        this.isLoading.set(false);
      },
    });
  }

  // Go back to previous step
  goBack() {
    this.errorMessage.set('');
    this.successMessage.set('');

    if (this.currentStep() === 'otp') {
      this.currentStep.set('email');
    } else if (this.currentStep() === 'reset') {
      this.currentStep.set('otp');
    }
  }

  // Navigate to login
  goToLogin() {
    this.router.navigate(['/login']);
  }
}
