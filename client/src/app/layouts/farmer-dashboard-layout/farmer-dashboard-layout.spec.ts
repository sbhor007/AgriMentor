import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerDashboardLayout } from './farmer-dashboard-layout';

describe('FarmerDashboardLayout', () => {
  let component: FarmerDashboardLayout;
  let fixture: ComponentFixture<FarmerDashboardLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerDashboardLayout]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FarmerDashboardLayout);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
