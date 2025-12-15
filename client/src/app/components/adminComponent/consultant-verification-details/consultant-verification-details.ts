import { Component, OnInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../../services/adminService/admin-service';
import { toast } from 'ngx-sonner';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-consultant-verification-details',
  imports: [CommonModule],
  templateUrl: './consultant-verification-details.html',
  styleUrl: './consultant-verification-details.css',
})
export class ConsultantVerificationDetails implements OnInit {
  consultant: any = null;
  isLoading = true;
  consultantId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adminService: AdminService,
    private sanitizer: DomSanitizer,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.route.paramMap.subscribe((params) => {
        this.consultantId = params.get('id');
        if (this.consultantId) {
          this.loadConsultantDetails(this.consultantId);
        }
      });
    }
  }

  loadConsultantDetails(id: string): void {
    this.isLoading = true;
    // Since we don't have a single get-consultant API, we fetch all and filter
    this.adminService.getAllConsultants().subscribe({
      next: (response) => {
        const allConsultants = response.data || [];
        // Try matching by ID first, then by email if ID is string based
        this.consultant = allConsultants.find(
          (c: any) => c.id == id || c.email === id || c.firstName === id
        );
        setTimeout(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        });
      },
      error: (error) => {
        console.error('Error loading consultant details:', error);
        toast.error('Failed to load consultant details');
        setTimeout(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        });
      },
    });
  }

  verifyConsultant(): void {
    if (!this.consultant) return;

    this.adminService.verifyConsultant(this.consultant.email).subscribe({
      next: (response) => {
        toast.success('Consultant verified successfully');
        this.router.navigate(['/admin/approvals']);
      },
      error: (error) => {
        console.error('Error verifying consultant:', error);
        toast.error('Failed to verify consultant');
      },
    });
  }

  rejectConsultant(): void {
    if (!this.consultant) return;

    this.adminService.rejectConsultant(this.consultant.email).subscribe({
      next: (response) => {
        toast.success('Consultant rejected successfully');
        this.router.navigate(['/admin/approvals']);
      },
      error: (error) => {
        console.error('Error rejecting consultant:', error);
        toast.error('Failed to reject consultant');
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/approvals']);
  }

  isPdf(url: string | undefined): boolean {
    return url ? url.toLowerCase().endsWith('.pdf') : false;
  }

  getSafeUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}
