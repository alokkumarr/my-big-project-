import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AlertsService } from './alerts.service';

describe('AlertsService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] })
  );

  it('should be created', () => {
    const service: AlertsService = TestBed.get(AlertsService);
    expect(service).toBeTruthy();
  });
});
