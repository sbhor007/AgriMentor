import { CommonModule, TitleCasePipe } from '@angular/common'; // Added CommonModule
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router'; // Added RouterLink
import { constants } from 'buffer';
import { Subscription } from 'rxjs';
import { FarmerService } from '../../../services/farmerService/farmer-service';
import { get } from 'http';
import { WeatherService } from '../../../services/weather/weather.service';
import { LocationService } from '../../../services/location/location-service';

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
  weather: any = null;
  summaryCards = [
    { title: 'Active Crops', value: '3', sub: 'Rice, Wheat, Corn', color: 'border-green-400' },
    { title: 'Total Consultations', value: '0', sub: 'Loading...', color: 'border-blue-400' },
    { title: 'Farm Size', value: '--', sub: 'Loading...', color: 'border-yellow-400' },
  ];

  private subscription!: Subscription;

  constructor(
    private farmerService: FarmerService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private weatherService: WeatherService,
    private locationService: LocationService
  ) {}

  ngOnInit() {
    this.getFarmerProfile();
    this.getConsultationRequest();
    this.initializeLocationAndWeather();
  }

  async initializeLocationAndWeather() {
    try {
      // 1. Get current location
      const coords = await this.locationService.getCurrentLocation();
      console.log('📍 Current Location:', coords);

      // 2. Fetch address details (updates service signals)
      this.locationService.getAddress(coords.latitude, coords.longitude);

      // 3. Fetch Weather
      this.getWeatherDataByLatLon(coords.latitude, coords.longitude);

      // Optional: Get extended forecast
      // this.getPastAndFutureWeather(coords.latitude, coords.longitude);
    } catch (error) {
      console.error('❌ Error getting location:', error);
      // Fallback to default city if location fails
      this.getWeatherDataByCity('Pune');
    }
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

        // Update Farm Size card with dynamic data
        const farmSizeIndex = this.summaryCards.findIndex((c) => c.title === 'Farm Size');
        if (farmSizeIndex !== -1) {
          const farmArea = state.farmAreaHectares || 0;
          const soilType = this.formatSoilType(state.soilType) || 'Unknown';

          this.summaryCards[farmSizeIndex].value = farmArea > 0 ? `${farmArea} ha` : 'Not set';
          this.summaryCards[farmSizeIndex].sub = `Soil: ${soilType}`;
        }

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

  /**
   * Format soil type for display
   */
  formatSoilType(soilType: string): string {
    if (!soilType) return 'Unknown';

    // Convert enum to readable format
    const soilTypeMap: { [key: string]: string } = {
      CLAY: 'Clay',
      SANDY: 'Sandy',
      LOAMY: 'Loamy',
      SILT: 'Silt',
      PEATY: 'Peaty',
      CHALKY: 'Chalky',
      SALINE: 'Saline',
    };

    return soilTypeMap[soilType] || soilType;
  }

  /*
  get weather data by city
  */
  getWeatherDataByCity(city: string) {
    this.weatherService.getWeatherDataByCity(city).subscribe((state: any) => {
      console.log('🟢 Received weather data:', state);
      if (state) {
        this.weather = state;
        this.cdr.detectChanges();
      }
    });
  }

  /*
   *get weather data by latitude and longitude
   */
  getWeatherDataByLatLon(lat: any, lon: any) {
    this.weatherService.getWeatherDataByLatLon(lat, lon).subscribe((state: any) => {
      console.log('🟢 Received weather data:', state);
      if (state) {
        this.weather = state;
        this.cdr.detectChanges();
      }
    });
  }

  /*
   *get past and future weather data by latitude and longitude
   */
  getPastAndFutureWeather(lat: number, lon: number) {
    this.weatherService.getPastAndFutureWeather(lat, lon).subscribe((state: any) => {
      console.log('🟢 Received weather data:', state);
      if (state) {
        this.weather = state;
        this.cdr.detectChanges();
      }
    });
  }
}
