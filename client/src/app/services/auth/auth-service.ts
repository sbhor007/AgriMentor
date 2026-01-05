import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ApiService } from '../../API/api-service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private api: ApiService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  isUserAlreadyExist(username: string, role: string): Observable<boolean> {
    return this.api.isUserAlreadyExistst(username, role).pipe(
      map((res: any) => {
        console.log(res);

        return res.data;
      })
    );
  }

  registerUser(userData: any) {
    console.log('Insede ResgisterUser...');

    this.api.registerUser(userData).subscribe({
      next: (res) => {
        console.log('AUTH::DATA: ' + res);
        toast.success('Registration successful');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.log('AUTH::ERROR: ' + err);
        toast.error('Registration failed');
      },
    });
  }

  //REgister consultant
  registerConsultant(consultantData: any) {
    console.log('Insede ResgisterConsultant...');

    this.api.registerConsultant(consultantData).subscribe({
      next: (res) => {
        console.log('AUTH:RegisterConsultant::DATA: ' + res);
        toast.info('Registration Request send successfully, wait for approval');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.log('AUTH:RegisterConsultant::ERROR: ' + err);
        console.log(err);

        toast.error('Registration failed');
      },
    });
  }

  login(userData: any) {
    this.api.login(userData).subscribe({
      next: (res) => {
        console.log('AUTH:LOGIN::DATA: ' + res);
        toast.success('Login successful');
        console.log(res);
        const tokens = res.data;
        if (isPlatformBrowser(this.platformId)) {
          sessionStorage.setItem('token', tokens.jwt);
          // // sessionStorage.setItem('refresh', tokens.refresh);
          sessionStorage.setItem('userId', tokens.id);
          sessionStorage.setItem('role', tokens.role);
          sessionStorage.setItem('email', userData.username);
        }
        //switch route based on role
        if (tokens.role === 'CONSULTANT') {
          this.router.navigate(['consultant/dashboard']);
        } else if (tokens.role === 'FARMER') {
          // this.farmerService.getFarmerProfile();
          this.router.navigate(['farmer/dashboard']);
        } else {
          this.router.navigate(['admin/home']);
        }
      },
      error: (err) => {
        console.log('AUTH:LOGIN::ERROR: ');
        console.log(err);

        toast.error('Login failed: ' + (err.error?.message || err.message || 'Unknown error'));
      },
    });
  }

  /**
   * Send OTP to email for verification
   * @param email - User's email address
   * @returns Observable with API response
   */
  sendOtp(email: string) {
    return this.api.sendOtp(email).subscribe({
      next: (res) => {
        console.log('OTP sent successfully:', res);
        toast.success('OTP sent to your email');
      },
      error: (err) => {
        console.error('Failed to send OTP:', err);
        toast.error(
          'Failed to send OTP: ' + (err.error?.message || err.message || 'Unknown error')
        );
      },
    });
  }

  /**
   * Verify OTP sent to email
   * @param email - User's email address
   * @param otp - OTP code to verify
   * @returns Observable with verification result
   */
  verifyOtp(email: string, otp: string) {
    return this.api.verifyOtp(email, otp);
  }

  /**
   * Send OTP for forgot password flow
   * @param email - User's email address
   * @returns Observable with API response
   */
  sendForgotPasswordOtp(email: string): Observable<any> {
    return this.api.sendForgotPasswordOtp(email);
  }

  /**
   * Reset password using OTP
   * @param email - User's email address
   * @param otp - OTP code
   * @param newPassword - New password to set
   * @returns Observable with API response
   */
  resetPassword(email: string, otp: string, newPassword: string): Observable<any> {
    return this.api.resetPassword(email, otp, newPassword);
  }

  /**
   * Get the current user's role from session storage
   */
  getUserRole(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return sessionStorage.getItem('role');
    }
    return null;
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!sessionStorage.getItem('token');
    }
    return false;
  }

  /**
   * Log out the user and clear session storage
   */
  logout() {
    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.clear();
    }
    this.router.navigate(['/login']);
    toast.info('Logged out successfully');
  }
}
