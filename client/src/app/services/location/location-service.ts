import { Injectable, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../API/api-service';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  latitude = signal<string | null>(null);
  longitude = signal<string | null>(null);
  address = signal<string | null>(null);

  constructor(private api: ApiService) {}

  getCurrentLocation(): Promise<{ latitude: number; longitude: number; accuracy: number }> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        return reject('Geolocation is not supported by this browser.');
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lon = position.coords.longitude;
          this.latitude.set(lat.toString());
          this.longitude.set(lon.toString());

          resolve({
            latitude: lat,
            longitude: lon,
            accuracy: position.coords.accuracy,
          });
        },
        (error) => {
          switch (error.code) {
            case error.PERMISSION_DENIED:
              reject('User denied location permission.');
              break;
            case error.POSITION_UNAVAILABLE:
              reject('Location unavailable.');
              break;
            case error.TIMEOUT:
              reject('Location request timed out.');
              break;
            default:
              reject('Unknown error occurred.');
          }
        },
        {
          enableHighAccuracy: true, // 🔥 More accurate GPS
          timeout: 10000,
          maximumAge: 0,
        }
      );
    });
  }

  getAddress(lat: number, lng: number) {
    return this.api.getAddress(lat, lng).subscribe({
      next: (res) => {
        console.log('LOCATION_SERVICE:getAddress::RES:  ', res);
        if (res && res.display_name) {
          this.latitude.set(res.latitude);
          this.longitude.set(res.longitude);
          this.address.set(res.display_name);
        }
      },
      error: (err) => {
        console.log('LOCATION_SERVICE:getAddress::ERROR:  ', err);
      },
    });
  }
}
