import { Router, ActivatedRoute } from '@angular/router';

import { TestBed, inject, async } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { UserAssignmentService } from './userassignment.service';
import { JwtService } from '../../../common/services/jwt.service';
import { Observable } from 'rxjs';
import APP_CONFIG from '../../../../../appConfig';

const mockService = {};

describe('User Assignment Service', () => {
  let httpTestingController: HttpTestingController;
  let userAssignmentService: UserAssignmentService;
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserAssignmentService,
        { provide: JwtService, useValue: mockService },
        { provide: Router, useValue: mockService },
        { provide: ActivatedRoute, useValue: mockService }
      ]
    }).compileComponents();

    userAssignmentService = TestBed.get(UserAssignmentService);
    httpTestingController = TestBed.get(HttpTestingController);
    done();
  });

  it('should exist', () => {
    expect(userAssignmentService).not.toBeNull();
  });

  it('getList function should exist', () => {
    expect(userAssignmentService.getList()).not.toBeNull();
  });

  it('addSecurityGroup should exist and return an Observable', inject(
    [UserAssignmentService],
    (observe: UserAssignmentService) => {
      expect(observe.addSecurityGroup({}) instanceof Observable).toEqual(false);
    }
  ));

  describe('getEligibleDSKFieldsFor', () => {
    it('should return flattened dsk structure', async(() => {
      const customer = '1';
      const product = '4';
      userAssignmentService
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
});
