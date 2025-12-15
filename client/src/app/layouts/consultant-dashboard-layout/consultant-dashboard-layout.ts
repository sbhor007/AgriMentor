import { ChangeDetectorRef, Component, inject, Inject, PLATFORM_ID } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { FarmerNavbar } from '../../components/farmerComponents/farmer-navbar/farmer-navbar';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { App, RouteAnimations } from '../../app';
import { Subscription } from 'rxjs';
import { ConsultantService } from '../../services/consultantService/consultant-service';
import { ConsultantNavbar } from "../../components/consultantComponents/consultant-navbar/consultant-navbar";

@Component({
  selector: 'app-consultant-dashboard-layout',
  imports: [RouterOutlet, CommonModule, ConsultantNavbar],
  templateUrl: './consultant-dashboard-layout.html',
  styleUrl: './consultant-dashboard-layout.css',
  animations: [RouteAnimations]
})
export class ConsultantDashboardLayout {
  private app = inject(App);
  consultantProfile: any = null;
    private subscription!: Subscription;
  

  prepareRoute(outlet: RouterOutlet) {
    // return in next tick to avoid ExpressionChangedAfterItHasBeenCheckedError
    return Promise.resolve().then(() => this.app.prepareRoute(outlet));
  }

  isMobileMenuOpen = false;
  private isBrowser!: boolean;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private router: Router,
    private consultantService: ConsultantService,
    private cdr: ChangeDetectorRef

  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      // Initialize any browser-specific code here
      this.checkScreenSize();
      window.addEventListener('resize', this.onResize.bind(this));
    }
    this.getFarmerProfile();
  }

  ngOnDestroy(): void {
    if (this.isBrowser) {
      // Clean up event listeners
      window.removeEventListener('resize', this.onResize.bind(this));
    }
  }

  toggleMobileMenu(): void {
    if (this.isBrowser) {
      this.isMobileMenuOpen = !this.isMobileMenuOpen;
    }
    if (this.subscription) {
      this.subscription.unsubscribe();
      console.log('🧹 Subscription cleaned up in ngOnDestroy()');
    }
  }

  closeMobileMenu(): void {
    if (this.isBrowser) {
      this.isMobileMenuOpen = false;
    }
  }

  private onResize(): void {
    if (this.isBrowser && window.innerWidth >= 1024) {
      this.isMobileMenuOpen = false;
    }
  }

  private checkScreenSize(): void {
    if (this.isBrowser) {
      this.isMobileMenuOpen = window.innerWidth < 1024;
    }
  }

  // Example of a method that uses browser APIs
  scrollToTop(): void {
    if (this.isBrowser) {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  // Example of a method that navigates
  navigateTo(route: string): void {
    if (this.isBrowser) {
      this.router.navigate([`/farmer/${route}`]);
      this.closeMobileMenu();
    }
  }
  

  getFarmerProfile(){
    this.subscription = this.consultantService.getConsultantProfile().subscribe((state: any) => {
      console.log('🟢 Received consultant profile from service:', state);

      this.consultantProfile = state;

      // FIX: Avoid ExpressionChangedAfterItHasBeenCheckedError
      this.cdr.detectChanges();
      console.log('🛠 ChangeDetectorRef.detectChanges() called — view updated');
    });
    console.log('consultantProfile');
    console.log(this.consultantProfile);
    
  }
}
