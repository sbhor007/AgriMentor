import { TestBed } from '@angular/core/testing';

import { FarmVisitingApi } from './farm-visiting-api';

describe('FarmVisitingApi', () => {
  let service: FarmVisitingApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FarmVisitingApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
