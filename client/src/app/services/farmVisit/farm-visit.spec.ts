import { TestBed } from '@angular/core/testing';

import { FarmVisit } from './farm-visit';

describe('FarmVisit', () => {
  let service: FarmVisit;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FarmVisit);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
