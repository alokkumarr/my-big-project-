import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { DatasourceService } from './datasource.service';
import { JwtService } from '../../../common/services';
import { Observable } from 'rxjs/Observable';

const jwtMockService = {
  customerCode: 'Synchronoss',

  getUserName() {
    return 'SIP Admin';
  },

  getProductName() {
    return 'SIP';
  }
};
describe('DatasourceService', () => {
  let datasourceService: DatasourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: JwtService, useValue: jwtMockService }]
    });
    datasourceService = TestBed.get(DatasourceService);
  });

  it('should be created', () => {
    const service: DatasourceService = TestBed.get(DatasourceService);
    expect(service).toBeTruthy();
  });

  it('getSourceList should exist and return an Observable', () => {
    expect(
      datasourceService.getSourceList() instanceof Observable
    ).toBeTruthy();
  });

  it('getRoutesList should exist and return an Observable', () => {
    expect(
      datasourceService.getRoutesList(1) instanceof Observable
    ).toBeTruthy();
  });

  it('deleteChannel should exist and return an Observable', () => {
    expect(
      datasourceService.deleteChannel(1) instanceof Observable
    ).toBeTruthy();
  });

  it('updateSource should exist and return an Observable', () => {
    expect(
      datasourceService.updateSource(1, []) instanceof Observable
    ).toBeTruthy();
  });

  it('createSource should exist and return an Observable', () => {
    expect(
      datasourceService.createSource([]) instanceof Observable
    ).toBeTruthy();
  });

  it('createRoute should exist and return an Observable', () => {
    expect(
      datasourceService.createRoute(1, []) instanceof Observable
    ).toBeTruthy();
  });

  it('updateRoute should exist and return an Observable', () => {
    expect(
      datasourceService.updateRoute(1, 1, []) instanceof Observable
    ).toBeTruthy();
  });

  it('deleteRoute should exist and return an Observable', () => {
    expect(
      datasourceService.deleteRoute(1, 1) instanceof Observable
    ).toBeTruthy();
  });

  it('decryptPWD should exist and return an Observable', () => {
    expect(
      datasourceService.decryptPWD('123') instanceof Observable
    ).toBeTruthy();
  });

  it('encryptPWD should exist and return an Observable', () => {
    expect(
      datasourceService.encryptPWD('123') instanceof Observable
    ).toBeTruthy();
  });
});
