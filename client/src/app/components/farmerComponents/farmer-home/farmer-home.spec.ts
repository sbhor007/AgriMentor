import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FarmerHome } from './farmer-home';

describe('FarmerHome', () => {
  let component: FarmerHome;
  let fixture: ComponentFixture<FarmerHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FarmerHome);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
