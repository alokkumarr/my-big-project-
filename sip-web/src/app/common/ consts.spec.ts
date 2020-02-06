import {
  AGGREGATE_TYPES,
  filterAggregatesByDataType,
  filterAggregatesByAnalysisType
} from './consts';
describe('Common Constants', () => {
  describe('filterAggregatesByDataType', () => {
    it('should filter aggregates by data type', () => {
      const firstDateAggregate = AGGREGATE_TYPES.find(agg =>
        agg.validDataType.includes('date')
      ).value;
      expect(filterAggregatesByDataType('date')[0].value).toEqual(
        firstDateAggregate
      );
    });

    it('should return empty array for invalid data types', () => {
      expect(filterAggregatesByDataType('abc').length).toEqual(0);
    });
  });

  describe('filterAggregatesByAnalysisType', () => {
    it('should filter aggregates by data type', () => {
      const firstChartAggregate = AGGREGATE_TYPES.find(agg =>
        agg.valid.includes('chart')
      ).value;
      expect(filterAggregatesByAnalysisType('chart')[0].value).toEqual(
        firstChartAggregate
      );
    });

    it('should return empty array for invalid data types', () => {
      expect(filterAggregatesByAnalysisType('abc').length).toEqual(0);
    });

    it('should filter from inputs when given', () => {
      expect(
        filterAggregatesByAnalysisType('chart', [{ valid: ['chart'] }] as any)
          .length
      ).toEqual(1);
    });
  });
});
