import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerRegister } from './farmer-register';

describe('FarmerRegister', () => {
  let component: FarmerRegister;
  let fixture: ComponentFixture<FarmerRegister>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerRegister]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FarmerRegister);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
