import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Observable } from 'rxjs';
import { MaterialModule } from '../../../../../material.module';
import { CommonPipesModule } from '../../../../../common/pipes/common-pipes.module';
import { AddAlertComponent } from './add-alert.component';
import { AlertDefinition, AlertConfig } from '../../../alerts.interface';
import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { ObserveService } from '../../../../observe/services/observe.service';

const ToastServiceStub: Partial<ToastService> = {
  success(msg, title = '', options = {}) {}
};

const ObserveServiceStub = {};

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
  getDatapod$: id => {
    return new Observable();
  }
};

const payload: AlertConfig = {
  alertRuleName: '',
  alertRuleDescription: '',
  alertSeverity: '',
  activeInd: true,
  datapodId: '',
  datapodName: '',
  categoryId: '',
  notification: {},
  lookbackColumn: '',
  lookbackPeriod: '-',
  product: 'SAWD000001',
  metricsColumn: '',
  aggregationType: '',
  operator: '',
  thresholdValue: '',
  otherThresholdValue: null,
  attributeName: '',
  attributeValue: '',
  createdBy: undefined,
  createdTime: undefined
};

const alertDefinitionStub: AlertDefinition = {
  action: 'update',
  alertConfig: { ...payload }
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
        HttpClientTestingModule,
        CommonPipesModule
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: ConfigureAlertService, useValue: confAlertServiceStub },
        { provide: ToastService, useValue: ToastServiceStub },
        { provide: ObserveService, useValue: ObserveServiceStub }
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
    component.alertMetricFormGroup.controls['datapodName'].setValue(
      selectedItem.metricName
    );
    component.onDatapodSelected(selectedItem);
    expect(component.metricsList$ instanceof Observable).toBe(true);
  });

  it('should reset metricsColumn formControl value', () => {
    component.onDatapodChanged();
    expect(
      component.alertMetricFormGroup.controls['metricsColumn'].value
    ).toEqual('');
  });

  it('should create alert payload', () => {
    component.selectedDatapod = {
      artifacts: [{ artifactName: 'sample', fields: [] }],
      esRepository: {
        indexName: '',
        type: '',
        storageType: ''
      }
    };
    component.selectedMetricsColumn = {
      columnName: '',
      alias: '',
      displayName: '',
      type: ''
    };
    component.constructPayload();
    expect(component.endPayload).toEqual(payload);
  });

  it('should create alert', () => {
    const payloadSpy = spyOn(component, 'constructPayload');
    component.createAlert();
    expect(payloadSpy).toHaveBeenCalled();
    expect(component.subscriptions).not.toBeNull();
  });

  it('should update alert', () => {
    const payloadSpy = spyOn(component, 'constructPayload');
    component.updateAlert();
    expect(payloadSpy).toHaveBeenCalled();
    expect(component.subscriptions).not.toBeNull();
  });

  it('should notifyOnAction', () => {
    const data = {
      alert: {},
      message: 'Alert rule updated successfully'
    };
    const addAlertSpy = spyOn(component.onAddAlert, 'emit');
    const notifySpy = spyOn(component._notify, 'success');

    component.notifyOnAction(data);
    expect(notifySpy).toHaveBeenCalled();
    expect(addAlertSpy).toHaveBeenCalled();
  });
});
