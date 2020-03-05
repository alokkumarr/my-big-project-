import { TestBed } from '@angular/core/testing';
import { ChartService } from './chart.service';

describe('Chart Service', () => {
  let service: ChartService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ChartService]
    });

    service = TestBed.get(ChartService);
  });

  it('should be initialised', () => {
    expect(service).toBeTruthy();
  });

  describe('initLegend', () => {
    it('should return legend config', () => {
      const legend = service.initLegend({ chartType: 'combo' });
      expect(legend.align).not.toBeNull();
    });
  });

  describe('getDataFieldIdentifier', () => {
    it('should get data field identifier correctly', () => {
      expect(
        service.getDataFieldIdentifier({
          expression: 'abc',
          columnName: 'ABC'
        } as any)
      ).toEqual('ABC');
      expect(
        service.getDataFieldIdentifier({
          aggregate: 'sum',
          columnName: 'ABC'
        } as any)
      ).toEqual('sum@@abc');
      expect(
        service.getDataFieldIdentifier({ columnName: 'ABC.keyword' } as any)
      ).toEqual('ABC');
    });
  });
});
