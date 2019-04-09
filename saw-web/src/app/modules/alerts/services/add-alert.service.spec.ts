import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AddAlertService } from './add-alert.service';

describe('AddAlertService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    })
  );

  it('should be created', () => {
    const service: AddAlertService = TestBed.get(AddAlertService);
    expect(service).toBeTruthy();
  });
});
