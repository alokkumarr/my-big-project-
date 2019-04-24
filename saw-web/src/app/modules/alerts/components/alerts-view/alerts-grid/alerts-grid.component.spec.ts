import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertsGridComponent } from './alerts-grid.component';

describe('AlertsGridComponent', () => {
  let component: AlertsGridComponent;
  let fixture: ComponentFixture<AlertsGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlertsGridComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertsGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
