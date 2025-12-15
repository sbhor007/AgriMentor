import { ChangeDetectorRef, Component, HostListener, Inject, inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { App, RouteAnimations} from '../../app';
import { FarmerNavbar } from "../../components/farmerComponents/farmer-navbar/farmer-navbar";
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Subscription } from 'rxjs';
import { FarmerService } from '../../services/farmerService/farmer-service';

@Component({
  selector: 'app-farmer-dashboard-layout',
  imports: [RouterOutlet, FarmerNavbar,CommonModule],
  templateUrl: './farmer-dashboard-layout.html',
  styleUrl: './farmer-dashboard-layout.css',
  animations: [RouteAnimations]
})
export class FarmerDashboardLayout implements OnInit, OnDestroy {
  private app = Inject(App);
  farmerProfile: any = null;
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
    private farmerService: FarmerService,
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
    this.subscription = this.farmerService.getFarmerProfile().subscribe((state: any) => {
      console.log('🟢 Received farmer profile from service:', state);

      this.farmerProfile = state;

      // FIX: Avoid ExpressionChangedAfterItHasBeenCheckedError
      this.cdr.detectChanges();
      console.log('🛠 ChangeDetectorRef.detectChanges() called — view updated');
    });
    console.log('farmarProfile');
    console.log(this.farmerProfile);
    
  }


  
}
