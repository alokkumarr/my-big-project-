import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

import { AnalyzeDialogService } from './analyze-dialog.service';
import { FilterService } from './filter.service';
import { AnalyzeService } from './analyze.service';

class AnalyzeDialogServiceStub {
  getRequestParams() {
    return {};
  }
}

class AnalyzeServiceStub {
  flattenAndFetchFilters([], []) {
    return [];
  }
}
class RouterStub {}
class LocationStub {
  getMenu() {}
}

describe('Filter Service', () => {
  let service: FilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        FilterService,
        {
          provide: AnalyzeDialogService,
          useValue: new AnalyzeDialogServiceStub()
        },
        {
          provide: AnalyzeService,
          useValue: new AnalyzeServiceStub()
        },
        { provide: Router, useValue: new RouterStub() },
        { provide: Location, useValue: new LocationStub() }
      ]
    });

    service = TestBed.get(FilterService);
  });

  it('should exist', () => {
    expect(service).toBeTruthy();
  });
});
