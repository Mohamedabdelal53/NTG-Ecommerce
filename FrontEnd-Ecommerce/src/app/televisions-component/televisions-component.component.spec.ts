import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelevisionsComponentComponent } from './televisions-component.component';

describe('TelevisionsComponentComponent', () => {
  let component: TelevisionsComponentComponent;
  let fixture: ComponentFixture<TelevisionsComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TelevisionsComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TelevisionsComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
