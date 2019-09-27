import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
// import { GridPagingOptions } from '../alerts.interface';
import { AlertsService } from './alerts.service';
import { Observable } from 'rxjs';

// const pagingOptions: GridPagingOptions = {
//   take: 10,
//   skip: 10
// };

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

  it('getRequest should return  observable', () => {
    expect(
      service.getRequest('alerts/states') instanceof Observable
    ).toBeTruthy();
  });

  //   it('should return observable for calling getAllAlertsCount', () => {
  //     expect(
  //       service.getAllAlertsCount({
  //         preset: 'TW',
  //         groupBy: 'StartTime'
  //       }) instanceof Observable
  //     ).toBeTruthy();
  //   });

  //   it('should return observable for calling getAllAlertsSeverity', () => {
  //     expect(
  //       service.getAllAlertsSeverity({
  //         preset: 'TW',
  //         groupBy: 'StartTime'
  //       }) instanceof Observable
  //     ).toBeTruthy();
  //   });

  //   it('should return observable for calling getAlertCountById', () => {
  //     expect(
  //       service.getAlertCountById(1, {
  //         preset: 'TW',
  //         groupBy: 'StartTime'
  //       }) instanceof Observable
  //     ).toBeTruthy();
  //   });
});
