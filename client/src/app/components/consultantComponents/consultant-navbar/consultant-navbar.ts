import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-consultant-navbar',
  imports: [RouterModule, CommonModule],
  templateUrl: './consultant-navbar.html',
  styleUrl: './consultant-navbar.css',
})
export class ConsultantNavbar {
  @Output() closeMenu = new EventEmitter<void>();
  isMobileMenuOpen = false;
  isInitialized = false;

  menuItems = [
    {
      title: 'Dashboard',
      route: 'dashboard',
      icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
    },
    {
      title: 'Consultation Request',
      route: 'consultation-request',
      icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    },
    {
      title: 'Chat',
      route: '/consultant/chat',
      icon: 'M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z',
    },
    {
      title: 'Weather',
      route: '/consultant/weather',
      icon: 'M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z',
    },
    {
      title: 'Farm Visit Schedule',
      route: '/consultant/farm-visits',
      icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
    },
    {
      title: 'Feedbacks',
      route: '/consultant/feedbacks',
      icon: 'M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z',
    },
    {
      title: 'Profile',
      route: '/consultant/profile',
      icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z',
    },
  ];

  constructor(private router: Router, private cdr: ChangeDetectorRef) {}

  ngAfterViewInit() {
    // Mark the component as initialized
    this.isInitialized = true;
    // Trigger change detection
    this.cdr.detectChanges();
  }

  // Close mobile menu when a link is clicked
  onLinkClick() {
    this.closeMenu.emit();
  }

  // Handle logout
  onLogout() {
    // Add your logout logic here
    console.log('Logging out...');

    // Close menu
    this.closeMenu.emit();

    // Example: Clear auth token and redirect
    // localStorage.removeItem('authToken');
    // this.router.navigate(['/login']);

    // For now, just navigate to login
    this.router.navigate(['/login']);
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }
}
