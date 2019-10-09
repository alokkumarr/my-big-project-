import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgxsModule } from '@ngxs/store';

import { MaterialModule } from '../../../../material.module';
import { AlertsState } from '../../state/alerts.state';
import { AlertsViewComponent } from './alerts-view.component';

describe('AlertsViewComponent', () => {
  let component: AlertsViewComponent;
  let fixture: ComponentFixture<AlertsViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MaterialModule,
        HttpClientTestingModule,
        NgxsModule.forRoot([AlertsState])
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [AlertsViewComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('setAlertLoaderForGrid should set alerts Loader', () => {
    const loaderSpy = spyOn(component, 'alertsDataLoader');
    component.setAlertLoaderForGrid();
    expect(loaderSpy instanceof Function).toBeTruthy();
  });

  it('fetchLateshAlerts should call setAlertLoaderForGrid', () => {
    const setAlertLoaderSpy = spyOn(component, 'setAlertLoaderForGrid');
    component.fetchLateshAlerts();
    expect(setAlertLoaderSpy).toHaveBeenCalled();
  });
});
