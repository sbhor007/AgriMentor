import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDashboardLayout } from './admin-dashboard-layout';

describe('AdminDashboardLayout', () => {
  let component: AdminDashboardLayout;
  let fixture: ComponentFixture<AdminDashboardLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDashboardLayout]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDashboardLayout);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
