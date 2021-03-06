import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialog } from '@angular/material';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../../material.module';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { Observable, of } from 'rxjs';
import { AlertDefinition } from '../../../alerts.interface';
import { AlertsConfigurationComponent } from './alerts-configuration.component';
import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

const ToastServiceStub: Partial<ToastService> = {};
const confAlertServiceStub = {
  deleteAlert: (id: string) => {
    return new Observable();
  },
  getAllAlerts: async () => {
    return Promise.resolve({ alertRuleDetailsList: [], numberOfRecords: 11 });
  }
};
const alertDefinitionStub: AlertDefinition = {
  action: 'create',
  alertConfig: {
    alertRuleName: 'abc',
    alertRuleDescription: 'abc',
    alertSeverity: 'CRITICAL',
    activeInd: false,
    datapodId: '1',
    datapodName: 'abc',
    categoryId: '',
    notification: {},
    lookbackColumn: '',
    lookbackPeriod: '',
    product: 'SAWD000001',
    metricsColumn: '',
    aggregationType: '',
    operator: '',
    thresholdValue: '',
    otherThresholdValue: null,
    attributeName: '',
    attributeValue: '',
    sipQuery: { artifacts: [], filters: [] }
  }
};
export class MatDialogMock {
  open() {
    return {
      afterClosed: () => of({ action: true })
    };
  }
}

describe('AlertsConfigurationComponent', () => {
  let component: AlertsConfigurationComponent;
  let fixture: ComponentFixture<AlertsConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MaterialModule,
        HttpClientTestingModule,
        DxTemplateModule,
        DxDataGridModule
      ],
      declarations: [AlertsConfigurationComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: ConfigureAlertService, useValue: confAlertServiceStub },
        { provide: ToastService, useValue: ToastServiceStub },
        { provide: MatDialog, useClass: MatDialogMock }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertsConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should cancel add Alert action', () => {
    component.cancelAddalert();
  });

  it('should be called after an Alert is added', () => {
    component.onAddAlert();
  });

  it('should resetAlertDefInput', () => {
    component.resetAlertDefInput();
  });

  it('should delete Alert', () => {
    component.deleteAlert(alertDefinitionStub.alertConfig);
  });

  it('should set alertLoader data', () => {
    component.data = null;
    component.setAlertLoaderForGrid();
    expect(component.data).toBeTruthy();
  });

  it('should set proper navTitle', () => {
    component.addAlertClicked();
    expect(component.navTitle).toEqual('Add Alert');
    component.editAlert(alertDefinitionStub.alertConfig);
    expect(component.navTitle).toEqual('Edit Alert');
  });

  it('should set proper pagingEnabled', () => {
    component.enablePaging = false;
    component.setAlertLoaderForGrid();
    fixture.detectChanges();
    component.data.load().then(() => {
      return expect(component.enablePaging).toBeTruthy();
    });
  });
});
