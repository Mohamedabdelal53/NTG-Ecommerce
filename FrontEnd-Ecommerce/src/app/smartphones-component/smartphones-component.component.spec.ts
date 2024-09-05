import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmartphonesComponentComponent } from './smartphones-component.component';

describe('SmartphonesComponentComponent', () => {
  let component: SmartphonesComponentComponent;
  let fixture: ComponentFixture<SmartphonesComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SmartphonesComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmartphonesComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
