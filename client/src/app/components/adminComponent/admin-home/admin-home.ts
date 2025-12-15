import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AdminService } from '../../../services/adminService/admin-service';
import { AdminDashboardStats, Activity } from '../../../interfaces/admin.interfaces';

@Component({
  selector: 'app-admin-home',
  imports: [CommonModule],
  templateUrl: './admin-home.html',
  styleUrl: './admin-home.css',
})
export class AdminHome implements OnInit {
  // Dashboard statistics
  dashboardStats: AdminDashboardStats | null = null;
  recentActivities: Activity[] = [];

  // Loading states
  isLoadingStats = true;
  isLoadingActivities = true;

  constructor(
    private adminService: AdminService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadDashboardStatistics();
      this.loadRecentActivities();
    }
  }

  loadDashboardStatistics(): void {
    console.log('🟢 [AdminHome] Loading dashboard statistics...');
    this.isLoadingStats = true;
    this.adminService.getDashboardStatistics().subscribe({
      next: (response) => {
        console.log('🟢 [AdminHome] Received response:', response);
        if (response && response.data) {
          this.dashboardStats = response.data;
        } else {
          // Fallback if response itself is the data (depending on API service handling)
          this.dashboardStats = response.data || response;
        }
        console.log('🟢 [AdminHome] Dashboard stats assigned:', this.dashboardStats);
        console.log('🟢 [AdminHome] Total Users:', this.dashboardStats?.totalUsers);
        console.log(
          '🟢 [AdminHome] Active Consultations:',
          this.dashboardStats?.activeConsultations
        );
        console.log('🟢 [AdminHome] Pending Requests:', this.dashboardStats?.pendingRequests);
        this.isLoadingStats = false;
      },
      error: (error) => {
        console.error('🔴 [AdminHome] Error loading dashboard statistics:', error);
        console.error('🔴 [AdminHome] Error status:', error.status);
        console.error('🔴 [AdminHome] Error details:', error.error);
        this.isLoadingStats = false;
      },
    });
  }

  loadRecentActivities(): void {
    this.isLoadingActivities = true;
    this.adminService.getRecentActivities().subscribe({
      next: (response) => {
        this.recentActivities = response.data;
        this.isLoadingActivities = false;
      },
      error: (error) => {
        console.error('Error loading recent activities:', error);
        this.isLoadingActivities = false;
      },
    });
  }
}
