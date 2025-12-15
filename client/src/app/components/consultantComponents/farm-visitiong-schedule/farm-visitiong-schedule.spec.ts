import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmVisitiongSchedule } from './farm-visitiong-schedule';

describe('FarmVisitiongSchedule', () => {
  let component: FarmVisitiongSchedule;
  let fixture: ComponentFixture<FarmVisitiongSchedule>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmVisitiongSchedule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FarmVisitiongSchedule);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
