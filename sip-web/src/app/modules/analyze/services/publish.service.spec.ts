import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
// import { HttpClient } from '@angular/common/http';
import { AnalyzeService } from './analyze.service';
import { PublishService } from './publish.service';
import {
  JwtService
} from '../../../common/services';
import { ExecuteService } from './execute.service';
// import { asyncData } from '../../../common/utils/async-observable-helper';

class JwtServiceStub {
  getRequestParams() {
    return {};
  }
}
class ExecuteServiceStub {}

class AnalyzeServiceStub {}

describe('Publish Service', () => {
  let service: PublishService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PublishService,
        { provide: JwtService, useValue: new JwtServiceStub() },
        { provide: ExecuteService, useValue: new ExecuteServiceStub() },
        { provide: AnalyzeService, useValue: new AnalyzeServiceStub() }
      ]
    });

    service = TestBed.get(PublishService);
  });

  it('should exist', () => {
    expect(service).toBeTruthy();
  });
});
