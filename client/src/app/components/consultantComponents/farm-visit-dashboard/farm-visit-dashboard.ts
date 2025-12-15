import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FarmVisit } from '../../../services/farmVisit/farm-visit';

interface FarmVisitData {
  id: number;
  consultationId: number;
  consultationTopic: string;
  scheduledDate: string;
  visitNotes: string;
  visitStatus: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'MISSED';
  farmAddress: {
    street: string;
    city: string;
    state: string;
    pinCode: string;
    country: string;
  };
  farmerName?: string;
  isOverdue: boolean;
}

@Component({
  selector: 'app-farm-visit-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './farm-visit-dashboard.html',
  styleUrl: './farm-visit-dashboard.css',
})
export class FarmVisitDashboard implements OnInit {
  allVisits: FarmVisitData[] = [];
  filteredVisits: FarmVisitData[] = [];
  isLoading = true;
  activeTab: 'today' | 'upcoming' | 'completed' | 'canceled' | 'missed' = 'today';
  searchQuery = '';

  constructor(private farmVisitService: FarmVisit, private router: Router) {}

  ngOnInit() {
    this.loadAllVisits();
  }

  loadAllVisits() {
    this.isLoading = true;
    this.farmVisitService.getAllConsultantVisits().subscribe({
      next: (res: any) => {
        console.log('✅ Farm visits loaded:', res);
        this.allVisits = res.data || [];
        this.filterVisits();
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('❌ Error loading farm visits:', err);
        this.isLoading = false;
      },
    });
  }

  filterVisits() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let filtered = this.allVisits;

    // Filter by tab
    switch (this.activeTab) {
      case 'today':
        filtered = this.allVisits.filter((v) => {
          const visitDate = new Date(v.scheduledDate);
          visitDate.setHours(0, 0, 0, 0);
          return v.visitStatus === 'SCHEDULED' && visitDate.getTime() === today.getTime();
        });
        break;
      case 'upcoming':
        filtered = this.allVisits.filter((v) => {
          const visitDate = new Date(v.scheduledDate);
          return v.visitStatus === 'SCHEDULED' && visitDate > today;
        });
        break;
      case 'completed':
        filtered = this.allVisits.filter((v) => v.visitStatus === 'COMPLETED');
        break;
      case 'canceled':
        filtered = this.allVisits.filter((v) => v.visitStatus === 'CANCELLED');
        break;
      case 'missed':
        filtered = this.allVisits.filter((v) => v.visitStatus === 'MISSED');
        break;
    }

    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(
        (v) =>
          v.consultationTopic?.toLowerCase().includes(query) ||
          v.farmerName?.toLowerCase().includes(query) ||
          v.farmAddress?.city?.toLowerCase().includes(query)
      );
    }

    this.filteredVisits = filtered.sort(
      (a, b) => new Date(a.scheduledDate).getTime() - new Date(b.scheduledDate).getTime()
    );
  }

  switchTab(tab: typeof this.activeTab) {
    this.activeTab = tab;
    this.filterVisits();
  }

  getTabCount(tab: typeof this.activeTab): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    switch (tab) {
      case 'today':
        return this.allVisits.filter((v) => {
          const visitDate = new Date(v.scheduledDate);
          visitDate.setHours(0, 0, 0, 0);
          return v.visitStatus === 'SCHEDULED' && visitDate.getTime() === today.getTime();
        }).length;
      case 'upcoming':
        return this.allVisits.filter((v) => {
          const visitDate = new Date(v.scheduledDate);
          return v.visitStatus === 'SCHEDULED' && visitDate > today;
        }).length;
      case 'completed':
        return this.allVisits.filter((v) => v.visitStatus === 'COMPLETED').length;
      case 'canceled':
        return this.allVisits.filter((v) => v.visitStatus === 'CANCELLED').length;
      case 'missed':
        return this.allVisits.filter((v) => v.visitStatus === 'MISSED').length;
      default:
        return 0;
    }
  }

  completeVisit(visitId: number) {
    if (confirm('Mark this visit as completed?')) {
      this.farmVisitService.completeVisit(visitId).subscribe({
        next: () => {
          console.log('✅ Visit completed');
          this.loadAllVisits();
        },
        error: (err) => console.error('❌ Error:', err),
      });
    }
  }

  cancelVisit(visitId: number) {
    if (confirm('Cancel this visit?')) {
      this.farmVisitService.cancelVisit(visitId).subscribe({
        next: () => {
          console.log('✅ Visit canceled');
          this.loadAllVisits();
        },
        error: (err) => console.error('❌ Error:', err),
      });
    }
  }

  viewDetails(consultationId: number) {
    this.router.navigate(['/consultant/consultations', consultationId]);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
