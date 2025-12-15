import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultantHome } from './consultant-home';

describe('ConsultantHome', () => {
  let component: ConsultantHome;
  let fixture: ComponentFixture<ConsultantHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultantHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultantHome);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
