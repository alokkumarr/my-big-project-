import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialModule } from '../../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddAlertComponent } from './add-alert.component';
import { AlertDefinition, AlertConfig } from '../../../alerts.interface';
import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { Observable } from 'rxjs';

const ToastServiceStub: Partial<ToastService> = {};

const confAlertServiceStub = {
  createAlert: () => {
    return new Observable();
  },
  getOperators: () => {
    return new Observable();
  },
  getAggregations: () => {
    return new Observable();
  },
  updateAlert: (id: string, alertConfig: AlertConfig) => {
    return new Observable();
  },
  getListOfDatapods$: () => {
    return new Observable();
  },
  getMetricsInDatapod$: id => {
    return new Observable();
  }
};

const alertDefinitionStub: AlertDefinition = {
  action: 'create',
  alertConfig: {
    alertRulesSysId: '1',
    datapodId: '1',
    datapodName: 'abc',
    alertName: 'abc',
    alertDescription: 'abc',
    category: '1',
    alertSeverity: 'CRITICAL',
    monitoringEntity: 'abc123',
    aggregation: 'AVG',
    operator: 'GT',
    thresholdValue: 2,
    activeInd: false,
    createdBy: 'admin',
    createdTime: 1555524407000,
    modifiedTime: null,
    modifiedBy: null
  }
};

describe('AddAlertComponent', () => {
  let component: AddAlertComponent;
  let fixture: ComponentFixture<AddAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddAlertComponent],
      imports: [
        MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        NoopAnimationsModule,
        HttpClientTestingModule
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: ConfigureAlertService, useValue: confAlertServiceStub },
        { provide: ToastService, useValue: ToastServiceStub }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddAlertComponent);
    component = fixture.componentInstance;
    component.alertDefinition = alertDefinitionStub;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set datapodName and fetch metrics list in a datapod', () => {
    const selectedItem = {
      id: '1',
      metricName: 'sample'
    };
    // component.alertMetricFormGroup.controls['monitoringEntity'].setValue('');
    component.alertMetricFormGroup.controls['datapodName'].setValue(
      selectedItem.metricName
    );

    component.onDatapodSelected(selectedItem);
  });

  it('should create alert payload', () => {
    component.constructPayload();
  });

  it('should create alert', () => {
    component.createAlert();
  });

  it('should update alert', () => {
    component.updateAlert();
  });
});
