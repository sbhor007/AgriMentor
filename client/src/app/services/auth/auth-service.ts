import { Injectable } from '@angular/core';
import { ApiService } from '../../API/api-service';
import { Router } from '@angular/router';
import { toast } from 'ngx-sonner';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private api: ApiService,
    private router: Router // private farmerService:FarmerService
  ) {}

  isUserAlreadyExist(username: string, role: string): Observable<boolean> {
    return this.api.isUserAlreadyExistst(username, role).pipe(
      map((res: any) => {
        return res.data; // true or false
      })
    );
  }

  registerUser(userData: any) {
    console.log('Insede ResgisterUser...');

    this.api.registerUser(userData).subscribe({
      next: (res) => {
        console.log('AUTH::DATA: ' + res);
        toast.success('Registration successful');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.log('AUTH::ERROR: ' + err);
        toast.error('Registration failed');
      },
    });
  }

  //REgister consultant
  registerConsultant(consultantData: any) {
    console.log('Insede ResgisterConsultant...');

    this.api.registerConsultant(consultantData).subscribe({
      next: (res) => {
        console.log('AUTH:RegisterConsultant::DATA: ' + res);
        toast.success('Registration successful');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.log('AUTH:RegisterConsultant::ERROR: ' + err);
        console.log(err);

        toast.error('Registration failed');
      },
    });
  }

  login(userData: any) {
    this.api.login(userData).subscribe({
      next: (res) => {
        console.log('AUTH:LOGIN::DATA: ' + res);
        toast.success('Login successful');
        console.log(res);
        const tokens = res.data;
        sessionStorage.setItem('token', tokens.jwt);
        // // sessionStorage.setItem('refresh', tokens.refresh);
        sessionStorage.setItem('userId', tokens.id);
        sessionStorage.setItem('role', tokens.role);
        sessionStorage.setItem('email', userData.username);
        //switch route based on role
        if (tokens.role === 'CONSULTANT') {
          this.router.navigate(['consultant/dashboard']);
        } else if (tokens.role === 'FARMER') {
          // this.farmerService.getFarmerProfile();
          this.router.navigate(['farmer/dashboard']);
        } else {
          this.router.navigate(['admin/home']);
        }
      },
      error: (err) => {
        console.log('AUTH:LOGIN::ERROR: ');
        console.log(err);

        toast.error('Login failed: ' + (err.error?.message || err.message || 'Unknown error'));
      },
    });
  }
}
