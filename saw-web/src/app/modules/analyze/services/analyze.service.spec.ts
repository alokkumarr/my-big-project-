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

class JwtServiceStub {
  getRequestParams() {
    return {};
  }
}
class ToastServiceStub {}
class MenuServiceStub {}

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
        { provide: ToastService, useValue: new ToastServiceStub() }
      ]
    });

    service = TestBed.get(AnalyzeService);
  });

  it('should exist', () => {
    expect(service).toBeTruthy();
  });

  it('should read analysis', () => {
    spy = spyOn(TestBed.get(HttpClient), 'post').and.returnValue(
      asyncData({ contents: { analyze: [{}] } } as any)
    );

    service.readAnalysis('abc').then((res: any) => expect(res).toEqual({}));

    expect(spy.calls.count()).toEqual(1);
  });
});
