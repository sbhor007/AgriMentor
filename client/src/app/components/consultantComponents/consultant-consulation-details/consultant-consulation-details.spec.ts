import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantConsulationDetails } from './consultant-consulation-details';

describe('ConsultantConsulationDetails', () => {
  let component: ConsultantConsulationDetails;
  let fixture: ComponentFixture<ConsultantConsulationDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantConsulationDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantConsulationDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
