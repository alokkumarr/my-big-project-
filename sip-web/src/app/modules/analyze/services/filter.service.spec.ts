import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AnalyzeDialogService } from './analyze-dialog.service';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { FilterService } from './filter.service';

class AnalyzeDialogServiceStub {
  getRequestParams() {
    return {};
  }
}
class RouterStub {}
class LocationStub {
  getMenu() {}
}
const analysis = {
  sipQuery: {
    filters: [
      {
        isRuntime: true,
        model: 'something'
      },
      {
        isRuntime: false,
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

    expect(cleanedFilters[0].model).not.toBeUndefined();
    expect(cleanedFilters[1].model).toBeDefined();
  });
});
