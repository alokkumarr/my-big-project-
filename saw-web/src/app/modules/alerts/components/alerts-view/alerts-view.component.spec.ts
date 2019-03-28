import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertsViewComponent } from './alerts-view.component';

describe('AlertsViewComponent', () => {
  let component: AlertsViewComponent;
  let fixture: ComponentFixture<AlertsViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlertsViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
