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
class MenuServiceStub {
  getMenu() {}
}
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

  it('should read analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'get').and.returnValue(
      asyncData({ analysis: {} } as any)
    );

    service
      .readAnalysis('abc', false)
      .then((res: any) => expect(res).toEqual({}));

    expect(spy.calls.count()).toEqual(1);
  });

  it('should delete analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'delete').and.returnValue(
      asyncData({ analysis: {} } as any)
    );

    service.deleteAnalysis({} as any);
    expect(spy).toHaveBeenCalled();
  });

  it('should save analysis', () => {
    const updateSpy = spyOn(service, 'updateAnalysis').and.returnValue({});
    const createSpy = spyOn(service, 'createAnalysis').and.returnValue({});

    service.saveAnalysis({ id: null } as any);
    expect(createSpy).toHaveBeenCalled();
    expect(updateSpy).not.toHaveBeenCalled();

    service.saveAnalysis({ id: 1 } as any);
    expect(updateSpy).toHaveBeenCalled();
  });

  it('should get categories', () => {
    const menuPromise = Promise.resolve({});
    spy = spyOn(TestBed.get(MenuService), 'getMenu').and.returnValue(
      menuPromise
    );

    expect(service.getCategories(false)).toEqual(menuPromise);
    expect(spy).toHaveBeenCalled();
  });

  it('should fetch cron details', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .getCronDetails({})
      .then((res: any) => expect(res).toEqual({ contents: { analyze: [{}] } }));

    expect(spy.calls.count()).toEqual(1);
  });

  it('should fetch all cron jobs', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .getAllCronJobs({})
      .then((res: any) => expect(res).toEqual({ contents: { analyze: [{}] } }));

    expect(spy.calls.count()).toEqual(1);
  });

  it('create or update an existing analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .getAllCronJobs(analysis)
      .then((res: any) => expect(res).toEqual({ contents: { analyze: [{}] } }));

    expect(spy.calls.count()).toEqual(1);
  });

  it('fetch list of all gtp locations', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service
      .getlistFTP('SYNC')
      .then((res: any) => expect(res).toEqual({ contents: { analyze: [{}] } }));

    expect(spy.calls.count()).toEqual(1);
  });

  it('change schedule', () => {
    spy = spyOn(service, 'postRequest').and.returnValue({
      toPromise: () => {}
    });

    const newSchedule = { scheduleState: 'new' };
    service.changeSchedule(newSchedule);
    expect(spy).toHaveBeenCalledWith('scheduler/schedule', newSchedule);

    const updateSchedule = { scheduleState: 'exist' };
    service.changeSchedule(updateSchedule);
    expect(spy).toHaveBeenCalledWith('scheduler/update', updateSchedule);

    const deleteSchedule = { scheduleState: 'delete' };
    service.changeSchedule(deleteSchedule);
    expect(spy).toHaveBeenCalledWith('scheduler/delete', deleteSchedule);
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
