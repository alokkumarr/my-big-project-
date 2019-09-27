import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { MatIconRegistry } from '@angular/material';
import { MaterialModule } from '../../../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NgxsModule } from '@ngxs/store';
import { AlertsState } from '../../../../state/alerts.state';

import { AlertsDateFilterComponent } from './alerts-filter.component';

describe('AlertsDateFilterComponent', () => {
  let component: AlertsDateFilterComponent;
  let fixture: ComponentFixture<AlertsDateFilterComponent>;
  // let store: Store;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlertsDateFilterComponent],
      imports: [
        MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        NoopAnimationsModule,
        NgxsModule.forRoot([AlertsState])
      ],
      providers: [MatIconRegistry, HttpClient, HttpHandler],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
    // store = TestBed.get(Store);
    fixture = TestBed.createComponent(AlertsDateFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
