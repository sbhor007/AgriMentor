import { Component, EventEmitter, Output, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AdminService } from '../../../services/adminService/admin-service';
import { AdminDashboardStats } from '../../../interfaces/admin.interfaces';

@Component({
  selector: 'app-admin-navbar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './admin-navbar.html',
  styleUrl: './admin-navbar.css',
})
export class AdminNavbar implements OnInit {
  @Output() closeMenu = new EventEmitter<void>();

  dashboardStats: AdminDashboardStats | null = null;

  menuItems = [
    {
      title: 'Dashboard',
      route: '/admin/home',
      icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
      exact: true,
    },
    {
      title: 'Users Management',
      route: '/admin/users',
      icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z',
      badgeType: 'users',
    },
    {
      title: 'Consultations',
      route: '/admin/consultations',
      icon: 'M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z',
      badgeType: 'consultations',
    },
    {
      title: 'Pending Approvals',
      route: '/admin/approvals',
      icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
      badgeType: 'pending',
    },
    {
      title: 'Reports & Analytics',
      route: '/admin/reports',
      icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z',
    },
    {
      title: 'Global Feedbacks',
      route: '/admin/feedbacks',
      icon: 'M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z',
    },
    {
      title: 'System Settings',
      route: '/admin/settings',
      icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z',
    },
  ];

  constructor(
    private router: Router,
    private adminService: AdminService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadDashboardStats();
    }
  }

  loadDashboardStats(): void {
    console.log('🟣 [AdminNavbar] Subscribing to dashboard stats...');
    // Subscribe to the shared BehaviorSubject
    this.adminService.dashboardStats$.subscribe({
      next: (stats) => {
        if (stats) {
          console.log('🟣 [AdminNavbar] Received dashboard stats update:', stats);
          this.dashboardStats = stats;
        } else {
          // If no stats yet, trigger a fetch (optional, as AdminHome usually does it)
          // But to be safe if landing on other pages:
          console.log('🟣 [AdminNavbar] No stats in cache, triggering fetch...');
          this.adminService.getDashboardStatistics().subscribe();
        }
      },
      error: (error) => {
        console.error('🔴 [AdminNavbar] Error receiving dashboard stats:', error);
      },
    });
  }

  getBadge(type: string): string {
    if (!this.dashboardStats) return '';

    switch (type) {
      case 'users':
        return this.formatNumber(this.dashboardStats.totalUsers);
      case 'consultations':
        return this.formatNumber(this.dashboardStats.activeConsultations);
      case 'pending':
        return this.formatNumber(this.dashboardStats.pendingRequests);
      default:
        return '';
    }
  }

  getMenuBadge(menu: any): string {
    return menu.badgeType ? this.getBadge(menu.badgeType) : '';
  }

  formatNumber(num: number): string {
    if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
  }

  onLinkClick(): void {
    this.closeMenu.emit();
  }

  onLogout(): void {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}
