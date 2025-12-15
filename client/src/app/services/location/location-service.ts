import { Injectable, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../API/api-service';

@Injectable({
  providedIn: 'root',
})
export class LocationService {

  latitude = signal<string | null>(null);
  longitude = signal<string | null>(null);


constructor(private api:ApiService) {}

  getCurrentLocation(): Promise<{ latitude: number; longitude: number; accuracy: number }> {
    return new Promise((resolve, reject) => {

      if (!navigator.geolocation) {
        return reject("Geolocation is not supported by this browser.");
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy
          });
        },
        (error) => {
          switch (error.code) {
            case error.PERMISSION_DENIED:
              reject("User denied location permission.");
              break;
            case error.POSITION_UNAVAILABLE:
              reject("Location unavailable.");
              break;
            case error.TIMEOUT:
              reject("Location request timed out.");
              break;
            default:
              reject("Unknown error occurred.");
          }
        },
        {
          enableHighAccuracy: true, // 🔥 More accurate GPS
          timeout: 10000,
          maximumAge: 0
        }
      );

    });
  }

  getAddress(lat: number, lng: number){
    return this.api.getAddress(lat,lng).subscribe({
      next: (res) =>{
        console.log("LOCATION_SERVICE:getAddress::RES:  ",res)
      },
      error: (err) => {
        console.log("LOCATION_SERVICE:getAddress::ERROR:  ",err)

      }
    }) 
  
}

}
