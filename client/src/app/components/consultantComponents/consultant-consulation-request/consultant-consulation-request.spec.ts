import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantConsulationRequest } from './consultant-consulation-request';

describe('ConsultantConsulationRequest', () => {
  let component: ConsultantConsulationRequest;
  let fixture: ComponentFixture<ConsultantConsulationRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantConsulationRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantConsulationRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  
});
