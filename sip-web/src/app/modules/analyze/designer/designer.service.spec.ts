import { TestBed } from '@angular/core/testing';
import { DesignerService } from './designer.module';
import { AnalyzeService } from '../services/analyze.service';

describe('Designer Service', () => {
  let service: DesignerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        DesignerService,
        {
          provide: AnalyzeService,
          useValue: {}
        }
      ]
    });

    service = TestBed.get(DesignerService);
  });

  it('should exist', () => {
    expect(service).toBeTruthy();
  });

  it('should return dataField for a column', () => {
    expect(
      DesignerService.dataFieldFor({
        aggregate: 'sum',
        columnName: 'double'
      } as any)
    ).toEqual('sum@@double');

    expect(() => DesignerService.dataFieldFor({} as any)).toThrow();
  });
});
