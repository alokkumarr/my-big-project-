import { TestBed } from '@angular/core/testing';

import { AddAlertService } from './add-alert.service';

describe('AddAlertService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AddAlertService = TestBed.get(AddAlertService);
    expect(service).toBeTruthy();
  });
});
