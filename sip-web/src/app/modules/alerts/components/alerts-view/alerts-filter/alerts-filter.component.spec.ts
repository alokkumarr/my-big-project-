import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule } from '../../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NgxsModule } from '@ngxs/store';
import { AlertsFilterState } from '../state/alerts.state';

import { AlertsFilterComponent } from './alerts-filter.component';

describe('AlertsFilterComponent', () => {
  let component: AlertsFilterComponent;
  let fixture: ComponentFixture<AlertsFilterComponent>;
  // let store: Store;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlertsFilterComponent],
      imports: [
        MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        NoopAnimationsModule,
        NgxsModule.forRoot([AlertsFilterState])
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
    // store = TestBed.get(Store);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertsFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
