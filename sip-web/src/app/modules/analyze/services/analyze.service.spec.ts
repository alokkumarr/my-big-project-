import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { AnalyzeService } from './analyze.service';
import {
  JwtService,
  ToastService,
  MenuService
} from '../../../common/services';
import { asyncData } from '../../../common/utils/async-observable-helper';
import { Store } from '@ngxs/store';

class JwtServiceStub {
  getRequestParams() {
    return {};
  }
}
class ToastServiceStub {}
class MenuServiceStub {}
const StoreStub = {};
const analysis = {
  schedule: {
    scheduleState: 'new'
  }
};

describe('Analyze Service', () => {
  let service: AnalyzeService;
  let spy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AnalyzeService,
        { provide: JwtService, useValue: new JwtServiceStub() },
        { provide: MenuService, useValue: new MenuServiceStub() },
        { provide: ToastService, useValue: new ToastServiceStub() },
        { provide: Store, useValue: StoreStub }
      ]
    });

    service = TestBed.get(AnalyzeService);
  });

  it('should exist', () => {
    expect(service).toBeTruthy();
  });

  it('should read a non DSL analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .readAnalysisNonDSL('abc', {})
      .then((res: any) => expect(res).toEqual({}));

    expect(spy.calls.count()).toEqual(1);
  });

  it('should read analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .readAnalysis('abc', false)
      .then((res: any) => expect(res).toEqual({}));

    expect(spy.calls.count()).toEqual(1);
  });

  it('should fetch cron details', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .getCronDetails({})
      .then((res: any) => expect(res).toEqual(({ contents: ({ analyze: [({  }) ] }) })));

    expect(spy.calls.count()).toEqual(1);
  });

  it('should fetch all cron jobs', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .getAllCronJobs({})
      .then((res: any) => expect(res).toEqual(({ contents: ({ analyze: [({  }) ] }) })));

    expect(spy.calls.count()).toEqual(1);
  });

  it('create or update an existing analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .changeSchedule(analysis)
      .then((res: any) => expect(res).toEqual(({ contents: ({ analyze: [({  }) ] }) })));

    expect(spy.calls.count()).toEqual(1);
  });

  describe('calcNameMap', () => {
    it('should return name map for dsl artifacts', () => {
      const nameMap = service.calcNameMap([
        {
          artifactsName: 'abc',
          fields: [
            {
              columnName: 'column',
              displayName: 'display',
              alias: null,
              groupInterval: null,
              area: null,
              dataField: null,
              type: 'string',
              table: 'abc',
              name: '1'
            }
          ]
        }
      ]);

      expect(nameMap['abc']).not.toBeFalsy();
      expect(nameMap['abc']['column']).not.toBeFalsy();
    });
  });
});
