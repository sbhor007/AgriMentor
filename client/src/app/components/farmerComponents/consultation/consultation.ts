import { CommonModule, TitleCasePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FarmerService } from '../../../services/farmerService/farmer-service';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-consultation',
  imports: [TitleCasePipe, CommonModule, FormsModule],
  templateUrl: './consultation.html',
  styleUrl: './consultation.css',
})
export class Consultation implements OnInit {
  // ---------- TABS ----------
  tabs = [
    { key: 'approved', label: 'Approved' },
    { key: 'pending', label: 'Pending' },
    { key: 'completed', label: 'Completed' },
    { key: 'rejected', label: 'Rejected' },
  ];

  activeTab: string = 'pending'; // Default because backend usually returns pending first
  isLoading = false;
  searchQuery: string = '';
  private subscription!: Subscription;

  // ---------- CONSULTATION LIST (AFTER MAPPING) ----------
  consultationRequests: any[] = [];

  constructor(
    private router: Router,
    private farmerService: FarmerService,
    private cdr: ChangeDetectorRef
  ) {}

  // ---------- LIFECYCLE ----------
  ngOnInit(): void {
    this.fetchConsultationRequests();
  }

  // ---------- LOAD CONSULTATION REQUESTS ----------
  fetchConsultationRequests() {
    this.isLoading = true;

    this.subscription = this.farmerService.getConsultationRequest().subscribe({
      next: (response: any) => {
        console.log('🔵 Backend Raw Response:', response);

        // Ensure response.data exists
        const apiData = response || [];

        // MAP ONLY REQUIRED FIELDS
        this.consultationRequests = apiData.map((item: any) => ({
          id: item.id,
          topic: item.topic,
          consultationRequestStatus: item.consultationRequestStatus,
          createdAt: item.createdAt,
          date: new Date(item.createdAt).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
          }),
          consultant: {
            firstName: item.consultant?.firstName || 'N/A',
            lastName: item.consultant?.lastName || '',
            specialization: item.consultant?.specialization || 'Agriculture Expert',
          },
        }));

        console.log('🟢 Mapped Consultation Requests:', this.consultationRequests);

        this.isLoading = false;
        this.cdr.detectChanges();
      },

      error: (err) => {
        console.error('🔴 Error fetching consultations:', err);
        this.isLoading = false;
      },
    });

    console.log('Fetching consultation requests...');
  }

  // ---------- FILTER BY SEARCH GLOBALLY, THEN BY ACTIVE TAB ----------
  filteredConsultations() {
    let filtered = this.consultationRequests;

    // Apply search filter FIRST (globally across all consultations)
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter((c) => {
        const consultantName = `${c.consultant.firstName} ${c.consultant.lastName}`.toLowerCase();
        const topic = c.topic.toLowerCase();
        const specialization = c.consultant.specialization.toLowerCase();
        const status = c.consultationRequestStatus.toLowerCase();

        return (
          consultantName.includes(query) ||
          topic.includes(query) ||
          specialization.includes(query) ||
          status.includes(query)
        );
      });
    }

    // Then filter by active tab
    filtered = filtered.filter((c) => c.consultationRequestStatus.toLowerCase() === this.activeTab);

    return filtered;
  }

  // ---------- GET COUNT FOR SPECIFIC TAB ----------
  getCountForTab(tabKey: string): number {
    return this.consultationRequests.filter(
      (c) => c.consultationRequestStatus.toLowerCase() === tabKey
    ).length;
  }

  // ---------- NAVIGATION ----------
  navigateNewRequest() {
    console.log('New consultation request clicked');
    this.router.navigate(['/farmer/consultation-request']);
  }

  navigateToConsultationDetails(id: number, consultation: any) {
    console.log('Consultation Details:', id, consultation);
    this.router.navigate(['/farmer/consultation-request', id]);
  }
}
