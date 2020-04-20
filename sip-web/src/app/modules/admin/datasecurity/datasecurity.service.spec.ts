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

const mockService = {};

describe('Data Security Service', () => {
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

  describe('getFiltersFor', () => {
    it('should get filters', () => {
      const spy = spyOn(datasecurityService, 'getRequest').and.returnValue({
        pipe: () => {}
      });
      datasecurityService.getFiltersFor('123');
      expect(spy).toHaveBeenCalledWith('auth/admin/v1/dsk-security-groups/123');
    });
  });

  describe('getList', () => {
    it('should get list of user assignments', () => {
      const spy = spyOn(datasecurityService, 'getRequest').and.returnValue({
        toPromise: () => {}
      });
      datasecurityService.getList();
      expect(spy).toHaveBeenCalledWith('auth/admin/user-assignments');
    });
  });

  describe('attributeToGroup', () => {
    it('should call post if new attribute is being created', () => {
      const spy = spyOn(datasecurityService, 'postRequest').and.returnValue({});
      datasecurityService.attributetoGroup({
        attributeName: '',
        value: '',
        mode: 'create',
        groupSelected: { secGroupSysId: '2' }
      });
      expect(spy).toHaveBeenCalled();
    });

    it('should call put if old attribute is being edited', () => {
      const spy = spyOn(datasecurityService, 'putrequest').and.returnValue({});
      datasecurityService.attributetoGroup({
        attributeName: '',
        value: '',
        mode: 'edit',
        groupSelected: { secGroupSysId: '2' }
      });
      expect(spy).toHaveBeenCalled();
    });
  });

  describe('assignGroupToUser', () => {
    it('should call correct url', () => {
      const spy = spyOn(datasecurityService, 'putrequest').and.returnValue({});
      datasecurityService.assignGroupToUser({
        userId: '123'
      });
      expect(spy).toHaveBeenCalled();
    });
  });
});
