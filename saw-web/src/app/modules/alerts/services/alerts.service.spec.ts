import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { GridPagingOptions } from '../alerts.interface';
import { AlertsService } from './alerts.service';

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

  it('should getAlertsStatesForGrid', () => {
    service.getAlertsStatesForGrid(pagingOptions);
  });
});
