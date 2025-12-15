import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  benefits = [
    {
      title: 'Expert Guidance',
      description:
        'Connect with certified agricultural consultants who understand local farming conditions and best practices.',
      icon: 'svg',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-700',
    },
    {
      title: 'Weather Updates',
      description:
        'Get real-time weather forecasts and advisories to plan your farming activities effectively.',
      icon: 'svg',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-700',
    },
    {
      title: 'Real-time Chat',
      description:
        'Instant messaging with consultants for quick questions and ongoing support throughout the season.',
      icon: 'svg',
      iconBg: 'bg-yellow-100',
      iconColor: 'text-yellow-700',
    },
    {
      title: 'Detailed Reports',
      description:
        'Receive comprehensive consultation reports with crop recommendations and fertilizer schedules.',
      icon: 'svg',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-700',
    },
    {
      title: 'Soil Analysis',
      description:
        'Upload soil test results and get personalized recommendations for soil preparation and management.',
      icon: 'svg',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-700',
    },
    {
      title: 'Track Progress',
      description:
        'Monitor your consultation history and track improvements in your farming practices over time.',
      icon: 'svg',
      iconBg: 'bg-rose-100',
      iconColor: 'text-rose-700',
    },
  ];
}
