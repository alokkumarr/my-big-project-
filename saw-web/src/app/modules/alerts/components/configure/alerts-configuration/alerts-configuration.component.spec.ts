import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgModule } from '@angular/core';

import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../../material.module';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { Observable } from 'rxjs';
import { AlertDefinition } from '../../../alerts.interface';
import { AlertsConfigurationComponent } from './alerts-configuration.component';
import { ConfirmActionDialogComponent } from '../confirm-action-dialog/confirm-action-dialog.component';
import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

const ToastServiceStub: Partial<ToastService> = {};
const confAlertServiceStub = {
  deleteAlert: (id: string) => {
    return new Observable();
  },
  getAllAlerts: () => {
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

@NgModule({
  declarations: [ConfirmActionDialogComponent],
  entryComponents: [ConfirmActionDialogComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ConfirmActionModule {}

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
        DxDataGridModule,
        ConfirmActionModule
      ],
      declarations: [AlertsConfigurationComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: ConfigureAlertService, useValue: confAlertServiceStub },
        { provide: ToastService, useValue: ToastServiceStub }
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

  it('should be called on edit alert', () => {
    component.editAlert(alertDefinitionStub.alertConfig);
  });

  it('should resetAlertDefInput', () => {
    component.resetAlertDefInput();
  });

  it('should delete Alert', () => {
    component.deleteAlert(alertDefinitionStub.alertConfig);
  });
});
