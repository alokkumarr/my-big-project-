import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AddAlertService } from './add-alert.service';
import { AlertConfig } from '../alerts.interface';

import { Observable } from 'rxjs';

export const alertPayload: AlertConfig = {
  activeInd: 'string',
  aggregation: 'AVG',
  alertSeverity: 'CRITICAL',
  categoryId: 'string',
  datapodId: 'string',
  datapodName: 'string',
  monitoringEntity: 'string',
  operator: 'GT',
  product: 'string',
  ruleDescription: 'string',
  ruleName: 'string',
  thresholdValue: 0
};

describe('AddAlertService', () => {
  let addAlertService: AddAlertService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    addAlertService = TestBed.get(AddAlertService);
  });

  it('should be created', () => {
    expect(addAlertService).toBeTruthy();
  });

  it('getListOfDatapods should return an Observable', () => {
    expect(
      addAlertService.getListOfDatapods$() instanceof Observable
    ).toBeTruthy();
  });

  it('getMetricsInDatapod should return an Observable', () => {
    expect(
      addAlertService.getMetricsInDatapod$('1') instanceof Observable
    ).toBeTruthy();
  });

  it('getMetricsInDatapod should return an Observable', () => {
    expect(
      addAlertService.createAlert(alertPayload) instanceof Observable
    ).toBeTruthy();
  });
});
