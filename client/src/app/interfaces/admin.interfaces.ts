// Admin-related TypeScript interfaces matching backend DTOs

export interface UserGrowthStats {
  farmersThisMonth: number;
  consultantsThisMonth: number;
  farmerGrowthPercentage: number;
  consultantGrowthPercentage: number;
}

export interface AdminDashboardStats {
  totalUsers: number;
  totalFarmers: number;
  totalConsultants: number;
  activeConsultations: number;
  approvedConsultations: number;
  inProgressConsultations: number;
  pendingRequests: number;
  platformActivityRate: number;
  userGrowth: UserGrowthStats;
}

export interface UserStatistics {
  totalUsers: number;
  totalFarmers: number;
  totalConsultants: number;
  totalAdmins: number;
  activeUsers: number;
  inactiveUsers: number;
  verifiedUsers: number;
  unverifiedUsers: number;
}

export interface ConsultationOverview {
  totalConsultations: number;
  activeConsultations: number;
  completedConsultations: number;
  pendingConsultations: number;
  rejectedConsultations: number;
  approvedConsultations: number;
}

export interface Activity {
  type: string;
  description: string;
  username: string;
  userRole: string;
  timestamp: string;
}

export interface AdminUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  role: string;
  isActive: boolean;
  isVerified: boolean;
  createdAt: string;
  updatedAt: string;
  address?: {
    street: string;
    city: string;
    state: string;
    pinCode: string;
    country: string;
  };
}
