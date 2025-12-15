import { TestBed } from '@angular/core/testing';

import { ConsultationApi } from './consultation-api';

describe('ConsultationApi', () => {
  let service: ConsultationApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConsultationApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
