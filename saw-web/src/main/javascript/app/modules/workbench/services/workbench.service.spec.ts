import { expect } from 'chai';
import { Router } from '@angular/router';

import { configureTests } from '../../../../../../test/javascript/helpers/configureTests';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { WorkbenchService } from './workbench.service';
import { JwtService } from '../../../common/services';
import { Observable } from 'rxjs/Observable';

configureTests();

const mockService = {};
const jwtMockService = {
  customerCode: 'Synchronoss',

  getUserName() {
    return 'SIP Admin';
  }
};

describe('Workbench Service', () => {
  let workbenchService: WorkbenchService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        WorkbenchService,
        { provide: JwtService, useValue: jwtMockService },
        { provide: Router, useValue: mockService }
      ]
    }).compileComponents();
    workbenchService = TestBed.get(WorkbenchService);
  });

  it('getDatasets should exist and return an Observable', () => {
    expect(workbenchService.getDatasets() instanceof Observable).to.equal(true);
  });

  it('getStagingData should exist and return an Observable', () => {
    expect(
      workbenchService.getStagingData('path') instanceof Observable
    ).to.equal(true);
  });

  it('getRawPreviewData should exist and return an Observable', () => {
    expect(
      workbenchService.getRawPreviewData('path') instanceof Observable
    ).to.equal(true);
  });

  it('getDatasetDetails should exist and return an Observable', () => {
    expect(
      workbenchService.getDatasetDetails(1) instanceof Observable
    ).to.equal(true);
  });

  it('Data Inspect should exist and return an Observable', () => {
    expect(
      workbenchService.getParsedPreviewData({}) instanceof Observable
    ).to.equal(true);
  });

  it('createSemantic API should exist and return an Observable', () => {
    expect(workbenchService.createSemantic({}) instanceof Observable).to.equal(
      true
    );
  });

  it('getSemanticList API should exist and return an Observable', () => {
    expect(workbenchService.getListOfSemantic() instanceof Observable).to.equal(
      true
    );
  });

  it('getSemanticDetails API should exist and return an Observable', () => {
    expect(
      workbenchService.getSemanticDetails({}) instanceof Observable
    ).to.equal(true);
  });

  it('updateSemantic API should exist and return an Observable', () => {
    expect(
      workbenchService.updateSemanticDetails({}) instanceof Observable
    ).to.equal(true);
  });
});
