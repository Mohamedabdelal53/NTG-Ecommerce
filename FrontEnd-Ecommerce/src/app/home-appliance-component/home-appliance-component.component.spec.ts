import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeAppliancesComponent } from './home-appliance-component.component';

describe('HomeApplianceComponentComponent', () => {
  let component: HomeAppliancesComponent;
  let fixture: ComponentFixture<HomeAppliancesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeAppliancesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeAppliancesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
