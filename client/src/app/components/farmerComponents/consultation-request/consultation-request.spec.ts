import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultationRequest } from './consultation-request';

describe('ConsultationRequest', () => {
  let component: ConsultationRequest;
  let fixture: ComponentFixture<ConsultationRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultationRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultationRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
