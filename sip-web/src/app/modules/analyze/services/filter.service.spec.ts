import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import * as every from 'lodash/every';

import { AnalyzeDialogService } from './analyze-dialog.service';
import { FilterService } from './filter.service';
import { AnalyzeService } from './analyze.service';
import { JwtService } from './../../../common/services/jwt.service';
import { ToastService } from './../../../common/services/toastMessage.service';

class AnalyzeDialogServiceStub {
  getRequestParams() {
    return {};
  }
}
class RouterStub {}
class LocationStub {
  getMenu() {}
}

const allFiltersWithEmptyRuntimeFilters = [
  {
    isRuntimeFilter: true
  },
  {
    isRuntimeFilter: false,
    model: 'something'
  }
];

const runtimeFiltersWithValues = [
  {
    isRuntimeFilter: true,
    model: 'something'
  }
];

const analysis = {
  sipQuery: {
    filters: [
      {
        isRuntimeFilter: true,
        model: 'something'
      },
      {
        isRuntimeFilter: false,
        model: 'something'
      }
    ]
  }
};

describe('Filter Service', () => {
  let service: FilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        FilterService,
        AnalyzeService,
        JwtService,
        ToastService,
        {
          provide: AnalyzeDialogService,
          useValue: new AnalyzeDialogServiceStub()
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

  it('should clean runtime filter values', () => {
    const cleanedFilters = service.getCleanedRuntimeFilterValues(analysis);

    expect(cleanedFilters[0].model).toBeUndefined();
    expect(cleanedFilters.length).toBe(1);
  });

  it('should merge runtime filters with values into all filters', () => {
    const allFiltersWithValues = service.mergeValuedRuntimeFiltersIntoFilters(
      runtimeFiltersWithValues,
      allFiltersWithEmptyRuntimeFilters
    );
    expect(every(allFiltersWithValues, 'model')).toBe(true);
  });
});
