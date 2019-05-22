import { Router, ActivatedRoute } from '@angular/router';

import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ObserveService } from './observe.service';
import {
  JwtService,
  MenuService,
  CommonSemanticService
} from '../../../common/services';
import { AnalyzeService } from '../../analyze/services/analyze.service';
import { Observable } from 'rxjs';

const mockService = {};
class CommonSemanticServiceStub {}

describe('Observe Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ObserveService,
        { provide: JwtService, useValue: mockService },
        { provide: MenuService, useValue: mockService },
        { provide: Router, useValue: mockService },
        { provide: ActivatedRoute, useValue: mockService },
        { provide: AnalyzeService, useValue: mockService },
        {
          provide: CommonSemanticService,
          useValue: new CommonSemanticServiceStub()
        }
      ]
    }).compileComponents();
  });

  it('getSubcategoryCount works for empty array', inject(
    [ObserveService],
    (observe: ObserveService) => {
      expect(observe.getSubcategoryCount([])).toEqual(0);
    }
  ));

  it('executeKPI should exist and return an Observable', inject(
    [ObserveService],
    (observe: ObserveService) => {
      expect(observe.executeKPI({}) instanceof Observable).toEqual(true);
    }
  ));
});
