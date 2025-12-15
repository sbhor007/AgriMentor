import { TestBed } from '@angular/core/testing';

import { ConsultationReportApi } from './consultation-report-api';

describe('ConsultationReportApi', () => {
  let service: ConsultationReportApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConsultationReportApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
