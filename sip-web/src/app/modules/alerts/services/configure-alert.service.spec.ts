import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ConfigureAlertService } from './configure-alert.service';
import { AlertConfig } from '../alerts.interface';

import { Observable } from 'rxjs';

export const alertPayload: AlertConfig = {
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
  attributeName: '',
  attributeValue: '',
  sipQuery: { artifacts: [], filters: [] }
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

  it('getDatapod should return an Observable', () => {
    expect(
      configureAlertService.getDatapod$('1') instanceof Observable
    ).toBeTruthy();
  });

  it('createAlert should return an Observable', () => {
    expect(
      configureAlertService.createAlert(alertPayload) instanceof Observable
    ).toBeTruthy();
  });

  it('updateAlert should return an Observable', () => {
    expect(
      configureAlertService.updateAlert('1', alertPayload) instanceof Observable
    ).toBeTruthy();
  });

  it('deleteAlert should return an Observable', () => {
    expect(
      configureAlertService.deleteAlert('1') instanceof Observable
    ).toBeTruthy();
  });

  it('getAllAlerts should return an Observable', () => {
    expect(
      configureAlertService.getAllAlerts({}) instanceof Promise
    ).toBeTruthy();
  });

  it('getAggregations should return an Observable', () => {
    expect(
      configureAlertService.getAggregations() instanceof Observable
    ).toBeTruthy();
  });

  it('getOperators should return an Observable', () => {
    expect(
      configureAlertService.getOperators() instanceof Observable
    ).toBeTruthy();
  });
});
