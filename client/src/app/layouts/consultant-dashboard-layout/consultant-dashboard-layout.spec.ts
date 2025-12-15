import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantDashboardLayout } from './consultant-dashboard-layout';

describe('ConsultantDashboardLayout', () => {
  let component: ConsultantDashboardLayout;
  let fixture: ComponentFixture<ConsultantDashboardLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantDashboardLayout]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantDashboardLayout);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
