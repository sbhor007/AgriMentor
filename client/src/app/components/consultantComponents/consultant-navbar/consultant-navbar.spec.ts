import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantNavbar } from './consultant-navbar';

describe('ConsultantNavbar', () => {
  let component: ConsultantNavbar;
  let fixture: ComponentFixture<ConsultantNavbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantNavbar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantNavbar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
