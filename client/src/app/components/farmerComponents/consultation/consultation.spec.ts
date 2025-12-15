import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Consultation } from './consultation';

describe('Consultation', () => {
  let component: Consultation;
  let fixture: ComponentFixture<Consultation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Consultation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Consultation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
