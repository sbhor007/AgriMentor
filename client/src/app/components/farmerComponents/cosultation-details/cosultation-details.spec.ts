import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CosultationDetails } from './cosultation-details';

describe('CosultationDetails', () => {
  let component: CosultationDetails;
  let fixture: ComponentFixture<CosultationDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CosultationDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CosultationDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
