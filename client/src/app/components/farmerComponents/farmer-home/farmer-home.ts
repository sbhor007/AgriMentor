import { CommonModule, TitleCasePipe } from '@angular/common'; // Added CommonModule
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router'; // Added RouterLink
import { constants } from 'buffer';
import { Subscription } from 'rxjs';
import { FarmerService } from '../../../services/farmerService/farmer-service';
import { get } from 'http';

@Component({
  selector: 'app-farmer-home',
  imports: [CommonModule, RouterLink], // Added CommonModule and RouterLink
  templateUrl: './farmer-home.html',
  styleUrl: './farmer-home.css',
})
export class FarmerHome {
  today = new Date(); // Added today's date
  farmerProfile: any = null;
  consultationRequests: any[] = [];
  summaryCards = [
    { title: 'Active Crops', value: '3', sub: 'Rice, Wheat, Corn', color: 'border-green-400' },
    { title: 'Total Consultations', value: '0', sub: 'Loading...', color: 'border-blue-400' },
    { title: 'Farm Size', value: '15 acres', sub: 'Soil: Loamy', color: 'border-yellow-400' }, // Placeholder for now unless profile has this
    { title: 'Success Rate', value: '92%', sub: 'Based on feedback', color: 'border-green-500' },
  ];

  private subscription!: Subscription;

  constructor(
    private farmerService: FarmerService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.getFarmerProfile();
    this.getConsultationRequest();
  }

  getConsultationRequest() {
    this.subscription = this.farmerService.getConsultationRequest().subscribe((state: any) => {
      console.log('🟢 Received consultation requests:', state);
      if (state) {
        // Sort consultations by date - newest first
        this.consultationRequests = this.sortByNewest(state);
        console.log('✅ Sorted consultation requests (newest first):', this.consultationRequests);

        // Update summary card for consultations
        const totalIndex = this.summaryCards.findIndex((c) => c.title === 'Total Consultations');
        if (totalIndex !== -1) {
          const pending = this.consultationRequests.filter(
            (c) => c.consultationRequestStatus === 'PENDING'
          ).length;
          const approved = this.consultationRequests.filter(
            (c) => c.consultationRequestStatus === 'APPROVED'
          ).length;

          this.summaryCards[totalIndex].value = this.consultationRequests.length.toString();
          this.summaryCards[totalIndex].sub = `${pending} Pending, ${approved} Approved`;
        }

        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Sort consultations by date - newest first
   */
  sortByNewest(consultations: any[]): any[] {
    return consultations.sort((a, b) => {
      // Try to parse dates from different possible fields
      const dateA = this.parseDate(a.createdAt || a.date || a.requestDate);
      const dateB = this.parseDate(b.createdAt || b.date || b.requestDate);

      // Sort descending (newest first)
      return dateB.getTime() - dateA.getTime();
    });
  }

  /**
   * Parse date from various formats
   */
  parseDate(dateValue: any): Date {
    if (!dateValue) {
      return new Date(0); // Return epoch if no date
    }

    if (dateValue instanceof Date) {
      return dateValue;
    }

    // Try to parse string date
    const parsed = new Date(dateValue);
    return isNaN(parsed.getTime()) ? new Date(0) : parsed;
  }

  getFarmerProfile() {
    this.subscription = this.farmerService.getFarmerProfile().subscribe((state: any) => {
      console.log('🟢 Received farmer profile:', state);
      if (state) {
        this.farmerProfile = state;
        this.cdr.detectChanges();
      }
    });
  }

  navigateNewRequest() {
    this.router.navigate(['farmer/consultation-request']);
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  // Helpers for template
  getInitials(firstName: string, lastName: string): string {
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`;
  }
}
