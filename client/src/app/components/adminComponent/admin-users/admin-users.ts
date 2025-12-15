import { Component, OnInit, Inject, PLATFORM_ID, ChangeDetectorRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/adminService/admin-service';
import { AdminUser, UserStatistics } from '../../../interfaces/admin.interfaces';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-admin-users',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers implements OnInit {
  // Statistics
  userStats: UserStatistics | null = null;

  // Users list
  users: AdminUser[] = [];
  filteredUsers: AdminUser[] = [];

  // UI state
  searchTerm = '';
  selectedFilter = 'all';
  isLoading = true;

  constructor(
    private adminService: AdminService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadUserStatistics();
      this.loadAllUsers();

      console.log('user-State::');
      console.log(this.userStats);
      console.log(this.users);
    }
  }

  loadUserStatistics(): void {
    console.log('AdminUsers: Loading user statistics...');
    this.adminService.getUserStatistics().subscribe({
      next: (response) => {
        console.log('AdminUsers: getUserStatistics response:', response);
        this.userStats = response.data;
        console.log('AdminUsers: userStats set to:', this.userStats);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('AdminUsers: Error loading user statistics:', error);
        toast.error('Failed to load user statistics');
      },
    });
  }

  loadAllUsers(): void {
    this.isLoading = true;
    this.adminService.getAllUsers().subscribe({
      next: (response) => {
        console.log('AdminUsers: getAllUsers response:', response);
        this.users = response.data || [];
        console.log('AdminUsers: users set to:', this.users);
        this.filterUsers();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('AdminUsers: Error loading users:', error);
        toast.error('Failed to load users');
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  filterUsers(): void {
    if (!this.searchTerm) {
      this.filteredUsers = this.users;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredUsers = this.users.filter(
        (user) =>
          user.firstName.toLowerCase().includes(term) ||
          user.lastName.toLowerCase().includes(term) ||
          user.email.toLowerCase().includes(term) ||
          user.role.toLowerCase().includes(term)
      );
    }
  }

  toggleUserStatus(user: AdminUser): void {
    this.adminService.toggleUserStatus(user.email).subscribe({
      next: (response) => {
        toast.success(response.message || 'User status updated');
        this.loadAllUsers(); // Reload users
      },
      error: (error) => {
        console.error('Error toggling user status:', error);
        toast.error('Failed to update user status');
      },
    });
  }

  getInitials(firstName: string, lastName: string): string {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  }

  getRoleColor(role: string): string {
    return role === 'FARMER' ? 'emerald' : 'indigo';
  }

  getStatusClass(isActive: boolean): string {
    return isActive
      ? 'bg-green-100 text-green-700 border-green-200'
      : 'bg-red-100 text-red-700 border-red-200';
  }

  getStatusText(isActive: boolean): string {
    return isActive ? 'Active' : 'Inactive';
  }
}
