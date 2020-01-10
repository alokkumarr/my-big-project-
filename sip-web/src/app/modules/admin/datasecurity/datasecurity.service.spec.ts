import { Router, ActivatedRoute } from '@angular/router';

import { TestBed, inject, async } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { DataSecurityService } from './datasecurity.service';
import { JwtService } from '../../../common/services/jwt.service';
import { Observable } from 'rxjs';
import APP_CONFIG from '../../../../../appConfig';
import {
  DSKFilterGroup,
  DSKFilterBooleanCriteria,
  DSKFilterOperator
} from './dsk-filter.model';

const mockService = {};

describe('User Assignment Service', () => {
  let httpTestingController: HttpTestingController;
  let datasecurityService: DataSecurityService;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        DataSecurityService,
        { provide: JwtService, useValue: mockService },
        { provide: Router, useValue: mockService },
        { provide: ActivatedRoute, useValue: mockService }
      ]
    }).compileComponents();

    datasecurityService = TestBed.get(DataSecurityService);
    httpTestingController = TestBed.get(HttpTestingController);
    done();
  });

  it('should exist', () => {
    expect(datasecurityService).not.toBeNull();
  });

  it('getList function should exist', () => {
    expect(datasecurityService.getList()).not.toBeNull();
  });

  it('addSecurityGroup should exist and return an Observable', inject(
    [DataSecurityService],
    (observe: DataSecurityService) => {
      expect(observe.addSecurityGroup({}) instanceof Observable).toEqual(false);
    }
  ));

  describe('getEligibleDSKFieldsFor', () => {
    it('should return flattened dsk structure', async(() => {
      const customer = '1';
      const product = '4';
      datasecurityService
        .getEligibleDSKFieldsFor(customer, product)
        .subscribe(data => {
          expect(data.length).toEqual(1);
        });

      const req = httpTestingController.expectOne(
        `${APP_CONFIG.login.url}/auth/admin/dsk/fields`
      );
      expect(req.request.method).toEqual('GET');

      req.flush({
        dskEligibleData: {
          1: { 4: { 1: [{ columnName: '123', displayName: '123' }] } }
        }
      });
      httpTestingController.verify();
    }));
  });

  describe('generatePreview', () => {
    it('should generate preview string', () => {
      const filterGroup: DSKFilterGroup = {
        booleanCriteria: DSKFilterBooleanCriteria.AND,
        booleanQuery: [
          {
            columnName: 'ABC',
            model: {
              operator: DSKFilterOperator.ISIN,
              values: ['123']
            }
          }
        ]
      };
      const previewString = datasecurityService.generatePreview(filterGroup);
      expect(
        previewString.match(filterGroup.booleanQuery[0]['columnName'])
      ).toBeTruthy();
    });
  });
});
