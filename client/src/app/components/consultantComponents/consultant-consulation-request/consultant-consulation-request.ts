import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { ConsultationService } from '../../../services/consultationService/consultation-service';
import { FarmVisitiongSchedule } from '../farm-visitiong-schedule/farm-visitiong-schedule';
import { FormsModule } from '@angular/forms';

interface ConsultationRequest {
  id: number;
  topic: string;
  description: string;
  consultationRequestStatus: string;
  createdAt: string;
  date: string; // Add formatted date
  farmer: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
  };
  crop: {
    name: string;
    category: string;
    type: string;
    description: string;
  };
  farmAddress: {
    street: string;
    city: string;
    state: string;
    pinCode: string;
    country: string;
  };
}

@Component({
  selector: 'app-consultant-consulation-request',
  imports: [CommonModule, DatePipe, FarmVisitiongSchedule, FormsModule],
  templateUrl: './consultant-consulation-request.html',
  styleUrl: './consultant-consulation-request.css',
})
export class ConsultantConsulationRequest implements OnInit, OnDestroy {
  tabs = [
    {
      key: 'pending',
      label: 'New Requests',
      icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
      color: 'amber',
    },
    {
      key: 'approved',
      label: 'Approved',
      icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
      color: 'green',
    },
    { key: 'completed', label: 'Completed', icon: 'M5 13l4 4L19 7', color: 'blue' },
    { key: 'rejected', label: 'Rejected', icon: 'M6 18L18 6M6 6l12 12', color: 'red' },
  ];

  activeTab = 'pending';
  isLoading = false;
  consultationRequests: ConsultationRequest[] = [];
  filteredRequests: ConsultationRequest[] = []; // For search results
  searchQuery = ''; // Search input (renamed from searchTerm for consistency)
  selectedConsultation: ConsultationRequest | null = null;
  showDetailsModal = false;
  showScheduleModal = false;
  showActionConfirmModal = false;
  actionType: 'approve' | 'reject' | null = null;
  private subscription!: Subscription;

  constructor(
    private router: Router,
    private consultationService: ConsultationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadConsultations();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  loadConsultations(): void {
    this.isLoading = true;

    // Subscribe to the BehaviorSubject observable
    this.subscription = this.consultationService.listConsultationData$.subscribe({
      next: (apiData: any) => {
        if (!apiData) {
          // Data not yet loaded, trigger initial fetch
          this.consultationService.getConsultationRequests();
          return;
        }

        console.log('🔵 Backend Raw Response:', apiData);

        // MAP ONLY REQUIRED FIELDS
        this.consultationRequests = apiData.map((item: any) => ({
          id: item.id,
          topic: item.topic,
          description: item.description,
          consultationRequestStatus: item.consultationRequestStatus,
          createdAt: item.createdAt,
          farmer: {
            firstName: item.farmer?.firstName || 'N/A',
            lastName: item.farmer?.lastName || '',
            email: item.farmer?.email || '',
            phone: item.farmer?.phone || '',
          },
          crop: {
            name: item.crop?.name || 'N/A',
            category: item.crop?.category || '',
            type: item.crop?.type || '',
            description: item.crop?.description || '',
          },
          farmAddress: {
            street: item.farmAddress?.street || 'N/A',
            city: item.farmAddress?.city || '',
            state: item.farmAddress?.state || '',
            pinCode: item.farmAddress?.pinCode || '',
            country: item.farmAddress?.country || '',
          },
          date: new Date(item.createdAt).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
          }),
        }));

        console.log('🟢 Mapped Consultation Requests:', this.consultationRequests);

        // Initialize filtered requests
        this.applyFilters();

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('🔴 Error in subscription:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  // Get count for a specific status (for tab badges)
  getCountByStatus(status: string): number {
    return this.consultationRequests.filter(
      (c) => c.consultationRequestStatus.toLowerCase() === status.toLowerCase()
    ).length;
  }

  // Filter consultations: GLOBAL SEARCH FIRST, then by active tab
  applyFilters(): void {
    let filtered = this.consultationRequests;

    // Apply search filter FIRST (globally across all consultations)
    if (this.searchQuery.trim()) {
      const search = this.searchQuery.toLowerCase();
      filtered = filtered.filter((c) => {
        const farmerName = `${c.farmer.firstName} ${c.farmer.lastName}`.toLowerCase();
        const topic = c.topic.toLowerCase();
        const cropName = c.crop.name.toLowerCase();
        const city = c.farmAddress.city.toLowerCase();
        const status = c.consultationRequestStatus.toLowerCase();

        return (
          farmerName.includes(search) ||
          topic.includes(search) ||
          cropName.includes(search) ||
          city.includes(search) ||
          status.includes(search)
        );
      });
    }

    // Then filter by active tab
    filtered = filtered.filter((c) => c.consultationRequestStatus.toLowerCase() === this.activeTab);

    this.filteredRequests = filtered;
  }

  // Handle search input
  onSearch(event: any): void {
    this.searchQuery = event.target.value;
    this.applyFilters();
  }

  // Handle tab change
  onTabChange(tabKey: string): void {
    this.activeTab = tabKey;
    this.applyFilters();
  }

  // Get filtered consultations for display
  filteredConsultations(): ConsultationRequest[] {
    return this.filteredRequests;
  }

  openDetailsModal(consultation: ConsultationRequest): void {
    this.selectedConsultation = consultation;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    // Don't reset selectedConsultation here - might need it for subsequent actions
  }

  openActionConfirm(consultation: ConsultationRequest, action: 'approve' | 'reject'): void {
    console.log('🔵 Open Action Confirm:', consultation.id, action);
    this.selectedConsultation = consultation;
    this.actionType = action;
    this.showActionConfirmModal = true;
    this.cdr.detectChanges(); // Force change detection
  }

  closeActionConfirm(): void {
    this.showActionConfirmModal = false;
    this.actionType = null;
    // Don't reset selectedConsultation yet - needed for schedule modal
  }

  confirmAction(): void {
    if (!this.selectedConsultation || !this.actionType) {
      console.error('❌ Missing selectedConsultation or actionType');
      return;
    }

    console.log(
      '🟢 Confirming action:',
      this.actionType,
      'for consultation:',
      this.selectedConsultation.id
    );

    // Store consultation ID before any async operations
    const consultationId = this.selectedConsultation.id;
    const action = this.actionType;

    if (action === 'approve') {
      // Close confirmation modal first
      this.closeActionConfirm();

      // Call the API to accept
      this.consultationService.acceptConsultationRequest(consultationId);

      // Small delay to ensure modal transitions properly
      setTimeout(() => {
        this.openScheduleModal();
      }, 100);
    } else {
      // Call the API to reject
      this.consultationService.rejectConsultationRequest(consultationId);

      // Close modals
      this.closeActionConfirm();
      this.selectedConsultation = null;

      // Show feedback
      console.log('✅ Consultation rejected successfully');
    }
    this.consultationService.getListConsultationRequestsData();
    this.loadConsultations();
  }

  openScheduleModal(): void {
    console.log('🟢 Opening schedule modal for consultation:', this.selectedConsultation?.id);
    this.showScheduleModal = true;
    this.router.navigate(['/farm-schedule', this.selectedConsultation?.id]);
    this.cdr.detectChanges(); // Force change detection
  }

  closeScheduleModal(): void {
    console.log('🔵 Closing schedule modal');
    this.showScheduleModal = false;
    this.selectedConsultation = null;
  }

  handleScheduledVisit(data: any) {
    debugger;
    console.log('📅 Farm visit scheduled:', data);

    // Call the service to schedule the visit
    // const consultationId = data.consultationId;
    const scheduledData = {
      scheduledDate: `${data.date}T${data.time}`,
      visitNotes: data.note,
      visitStatus: 'SCHEDULED',
    };
    debugger;
    // this.consultationService.scheduleFarmVisit(scheduledData, consultationId);
    console.log('workde');
  }

  getTabColor(key: string): string {
    const tab = this.tabs.find((t) => t.key === key);
    return tab?.color || 'gray';
  }

  getStatusBadgeClass(status: string): string {
    const statusLower = status.toLowerCase();
    const colors: any = {
      pending: 'bg-amber-100 text-amber-700 border-amber-200',
      approved: 'bg-green-100 text-green-700 border-green-200',
      completed: 'bg-blue-100 text-blue-700 border-blue-200',
      rejected: 'bg-red-100 text-red-700 border-red-200',
    };
    return colors[statusLower] || 'bg-gray-100 text-gray-700';
  }

  getInitials(firstName: string, lastName: string): string {
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`;
  }

  /*
  navigate to consultation details
  */
  navigateToConsultationDetails(id: number): void {
    console.log('🟢 Navigating to consultation details for consultation:', id);
    this.router.navigate(['/consultant/consultation-details', id]);
  }
}
