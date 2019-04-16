import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ConfigureAlertService } from './configure-alert.service';
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
  alertDescriptions: 'string',
  alertName: 'string',
  thresholdValue: 0
};

describe('ConfigureAlertService', () => {
  let configureAlertService: ConfigureAlertService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    configureAlertService = TestBed.get(ConfigureAlertService);
  });

  it('should be created', () => {
    expect(configureAlertService).toBeTruthy();
  });

  it('getListOfDatapods should return an Observable', () => {
    expect(
      configureAlertService.getListOfDatapods$() instanceof Observable
    ).toBeTruthy();
  });

  it('getMetricsInDatapod should return an Observable', () => {
    expect(
      configureAlertService.getMetricsInDatapod$('1') instanceof Observable
    ).toBeTruthy();
  });

  it('getMetricsInDatapod should return an Observable', () => {
    expect(
      configureAlertService.createAlert(alertPayload) instanceof Observable
    ).toBeTruthy();
  });

  it('getAllAlerts should return an Observable', () => {
    expect(
      configureAlertService.getAllAlerts() instanceof Observable
    ).toBeTruthy();
  });
});
