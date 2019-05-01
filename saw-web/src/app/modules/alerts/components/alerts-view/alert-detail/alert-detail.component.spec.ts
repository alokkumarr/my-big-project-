import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Observable } from 'rxjs';
import { AlertsService } from '../../../services/alerts.service';
import { AlertDetailComponent } from './alert-detail.component';
import { AlertIds } from '../../../alerts.interface';

const alertIds: AlertIds = {
  alertRulesSysId: 3,
  alertTriggerSysId: 3
};

const alertServiceStub = {
  getAlertRuleDetails: (id: number) => {
    return new Observable();
  }
};

describe('AlertDetailComponent', () => {
  let component: AlertDetailComponent;
  let fixture: ComponentFixture<AlertDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [AlertDetailComponent],
      providers: [{ provide: AlertsService, useValue: alertServiceStub }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertDetailComponent);
    component = fixture.componentInstance;
    component.alertIds = alertIds;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getalertRuleDetails', () => {
    component.getalertRuleDetails(1);
  });
});
