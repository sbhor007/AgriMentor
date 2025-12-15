import { TestBed } from '@angular/core/testing';

import { ConsultationReportService } from './consultation-report-service';

describe('ConsultationReportService', () => {
  let service: ConsultationReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConsultationReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
