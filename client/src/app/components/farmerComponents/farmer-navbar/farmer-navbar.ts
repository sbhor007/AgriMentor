import { CommonModule, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-farmer-navbar',
  imports: [RouterModule, CommonModule],
  templateUrl: './farmer-navbar.html',
  styleUrl: './farmer-navbar.css',
})
export class FarmerNavbar {
  @Output() closeMenu = new EventEmitter<void>();
  isMobileMenuOpen = false;
  isInitialized = false;

  menuItems = [
    {
      title: 'Dashboard',
      route: 'dashboard',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6">
      </path>
    `,
    },
    {
      title: 'Consultations',
      route: 'consultation',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0z">
      </path>
    `,
    },
    {
      title: 'Consultation Request',
      route: 'consultation-request',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4">
      </path>
    `,
    },
    {
      title: 'Weather',
      route: '/farmer/weather',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z">
      </path>
    `,
    },
    {
      title: 'Chat',
      route: '/farmer/chat',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z">
      </path>
    `,
    },
    {
      title: 'Consultants',
      route: '/farmer/consultants',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z">
      </path>
    `,
    },
    {
      title: 'Profile',
      route: '/farmer/profile',
      icon: `
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z">
      </path>
    `,
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
