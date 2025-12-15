import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/adminService/admin-service';
import { ConsultationOverview } from '../../../interfaces/admin.interfaces';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-admin-consultations',
  imports: [CommonModule],
  templateUrl: './admin-consultations.html',
  styleUrl: './admin-consultations.css',
})
export class AdminConsultations implements OnInit {
  consultationStats: ConsultationOverview | null = null;
  consultations: any[] = [];
  isLoading = true;

  // Computed properties for template
  get totalConsultations(): number {
    return this.consultationStats?.totalConsultations || 0;
  }

  get activeConsultations(): number {
    return this.consultationStats?.activeConsultations || 0;
  }

  get completedConsultations(): number {
    return this.consultationStats?.completedConsultations || 0;
  }

  get pendingConsultations(): number {
    return this.consultationStats?.pendingConsultations || 0;
  }

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadConsultationOverview();
    this.loadAllConsultations();
  }

  loadConsultationOverview(): void {
    this.adminService.getConsultationOverview().subscribe({
      next: (response) => {
        this.consultationStats = response.data;
      },
      error: (error) => {
        console.error('Error loading consultation overview:', error);
        toast.error('Failed to load consultation statistics');
      },
    });
  }

  loadAllConsultations(): void {
    this.isLoading = true;
    this.adminService.getAllConsultations().subscribe({
      next: (response) => {
        this.consultations = response.data || [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading consultations:', error);
        toast.error('Failed to load consultations');
        this.isLoading = false;
      },
    });
  }
}
