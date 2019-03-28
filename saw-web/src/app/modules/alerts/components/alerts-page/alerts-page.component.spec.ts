import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { AlertsPageComponent } from './alerts-page.component';

describe('AlertsPageComponent', () => {
  let component: AlertsPageComponent;
  let fixture: ComponentFixture<AlertsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlertsPageComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
