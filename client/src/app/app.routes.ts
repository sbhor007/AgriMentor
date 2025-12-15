import { Routes } from '@angular/router';
import { ConsultantVerificationDetails } from './components/adminComponent/consultant-verification-details/consultant-verification-details';
import { Home } from './components/homeComponents/home/home';
import { HomeLayout } from './layouts/home-layout/home-layout';
import { Login } from './components/homeComponents/login/login';
import { FarmerRegister } from './components/homeComponents/farmer-register/farmer-register';
import { ConsultantRegister } from './components/homeComponents/consultant-register/consultant-register';
import { animation } from '@angular/animations';
import { ForgotPassword } from './components/homeComponents/forgot-password/forgot-password';
import { FarmerDashboardLayout } from './layouts/farmer-dashboard-layout/farmer-dashboard-layout';
import { FarmerHome } from './components/farmerComponents/farmer-home/farmer-home';
import { Consultation } from './components/farmerComponents/consultation/consultation';
import { ConsultationRequest } from './components/farmerComponents/consultation-request/consultation-request';
import { FarmerProfile } from './components/farmerComponents/farmer-profile/farmer-profile';
import { CosultationDetails } from './components/farmerComponents/cosultation-details/cosultation-details';
import { Consultant } from './components/farmerComponents/consultant/consultant';
import { ConsultantProfile as FarmerConsultantProfile } from './components/farmerComponents/consultant-profile/consultant-profile';
import { ConsultantDashboardLayout } from './layouts/consultant-dashboard-layout/consultant-dashboard-layout';
import { ConsultantHome } from './components/consultantComponents/consultant-home/consultant-home';
import { ConsultantProfile } from './components/consultantComponents/consultant-profile/consultant-profile';
import { ConsultantConsulationDetails } from './components/consultantComponents/consultant-consulation-details/consultant-consulation-details';
import { ConsultantConsulationRequest } from './components/consultantComponents/consultant-consulation-request/consultant-consulation-request';
import { FarmVisitiongSchedule } from './components/consultantComponents/farm-visitiong-schedule/farm-visitiong-schedule';
import { FarmVisitDashboard } from './components/consultantComponents/farm-visit-dashboard/farm-visit-dashboard';
import { AdminDashboardLayout } from './layouts/admin-dashboard-layout/admin-dashboard-layout';
import { AdminHome } from './components/adminComponent/admin-home/admin-home';
import { AdminUsers } from './components/adminComponent/admin-users/admin-users';
import { AdminConsultations } from './components/adminComponent/admin-consultations/admin-consultations';
import { AdminApprovals } from './components/adminComponent/admin-approvals/admin-approvals';
import { AdminReports } from './components/adminComponent/admin-reports/admin-reports';
import { AdminSettings } from './components/adminComponent/admin-settings/admin-settings';
import { ReportyReportDetail } from './components/reporty-report-detail/reporty-report-detail';
import { GlobalFeedbackComponent } from './components/shared/global-feedback/global-feedback';
import { ChatComponent } from './components/chat/chat';

export const routes: Routes = [
  {
    path: '',
    component: HomeLayout,
    children: [
      {
        path: '',
        component: Home,
        data: { animation: 'Home' },
      },
      {
        path: 'login',
        component: Login,
        data: { animation: 'Login' },
      },
      {
        path: 'register/farmer',
        component: FarmerRegister,
        data: {
          animation: 'FarmerRegister',
        },
      },
      {
        path: 'register/consultant',
        component: ConsultantRegister,
        data: {
          animation: 'ConsultantRegister',
        },
      },
      {
        path: 'forgot/password',
        component: ForgotPassword,
        data: { animation: 'ForgotPassword' },
      },
    ],
  },
  {
    path: 'farmer',
    component: FarmerDashboardLayout,
    children: [
      {
        path: 'dashboard',
        component: FarmerHome,
        data: { animation: 'Home' },
      },
      {
        path: 'consultation',
        component: Consultation,
        data: { animation: 'Consultation' },
      },
      {
        path: 'consultants',
        component: Consultant,
        data: { animation: 'Consultants' },
      },
      {
        path: 'consultation-request',
        component: ConsultationRequest,
        data: { animation: 'ConsultantRegister' },
      },
      {
        path: 'profile',
        component: FarmerProfile,
        data: { animation: 'FarmerProfile' },
      },
      {
        path: 'consultation-request/:id',
        component: CosultationDetails,
        data: { animation: 'CosultationDetails' },
      },
      {
        path: 'consultation/:id',
        component: ConsultantConsulationDetails,
        data: { animation: 'Cosultation' },
      },
      {
        path: 'consultant/profile/:id',
        component: FarmerConsultantProfile,
        data: { animation: 'ConsultantProfile' },
      },
      {
        path: 'report/:reportId',
        component: ReportyReportDetail,
        data: { animation: 'ReportDetail' },
      },
      {
        path: 'feedbacks',
        component: GlobalFeedbackComponent,
        data: { animation: 'Feedbacks' },
      },
      {
        path: 'chat',
        component: ChatComponent,
        data: { animation: 'Chat' },
      },
    ],
  },
  {
    path: 'consultant',
    component: ConsultantDashboardLayout,
    children: [
      {
        path: 'dashboard',
        component: ConsultantHome,
        data: { animation: 'Home' },
      },
      {
        path: 'consultation-request',
        component: ConsultantConsulationRequest,
        data: { animation: 'ConsultantConsulationRequest' },
      },
      {
        path: 'profile',
        component: ConsultantProfile,
        data: { animation: 'ConsultantProfile' },
      },
      {
        path: 'consultation-details/:id',
        component: ConsultantConsulationDetails,
        data: { animation: 'ConsultantConsulationDetails' },
      },
      {
        path: 'farm-schedule/:id',
        component: FarmVisitiongSchedule,
        data: { animation: 'FarmVisitiongSchedule' },
      },
      {
        path: 'farm-visits',
        component: FarmVisitDashboard,
        data: { animation: 'FarmVisitDashboard' },
      },
      {
        path: 'report/:reportId',
        component: ReportyReportDetail,
        data: { animation: 'ReportDetail' },
      },
      {
        path: 'feedbacks',
        component: GlobalFeedbackComponent,
        data: { animation: 'Feedbacks' },
      },
      {
        path: 'chat',
        component: ChatComponent,
        data: { animation: 'Chat' },
      },
    ],
  },
  {
    path: 'admin',
    component: AdminDashboardLayout,
    children: [
      {
        path: 'home',
        component: AdminHome,
        data: { animation: 'AdminHome' },
      },
      {
        path: 'users',
        component: AdminUsers,
        data: { animation: 'AdminUsers' },
      },
      {
        path: 'consultations',
        component: AdminConsultations,
        data: { animation: 'AdminConsultations' },
      },
      {
        path: 'approvals',
        component: AdminApprovals,
        data: { animation: 'AdminApprovals' },
      },
      {
        path: 'verification/:id',
        component: ConsultantVerificationDetails,
        data: { animation: 'ConsultantVerificationDetails' },
      },
      {
        path: 'reports',
        component: AdminReports,
        data: { animation: 'AdminReports' },
      },
      {
        path: 'feedbacks',
        component: GlobalFeedbackComponent,
        data: { animation: 'Feedbacks' },
      },
      {
        path: 'settings',
        component: AdminSettings,
        data: { animation: 'AdminSettings' },
      },
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full',
      },
    ],
  },
];
