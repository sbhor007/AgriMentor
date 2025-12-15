import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

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

constructor(private fb: FormBuilder, private router: Router) {
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
async submitEmail() {
if (this.emailForm.invalid) {
this.errorMessage.set('Please enter a valid email address');
return;
}

this.isLoading.set(true);
this.errorMessage.set('');

try {
// Simulate API call
await this.delay(1500);

this.userEmail.set(this.emailForm.value.email);
this.successMessage.set('OTP sent to your email!');
this.currentStep.set('otp');

// Clear success message after animation
setTimeout(() => this.successMessage.set(''), 3000);
} catch (error) {
this.errorMessage.set('Failed to send OTP. Please try again.');
} finally {
this.isLoading.set(false);
}
}

// Verify OTP
async verifyOTP() {
if (this.otpForm.invalid) {
this.errorMessage.set('Please enter a valid 6-digit OTP');
return;
}

this.isLoading.set(true);
this.errorMessage.set('');

try {
// Simulate API call
await this.delay(1500);

this.successMessage.set('OTP verified successfully!');
this.currentStep.set('reset');

setTimeout(() => this.successMessage.set(''), 3000);
} catch (error) {
this.errorMessage.set('Invalid OTP. Please try again.');
} finally {
this.isLoading.set(false);
}
}

// Reset password
async resetPassword() {
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

try {
// Simulate API call
await this.delay(1500);

this.currentStep.set('success');

// Redirect to login after 3 seconds
setTimeout(() => {
this.router.navigate(['/login']);
}, 3000);
} catch (error) {
this.errorMessage.set('Failed to reset password. Please try again.');
} finally {
this.isLoading.set(false);
}
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

// Utility function for delay
private delay(ms: number): Promise<void> {
return new Promise((resolve) => setTimeout(resolve, ms));
}
}


