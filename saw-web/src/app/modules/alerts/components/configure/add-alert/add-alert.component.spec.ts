import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialModule } from '../../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddAlertComponent } from './add-alert.component';
import { AddAlertService } from '../../../services/add-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { Observable } from 'rxjs';

const ToastServiceStub: Partial<ToastService> = {};

const addAlertServiceStub = {
  createAlert: () => {
    return new Observable();
  },
  getListOfDatapods$: () => {
    return new Observable();
  },
  getMetricsInDatapod$: id => {
    return new Observable();
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
        { provide: AddAlertService, useValue: addAlertServiceStub },
        { provide: ToastService, useValue: ToastServiceStub }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddAlertComponent);
    component = fixture.componentInstance;
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
    component.alertMetricFormGroup.controls['monitoringEntity'].setValue('');
    component.alertMetricFormGroup.controls['datapodName'].setValue(
      selectedItem.metricName
    );

    component.onDatapodSelected(selectedItem);
  });

  it('should create alert', () => {
    component.createAlert();
  });
});
