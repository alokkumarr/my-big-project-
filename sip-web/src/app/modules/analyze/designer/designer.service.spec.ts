import { TestBed } from '@angular/core/testing';
import { DesignerService } from './designer.module';
import { AnalyzeService } from '../services/analyze.service';
import { ArtifactColumnDSL } from './types';

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

  it('should return display name correctly, without reapplying aggregates', () => {
    /* Not a metric field */
    expect(
      DesignerService.displayNameFor({
        area: 'x',
        displayName: 'abc(def)'
      } as ArtifactColumnDSL)
    ).toEqual('def');

    /* Doesn't have parenthesis, not a metric field */
    expect(
      DesignerService.displayNameFor({
        area: 'x',
        displayName: 'abc(def'
      } as ArtifactColumnDSL)
    ).toEqual('abc(def');

    /* Metric field. Has Parenthesis. */
    expect(
      DesignerService.displayNameFor({
        area: 'y',
        aggregate: 'sum',
        displayName: 'abc(def)'
      } as ArtifactColumnDSL)
    ).toEqual('SUM(def)');

    /* Doesn't have parenthesis, metric field */
    expect(
      DesignerService.displayNameFor({
        area: 'y',
        aggregate: 'sum',
        displayName: 'def'
      } as ArtifactColumnDSL)
    ).toEqual('SUM(def)');
  });
});
