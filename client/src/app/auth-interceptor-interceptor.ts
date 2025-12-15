import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Platform } from '@angular/cdk/platform';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';


export const authInterceptorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const platformService = inject(Platform);

  // Skip interceptor for login/register requests
  if (req.url.includes('/auth/login') || req.url.includes('/auth/register')) {
    return next(req);
  }

  // Get the auth token from session storage safely
  const token = typeof window !== 'undefined' ? window.sessionStorage.getItem('token') : null;

  // If we have a token, add it to the request headers
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token might be expired or invalid, clear it and redirect to login
          if (typeof window !== 'undefined') {
            window.sessionStorage.removeItem('token');
          }
          router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }

  return next(req);
};
