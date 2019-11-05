import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  getTooltipFormatter,
  getTooltipFormats,
  displayNameWithoutAggregateFor
} from './tooltipFormatter';

describe('Analyze Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: []
    });
  });

  it('should call formatter function', () => {
    const response = getTooltipFormatter({}, 'column');
    expect(response).toBeTruthy();
  });

  it('should fetch all tool tip formats', () => {
    const response = getTooltipFormats({}, 'column');
    expect(response).toBeTruthy();
  });

  it('should not apply aggregate to derived metrics', () => {
    const tooltip = getTooltipFormatter(
      {
        y: [{ expression: 'abc', columnName: 'abc', displayName: 'abc' }],
        x: {},
        g: {}
      },
      'column'
    ).bind({ series: {} })();

    expect(/abc:/.test(tooltip)).toBeTruthy();
  });

  describe('displayNameWithoutAggregateFor', () => {
    it('should return display name without parenthesis (aggregates) if datafield present', () => {
      expect(
        displayNameWithoutAggregateFor({
          dataField: 'abc',
          displayName: 'avg(double)'
        })
      ).toEqual('double');

      expect(
        displayNameWithoutAggregateFor({
          dataField: 'abc',
          displayName: 'avg(default(double))'
        })
      ).toEqual('default(double)');
    });
  });
});
