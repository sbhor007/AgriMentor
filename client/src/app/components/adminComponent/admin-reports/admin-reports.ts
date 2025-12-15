import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AdminService } from '../../../services/adminService/admin-service';
import { UserStatistics, ConsultationOverview } from '../../../interfaces/admin.interfaces';

@Component({
  selector: 'app-admin-reports',
  imports: [CommonModule],
  templateUrl: './admin-reports.html',
  styleUrl: './admin-reports.css',
})
export class AdminReports implements OnInit {
  userStats: UserStatistics | null = null;
  consultationStats: ConsultationOverview | null = null;
  isLoading = true;

  constructor(
    private adminService: AdminService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadStatistics();
    }
  }

  loadStatistics(): void {
    this.isLoading = true;

    // Load user statistics
    this.adminService.getUserStatistics().subscribe({
      next: (response) => {
        this.userStats = response.data || null;
      },
      error: (error) => {
        console.error('Error loading user statistics:', error);
      },
    });

    // Load consultation statistics
    this.adminService.getConsultationStatistics().subscribe({
      next: (response) => {
        this.consultationStats = response.data || null;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading consultation statistics:', error);
        this.isLoading = false;
      },
    });
  }
}
