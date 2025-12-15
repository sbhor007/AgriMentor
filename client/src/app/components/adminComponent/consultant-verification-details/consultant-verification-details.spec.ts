import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantVerificationDetails } from './consultant-verification-details';

describe('ConsultantVerificationDetails', () => {
  let component: ConsultantVerificationDetails;
  let fixture: ComponentFixture<ConsultantVerificationDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantVerificationDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantVerificationDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
