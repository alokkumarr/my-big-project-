import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { DatasourceService } from './datasource.service';
import { JwtService } from '../../../common/services';

const jwtMockService = {
  customerCode: 'Synchronoss',

  getUserName() {
    return 'SIP Admin';
  }
};
describe('DatasourceService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        DatasourceService,
        { provide: JwtService, useValue: jwtMockService }
      ]
    }));

  it('should be created', () => {
    const service: DatasourceService = TestBed.get(DatasourceService);
    expect(service).toBeTruthy();
  });
});
