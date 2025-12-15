import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { NavigationEnd, NavigationStart, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { NgxSonnerToaster } from 'ngx-sonner';
import {
  trigger,
  transition,
  style,
  animate,
  query,
  group,
  AnimationMetadata,
} from '@angular/animations';
import { LocationService } from './services/location/location-service';

// helper predicates
const isForward = (from: any, to: any) => {
  try {
    return (
      typeof from === 'string' &&
      typeof to === 'string' &&
      from.endsWith('-forward') &&
      to.endsWith('-forward')
    );
  } catch {
    return false;
  }
};
const isBack = (from: any, to: any) => {
  try {
    return (
      typeof from === 'string' &&
      typeof to === 'string' &&
      from.endsWith('-back') &&
      to.endsWith('-back')
    );
  } catch {
    return false;
  }
};

export const RouteAnimations = trigger('routeAnimations', [
  // FORWARD animation (use predicate)
  transition(
    (from, to) => isForward(from, to),
    [
      query(
        ':enter, :leave',
        style({ position: 'absolute', left: 0, right: 0, top: 0, width: '100%' }),
        { optional: true }
      ),
      query(':enter', style({ transform: 'translateX(30px)', opacity: 0 }), { optional: true }),
      group([
        query(
          ':leave',
          [animate('250ms ease', style({ transform: 'translateX(-10px)', opacity: 0 }))],
          { optional: true }
        ),
        query(
          ':enter',
          [animate('350ms ease', style({ transform: 'translateX(0)', opacity: 1 }))],
          { optional: true }
        ),
      ]),
    ]
  ),

  // BACK animation (use predicate)
  transition(
    (from, to) => isBack(from, to),
    [
      query(
        ':enter, :leave',
        style({ position: 'absolute', left: 0, right: 0, top: 0, width: '100%' }),
        { optional: true }
      ),
      query(':enter', style({ transform: 'translateX(-30px)', opacity: 0 }), { optional: true }),
      group([
        query(
          ':leave',
          [animate('250ms ease', style({ transform: 'translateX(10px)', opacity: 0 }))],
          { optional: true }
        ),
        query(
          ':enter',
          [animate('350ms ease', style({ transform: 'translateX(0)', opacity: 1 }))],
          { optional: true }
        ),
      ]),
    ]
  ),

  // fallback: any -> any (simple fade)
  transition('* => *', [
    query(
      ':enter, :leave',
      style({ position: 'absolute', left: 0, right: 0, top: 0, width: '100%' }),
      { optional: true }
    ),
    query(':enter', style({ opacity: 0 }), { optional: true }),
    group([
      query(':leave', [animate('200ms ease', style({ opacity: 0 }))], { optional: true }),
      query(':enter', [animate('300ms ease', style({ opacity: 1 }))], { optional: true }),
    ]),
  ]),
]);

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NgxSonnerToaster],
  templateUrl: './app.html',
  styleUrl: './app.css',
  animations: [RouteAnimations],
})
export class App implements OnInit {
  protected readonly title = signal('client');
  private navDirection: 'forward' | 'back' = 'forward';
  isBrowser: boolean;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private locationService: LocationService
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    // detect navigation trigger
    this.router.events
      .pipe(filter((e) => e instanceof NavigationStart))
      .subscribe((ev: NavigationStart) => {
        // navigationTrigger is 'popstate' when user used back/forward
        this.navDirection = (ev as any).navigationTrigger === 'popstate' ? 'back' : 'forward';
      });

    // ensure router ends: optional, not strictly needed for direction detection
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)).subscribe(() => {
      // can be used if you want to cleanup or log
    });
  }

  ngOnInit(): void {
    this.getCurrentLocation();
  }
  // prepareRoute returns a combined state: <routeAnimationToken>-<direction>
  prepareRoute(outlet: RouterOutlet) {
    const token = outlet?.activatedRouteData?.['animation'] ?? null;
    if (!token) return null;
    return `${token}-${this.navDirection}`; // e.g. "Home-forward" or "Login-back"
  }
  async ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId)) {
      const AOS = await import('aos');
      AOS.init({ duration: 900 });

      this.router.events
        .pipe(filter((event) => event instanceof NavigationEnd))
        .subscribe(() => setTimeout(() => AOS.refresh(), 50));
    }
  }

  getCurrentLocation() {
    this.locationService
      .getCurrentLocation()
      .then((location) => {
        console.log('Latitude:', location.latitude);
        console.log('Longitude:', location.longitude);
        console.log('Accuracy:', location.accuracy, 'meters');
        console.log(location);
        this.locationService.getAddress(location.latitude, location.longitude);
      })
      .catch((error) => {
        console.error('Error:', error);
      });
  }
}
