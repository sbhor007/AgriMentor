import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class WeatherService {
  private weatherApiUrl = environment.WEATHER_BASE_URL;
  private apiKey = environment.WEATHER_API;

  constructor(private http: HttpClient) {}

  getWeatherDataByCity(city: string) {
    return this.http.get(`${this.weatherApiUrl}current.json?key=${this.apiKey}&q=${city}`);
  }

  getWeatherDataByLatLon(lat: any, lon: any) {
    return this.http.get(`${this.weatherApiUrl}current.json?key=${this.apiKey}&q=${lat}&q=${lon}`);
  }

  getPastAndFutureWeather(lat: any, lon: any) {
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&past_days=5&forecast_days=5&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode&hourly=temperature_2m,weathercode&timezone=auto`;
    return this.http.get(url);
  }
}
