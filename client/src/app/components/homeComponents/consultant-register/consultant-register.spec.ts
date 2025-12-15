import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantRegister } from './consultant-register';

describe('ConsultantRegister', () => {
  let component: ConsultantRegister;
  let fixture: ComponentFixture<ConsultantRegister>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantRegister]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantRegister);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
