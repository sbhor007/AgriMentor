import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportyReportDetail } from './reporty-report-detail';

describe('ReportyReportDetail', () => {
  let component: ReportyReportDetail;
  let fixture: ComponentFixture<ReportyReportDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportyReportDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportyReportDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
