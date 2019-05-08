import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialog, MatDialogModule } from '@angular/material';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../../material.module';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { Observable, of } from 'rxjs';
import { AlertDefinition } from '../../../alerts.interface';
import { AlertsConfigurationComponent } from './alerts-configuration.component';
import { ConfirmActionDialogComponent } from '../confirm-action-dialog/confirm-action-dialog.component';
import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

const ToastServiceStub: Partial<ToastService> = {
  success(msg, title = '', options = {}) {}
};
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

describe('AlertsConfigurationComponent', () => {
  let component: AlertsConfigurationComponent;
  let fixture: ComponentFixture<AlertsConfigurationComponent>;
  let dialogSpy: jasmine.Spy;
  const dialogRefSpyObj = jasmine.createSpyObj({
    afterClosed: of({}),
    close: null
  });
  dialogRefSpyObj.componentInstance = { body: '' };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MaterialModule,
        HttpClientTestingModule,
        DxTemplateModule,
        DxDataGridModule,
        MatDialogModule
      ],
      declarations: [AlertsConfigurationComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: ConfigureAlertService, useValue: confAlertServiceStub },
        { provide: ToastService, useValue: ToastServiceStub }
      ]
    }).compileComponents();

    dialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue(
      dialogRefSpyObj
    );
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
    const sidenavSpy = spyOn(component.sidenav, 'close');
    const resetSpy = spyOn(component, 'resetAlertDefInput');
    component.cancelAddalert();
    expect(sidenavSpy).toHaveBeenCalled();
    expect(resetSpy).toHaveBeenCalled();
  });

  it('should be called after an Alert is added', () => {
    const sidenavSpy = spyOn(component.sidenav, 'close');
    const resetSpy = spyOn(component, 'resetAlertDefInput');
    component.onAddAlert();
    expect(sidenavSpy).toHaveBeenCalled();
    expect(resetSpy).toHaveBeenCalled();
    expect(component.configuredAlerts$ instanceof Observable).toBe(true);
  });

  it('should be called on edit alert', () => {
    const sidenavSpy = spyOn(component.sidenav, 'open');
    component.editAlert(alertDefinitionStub.alertConfig);
    expect(component.navTitle).toBe('Edit Alert');
    expect(component.alertDefInput.action).toBe('update');
    expect(component.alertDefInput.alertConfig).toEqual(
      alertDefinitionStub.alertConfig
    );
    expect(sidenavSpy).toHaveBeenCalled();
  });

  it('should resetAlertDefInput', () => {
    component.resetAlertDefInput();
    expect(component.alertDefInput.action).toBe('create');
  });

  it('should delete Alert', () => {
    component.deleteAlert(alertDefinitionStub.alertConfig);
    component.dialog.open(ConfirmActionDialogComponent, { width: '100px' });
    expect(dialogSpy).toHaveBeenCalled();
    expect(dialogRefSpyObj.afterClosed).toHaveBeenCalled();
  });
});
