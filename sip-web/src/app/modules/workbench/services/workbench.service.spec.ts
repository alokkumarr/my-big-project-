import { Router } from '@angular/router';

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { WorkbenchService } from './workbench.service';
import { JwtService } from '../../../common/services';
import { Observable } from 'rxjs';

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
        { provide: JwtService, useValue: jwtMockService },
        { provide: Router, useValue: mockService }
      ]
    }).compileComponents();
    workbenchService = TestBed.get(WorkbenchService);
  });

  it('getDatasets should exist and return an Observable', () => {
    expect(workbenchService.getDatasets() instanceof Observable).toBeTruthy();
  });

  it('getStagingData should exist and return an Observable', () => {
    expect(
      workbenchService.getStagingData('path') instanceof Observable
    ).toBeTruthy();
  });

  it('getRawPreviewData should exist and return an Observable', () => {
    expect(
      workbenchService.getRawPreviewData('path') instanceof Observable
    ).toBeTruthy();
  });

  it('getDatasetDetails should exist and return an Observable', () => {
    expect(
      workbenchService.getDatasetDetails(1) instanceof Observable
    ).toBeTruthy();
  });

  it('Data Inspect should exist and return an Observable', () => {
    expect(
      workbenchService.getParsedPreviewData({}) instanceof Observable
    ).toBeTruthy();
  });

  it('createSemantic API should exist and return an Observable', () => {
    expect(
      workbenchService.createSemantic({}) instanceof Observable
    ).toBeTruthy();
  });

  it('getSemanticList API should exist and return an Observable', () => {
    expect(
      workbenchService.getListOfSemantic() instanceof Observable
    ).toBeTruthy();
  });

  it('getSemanticDetails API should exist and return an Observable', () => {
    expect(
      workbenchService.getSemanticDetails({}) instanceof Observable
    ).toBeTruthy();
  });

  it('updateSemantic API should exist and return an Observable', () => {
    expect(
      workbenchService.updateSemanticDetails({}) instanceof Observable
    ).toBeTruthy();
  });

  it('getdataset with filters should exist and return an Observable', () => {
    expect(
      workbenchService.getFilteredDatasets({}) instanceof Observable
    ).toBeTruthy();
  });

  it('should fetch allowable tags for workbench module', () => {
    expect(
      workbenchService.getWorkbenchAllowableTagsList() instanceof Observable
    ).toBeTruthy();
  });

  it('should fetch allowable tags for all projects metadata', () => {
    expect(
      workbenchService.getAllProjectsAllowableTagList() instanceof Observable
    ).toBeTruthy();
  });

  it('should fetch list of names of streams and topics ', () => {
    expect(
      workbenchService.getListOfStreams() instanceof Observable
    ).toBeTruthy();
  });

  it('should fetch data for topic grid ', () => {
    expect(
      workbenchService.getListOfTopics('streams_!', 'type_1') instanceof
        Observable
    ).toBeTruthy();
  });
});
