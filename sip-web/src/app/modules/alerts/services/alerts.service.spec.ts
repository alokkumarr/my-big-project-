import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { GridPagingOptions } from '../alerts.interface';
import { AlertsService } from './alerts.service';
import { Observable } from 'rxjs';

const pagingOptions: GridPagingOptions = {
  take: 10,
  skip: 10
};

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

  it('getAlertsStatesForGrid should return Promise', () => {
    expect(
      service.getAlertsStatesForGrid(pagingOptions) instanceof Promise
    ).toBeTruthy();
  });

  it('getAlertRuleDetails should return  observable', () => {
    expect(service.getAlertRuleDetails(1) instanceof Observable).toBeTruthy();
  });

  it('getRequest should return  observable', () => {
    expect(
      service.getRequest('alerts/states') instanceof Observable
    ).toBeTruthy();
  });
});
