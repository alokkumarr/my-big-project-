import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AlertsService } from './alerts.service';
import { Observable } from 'rxjs';
import { defaultAlertFilters } from '../state/alerts.state';

describe('AlertsService', () => {
  let service: AlertsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AlertsService]
    });
    service = TestBed.get(AlertsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAlertRuleDetails should return  observable', () => {
    expect(service.getAlertRuleDetails(1) instanceof Observable).toBeTruthy();
  });

  it('convertOptionsToPayloadAndQueryParams should return null for sorts if no sorting information is given', () => {
    const options = {};
    const { sorts } = service.convertOptionsToPayloadAndQueryParams(options);
    expect(sorts).toEqual(null);
  });

  it('convertOptionsToPayloadAndQueryParams should convert asorts object to backend sorts structure', () => {
    const options = { sort: { selector: 'someField', desc: true } };
    const targetSorts = [{ fieldName: 'someField', order: 'DESC' }];
    const { sorts } = service.convertOptionsToPayloadAndQueryParams(options);
    expect(sorts).toEqual(targetSorts);
  });

  it('convertOptionsToPayloadAndQueryParams should create queryParams properly', () => {
    const options = { skip: 0, take: 10 };
    const targetQueryParams = `?pageNumber=1&pageSize=10`;
    const { queryParams } = service.convertOptionsToPayloadAndQueryParams(
      options
    );
    expect(queryParams).toEqual(targetQueryParams);
  });

  it('getRequest should return  observable', () => {
    expect(
      service.getRequest('alerts/states') instanceof Observable
    ).toBeTruthy();
  });

  it('should return observable for calling getAllAlertsCount', () => {
    expect(
      service.getAllAlertsCount(defaultAlertFilters) instanceof Observable
    ).toBeTruthy();
  });

  it('should return observable for calling getAllAlertsSeverity', () => {
    expect(
      service.getAllAlertsSeverity(defaultAlertFilters) instanceof Observable
    ).toBeTruthy();
  });

  it('should return observable for calling getAlertCountById', () => {
    expect(
      service.getAlertCountById(1, defaultAlertFilters) instanceof Observable
    ).toBeTruthy();
  });
});
