import { expect } from 'chai';
import { UIRouter } from '@uirouter/angular'

import { configureTests } from '../helpers/configureTests';
import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import * as fpGet from 'lodash/fp/get';
import APP_CONFIG from '../../../../appConfig';

import { WorkbenchService } from '../../../main/javascript/app/modules/workbench/services/workbench.service';


configureTests();

const mockService =  {
};


describe('Workbench Service', () => {
  let httpMock;
  let api = fpGet('api.url', APP_CONFIG);
  let wbAPI = `${this.api}/internal/workbench/projects`;
  let userProject = 'workbench'
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [WorkbenchService,
        {provide: UIRouter, useValue: mockService}]
    }).compileComponents();
    httpMock = TestBed.get(HttpClientTestingModule);
  });

  it('should return error if dataset request failed', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    WorkbenchService.getDatasets().subscribe((res: any) => {
      expect(res.failure.error.type).to.equal('ERROR_LOADING_DATASETS');
    });
  }));

  it('should get the datasets', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    const mockResponse = {
      data: [
      { id: 0, name: 'DS 1', location: 'location 1', creator: 'creator 1', description: 'Desc 1', size: '10000', last_updated_time: 'updated_time 1', updated_by: 'user 1', status: 'status 1', data_set_type: 'type 1', source_type: 'src 1', file_count: 'count 1', creation_time: 'time 1'},
      { id: 0, name: 'DS 2', location: 'location 2', creator: 'creator 2', description: 'Desc 2', size: '10000', last_updated_time: 'updated_time 2', updated_by: 'user 2', status: 'status 2', data_set_type: 'type 2', source_type: 'src 2', file_count: 'count 2', creation_time: 'time 2'}
      ]
    };
    WorkbenchService.getDatasets().subscribe((res: any) => {
      expect(res.length).to.be('2');
      expect(res).to.equal(mockResponse);
    });
    // const request = httpMock.expectOne(`${this.wbAPI}/${this.userProject}/datasets`);
    // expect(request.request.method).to.be('GET');
    // request.flush();
  }));

  it('should return error if staging data request failed', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    WorkbenchService.getStagingData(`${this.wbAPI}/${this.userProject}/raw/directory`).subscribe((res: any) => {
      expect(res.failure.error.type).to.be('ERROR_LOADING_STAGING_DATA');
    });
    // httpMock.verify();
  }));

  it('should get the staging data', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    const mockStagingResponse = {
      'delimiter': ',',
      'description':'It is delimited file inspecting to verify & understand the content of file',
      'fields':[{'name':'DOJ','type':'date','format':['YYYY-MM-DD']},
        {'name':'FirstName','type':'string'},
        {'name':'LastName','type':'string'},
        {'name':'City','type':'string'},
        {'name':'State','type':'string'},
        {'name':'Country','type':'string'}],
      'info':{'totalLines':6,'dataRows':5,'maxFields':6,'minFields':5,'file':'/old_file.csv'},
      'samplesParsed':[
        {'DOJ':'2018-01-27','FirstName':'Employee2','LastName':'Lastname2', 'City': 'Reston', 'State':'Virginia','Country':'USA'},
        {'DOJ':'2018-01-28','FirstName':'Employee3','LastName':'Lastname3', 'City': 'Reston', 'State':'Virginia','Country':'USA'}]
    }
    WorkbenchService.getStagingData(`${this.wbAPI}/${userProject}/raw/directory`).subscribe((res: any) => {
      expect(res.samplesParsed.length).to.be('2');
      expect(res.fields.length).to.equal(6);
      expect(res).to.equal(mockStagingResponse);

      expect(res.delimiter).to.be(',');
      expect(res.description).to.be('It is delimited file inspecting to verify & understand the content of file');

      expect(res.fields[0].name).to.be('DOJ');
      expect(res.fields[0].type).to.be('date');
      expect(res.fields[0].format).to.be('YYYY-MM-DD');

      expect(res.fields[1].name).to.be('FirstName');
      expect(res.fields[1].type).to.be('string');

      expect(res.fields[2].name).to.be('LastName');
      expect(res.fields[2].type).to.be('string');

      expect(res.fields[3].name).to.be('City');
      expect(res.fields[3].type).to.be('string');

      expect(res.fields[4].name).to.be('State');
      expect(res.fields[4].type).to.be('string');

      expect(res.info.totalLines).to.be('6');
      expect(res.info.dataRows).to.be('5');
      expect(res.info.maxFields).to.be('6');
      expect(res.info.minFields).to.be('5');
      expect(res.info.file).to.be('/old_file.csv');

      expect(res.samplesParsed[0].DOJ).to.be('2018-01-27');
      expect(res.samplesParsed[0].FirstName).to.be('Employee2');
      expect(res.samplesParsed[0].LastName).to.be('Lastname2');
      expect(res.samplesParsed[0].City).to.be('Reston');
      expect(res.samplesParsed[0].State).to.be('Virginia');
      expect(res.samplesParsed[0].Country).to.be('USA');

      expect(res.samplesParsed[0].DOJ).to.be('2018-01-28');
      expect(res.samplesParsed[0].FirstName).to.be('Employee3');
      expect(res.samplesParsed[0].LastName).to.be('Lastname3');
      expect(res.samplesParsed[0].City).to.be('Reston');
      expect(res.samplesParsed[0].State).to.be('Virginia');
      expect(res.samplesParsed[0].Country).to.be('USA');
    });
    // const request = httpMock.expectOne();
    // expect(request.request.method).to.be('GET');
    // request.flush();
  }));

  it('should return error if raw preview data request failed', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    WorkbenchService.getRawPreviewData(`${this.wbAPI}/${userProject}/raw/directory/preview`).subscribe((res: any) => {
      expect(res.failure.error.type).to.be('ERROR_LOADING_RAW_PREVIEW_DATA');
    });
    // httpMock.verify();
  }));

  it('should get the rawPreview data', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    const mockRawPreviewData= {
      'projectId': 'workbench',
      'path': '/old_file.csv',
      'data': [
        '2018-01-26, Employee1, LastName1, Reston, Virginia, USA',
        '2018-01-27, Employee2, LastName2, Reston, Virginia, USA',
        '2018-01-28, Employee3, LastName3, Reston, Virginia, USA',
        '2018-01-29, Employee4, LastName4, Reston, Virginia, USA',
        '2018-01-30, Employee5, LastName5, Reston, Virginia, USA'
      ]
    }
    WorkbenchService.getRawPreviewData(`${this.wbAPI}/${userProject}/raw/directory/preview`).subscribe((res: any) => {
      expect(res.projectId).to.be('projectId');
      expect(res.path).to.be('/old_file.csv');
      expect(res.data[0].DOJ).to.be('2018-01-26');
      expect(res.data[0].FirstName).to.be('Employee1');
      expect(res.data[0].LastName).to.be('LastName1');
      expect(res.data[0].City).to.be('Reston');
      expect(res.data[0].State).to.be('Virginia');
      expect(res.data[0].Country).to.be('USA');

      expect(res.data[1].DOJ).to.be('2018-01-27');
      expect(res.data[1].FirstName).to.be('Employee2');
      expect(res.data[1].LastName).to.be('LastName2');
      expect(res.data[1].City).to.be('Reston');
      expect(res.data[1].State).to.be('Virginia');
      expect(res.data[1].Country).to.be('USA');

      expect(res.data[2].DOJ).to.be('2018-01-28');
      expect(res.data[2].FirstName).to.be('Employee3');
      expect(res.data[2].LastName).to.be('LastName3');
      expect(res.data[2].City).to.be('Reston');
      expect(res.data[2].State).to.be('Virginia');
      expect(res.data[2].Country).to.be('USA');

      expect(res.data[3].DOJ).to.be('2018-01-29');
      expect(res.data[3].FirstName).to.be('Employee4');
      expect(res.data[3].LastName).to.be('LastName4');
      expect(res.data[3].City).to.be('Reston');
      expect(res.data[3].State).to.be('Virginia');
      expect(res.data[3].Country).to.be('USA');

      expect(res.data[4].DOJ).to.be('2018-01-30');
      expect(res.data[4].FirstName).to.be('Employee5');
      expect(res.data[4].LastName).to.be('LastName5');
      expect(res.data[4].City).to.be('Reston');
      expect(res.data[4].State).to.be('Virginia');
      expect(res.data[4].Country).to.be('USA');
      expect(res).to.equal(mockRawPreviewData);
    });
    // const request = httpMock.expectOne(`${workBenchService.wbAPI}`);
    // expect(request.request.method).toBe('GET');
    // request.flush();
  }));

  it('should get dataSet details', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    let id = 1;
    const dateSetResponse = {data: [{ id: 0, name: 'DS 1', location: 'location 1', creator: 'creator 1', description: 'Desc 1', size: '10000', last_updated_time: 'updated_time 1', updated_by: 'user 1', status: 'status 1', data_set_type: 'type 1', source_type: 'src 1', file_count: 'count 1', creation_time: 'time 1'}]			};
    WorkbenchService.getDatasetDetails(id).subscribe((res : any) => {
      expect(res[0].name).to.be('DS 1');
      expect(res[0].location).to.be('location 1');
      expect(res[0].creator).to.be('creator 1');
      expect(res[0].description).to.be('Desc 1');
      expect(res[0].size).to.be('10000');
      expect(res[0].last_updated_time).to.be('updated_time 1');
      expect(res[0].updated_by).to.be('user 1');
      expect(res[0].status).to.be('status 1');
      expect(res[0].data_set_type).to.be('type 1');
      expect(res[0].source_type).to.be('src 1');
      expect(res[0].file_count).to.be('count 1');
      expect(res[0].creation_time).to.be('time 1');
      expect(res).to.equal(dateSetResponse);
    });
    // const request = httpMock.expectOne(`${workBenchService.wbAPI}/${projectName}/${id}`);
    // expect(request.request.method).toBe('GET');
    // request.flush();
  }));

  it('should return error if parsed preview data request failed', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    WorkbenchService.getParsedPreviewData(`${this.wbAPI}/${userProject}/raw/directory/inspect`).subscribe((res: any) => {
      expect(res.failure.error.type).to.be('ERROR_LOADING_PARSED_PREVIEW_DATA');
    });
  }));

  it('should get the parsed preview data', inject([WorkbenchService], (WorkbenchService: WorkbenchService) => {
    const mockParsedPreviewData = [{
      '_id': 'workbench::Sample',
      'system': {
        'user': 'A_user',
        'project': 'workbench',
        'dstype': 'base',
        'format': 'parquet',
        'name': 'Sample',
        'catalog': 'data',
        'numberOfFiles': 1
      },
      'asOfNow': {
        'status': 'INIT',
        'started': '20180315-153647',
        'finished': '',
        'aleId': 'workbench::1521128208233',
        'batchId': 'batch-1521128206603'
      },
      'transformations': []
    },{
      '_id': 'workbench::test',
      'system': {
        'user': 'A_user',
        'project': 'workbench',
        'dstype': 'base',
        'format': 'parquet',
        'name': 'test',
        'catalog': 'data',
        'numberOfFiles': 1
      },'asOfNow': {
        'status': 'INIT',
        'started': '20180315-155243',
        'finished': '',
        'aleId': 'workbench::1521129164782',
        'batchId': 'batch-1521129162354'
      },'transformations': []
    }];
    
    WorkbenchService.getParsedPreviewData(`${this.wbAPI}/${userProject}/raw/directory/inspect`).subscribe((res: any) => {
      expect(res[0]._id).to.be('workbench::Sample');
      expect(res[0].system.user).to.be('A_user');
      expect(res[0].system.project).to.be('workbench');
      expect(res[0].system.dstype).to.be('base');
      expect(res[0].system.format).to.be('parquet');
      expect(res[0].system.name).to.be('Sample');
      expect(res[0].system.catalog).to.be('data');
      expect(res[0].system.numberOfFiles).to.be('1');

      expect(res[0].asOfNow.status).to.be('INIT');
      expect(res[0].asOfNow.started).to.be('20180315-153647');
      expect(res[0].asOfNow.finished).to.be('');
      expect(res[0].asOfNow.aleId).to.be('workbench::1521128208233');
      expect(res[0].asOfNow.batchId).to.be('batch-1521128206603');

      expect(res[1]._id).to.be('workbench::test');
      expect(res[1].system.user).to.be('A_user');
      expect(res[1].system.project).to.be('workbench');
      expect(res[1].system.dstype).to.be('base');
      expect(res[1].system.format).to.be('parquet');
      expect(res[1].system.name).to.be('Sample');
      expect(res[1].system.catalog).to.be('data');
      expect(res[1].system.numberOfFiles).to.be('1');

      expect(res[1].asOfNow.status).to.be('INIT');
      expect(res[1].asOfNow.started).to.be('20180315-155243');
      expect(res[1].asOfNow.finished).to.be('');
      expect(res[1].asOfNow.aleId).to.be('workbench::1521129164782');
      expect(res[1].asOfNow.batchId).to.be('batch-1521129162354');
    });

    // const request = httpMock.expectOne(`${WorkbenchService.wbAPI}`);
    // expect(request.request.method).to.be('GET');
    // request.flush();
  }));

});
