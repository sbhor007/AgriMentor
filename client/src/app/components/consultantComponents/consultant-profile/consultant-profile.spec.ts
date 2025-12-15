import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantProfile } from './consultant-profile';

describe('ConsultantProfile', () => {
  let component: ConsultantProfile;
  let fixture: ComponentFixture<ConsultantProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
