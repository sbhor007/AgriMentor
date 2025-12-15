import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass, NgStyle } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth/auth-service';

@Component({
  selector: 'app-login',
  imports: [NgClass, NgStyle, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
  schemas: [NO_ERRORS_SCHEMA],
})
export class Login {
  roles = ['FARMER', 'CONSULTANT', 'ADMIN'];
  activeRole = 'FARMER';
  customDelay: number = 0;
  image = 'images/farmer.svg';

  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService) {
    this.customDelay = 0;
    console.log('image::');

    console.log(this.image);

    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      role: [this.activeRole, Validators.required],
    });
  }

  // get customDelay(): number {
  //   return 380 + i * 60;
  // }

  setRole(role: string) {
    if (this.activeRole !== role) {
      this.activeRole = role;
      if (role == 'FARMER') {
        this.image = 'images/farmer.svg';
      } else if (role == 'CONSULTANT') {
        this.image = 'images/admin.svg';
      } else {
        this.image = 'images/farmer.svg';
      }

      this.loginForm.patchValue({ role: role });
    }
  }

  get f() {
    return this.loginForm.controls;
  }

  getResponsiveTranslate() {
    const index = this.roles.indexOf(this.activeRole);

    // Check if window is defined (browser environment)
    if (typeof window !== 'undefined') {
      if (window.innerWidth < 640) {
        // sm
        return `translateX(${index * 310}%)`;
      } else if (window.innerWidth < 1024) {
        // md
        return `translateX(${index * 320}%)`;
      }
    }
    // Default for server-side rendering and lg screens
    return `translateX(${index * 320}%)`;
  }

  onSubmit() {
    console.log(this.loginForm.value);
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    console.log('Logging in as:', this.activeRole);
    console.log('Form Value:', this.loginForm.value);

    this.authService.login(this.loginForm.value);
  }

  toRegister() {
    if (this.activeRole === 'FARMER') {
      console.log('Farmer router');
      this.router.navigate(['register/farmer']);
    } else if (this.activeRole === 'CONSULTANT') {
      console.log('admin router');
      this.router.navigate(['register/consultant']);
    }
  }
}
