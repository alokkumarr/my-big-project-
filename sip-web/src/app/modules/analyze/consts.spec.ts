import {
  TYPE_ICONS,
  DSL_ANALYSIS_TYPES,
  getFilterValue,
  STRING_FILTER_OPERATORS_OBJ,
  TSCOMBO_TYPES_OBJ,
  CHART_TYPES_OBJ,
  getFilterDisplayName
} from './consts';
import moment from 'moment';

describe('Analyze Constants', () => {
  it('should import chart types object', () => {
    expect(typeof CHART_TYPES_OBJ).toEqual('object');
  });

  it('should exist', () => {
    expect(TYPE_ICONS).toBeTruthy();
  });

  it('check is dsl type of analysis is defined', () => {
    expect(DSL_ANALYSIS_TYPES).toBeDefined();
  });

  it('check ts combo object', () => {
    expect(TSCOMBO_TYPES_OBJ).toBeDefined();
  });

  it('check with string filter operator', () => {
    expect(STRING_FILTER_OPERATORS_OBJ).toBeDefined();
  });

  it('should return correct complete filter value', () => {
    const nameMap = { table: { ABC: 'Abc' } };
    const text1 = getFilterDisplayName(nameMap, {
      isAggregationFilter: true,
      isRuntimeFilter: false,
      isOptional: false,
      columnName: 'ABC',
      aggregate: 'count',
      type: 'string',
      tableName: 'table',
      model: {
        operator: 'EQ',
        value: 123
      }
    });

    const text2 = getFilterDisplayName(nameMap, {
      isAggregationFilter: false,
      isRuntimeFilter: false,
      isOptional: false,
      columnName: 'ABC',
      type: 'integer',
      tableName: 'table',
      model: {
        operator: 'EQ',
        value: 123
      }
    });

    /* Works with artifactsName instead of tableName */
    const text3 = getFilterDisplayName(nameMap, {
      isAggregationFilter: false,
      isRuntimeFilter: false,
      isOptional: false,
      columnName: 'ABC',
      type: 'integer',
      artifactsName: 'table',
      model: {
        operator: 'EQ',
        value: 123
      }
    });
    expect(text1).toEqual('CNT(Abc): Equal to 123');
    expect(text2).toEqual('Abc: Equal to 123');
    expect(text3).toEqual('Abc: Equal to 123');
  });

  it('check if getfilter value is a function', () => {
    expect(typeof getFilterValue).toEqual('function');
    const Numberfilter = {
      columnName: 'AVAILABLE_ITEMS',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { operator: 'GT', value: 10 },
      tableName: 'mct_tgt_session',
      type: 'double'
    };

    expect(getFilterValue(Numberfilter)).toEqual(': Greater than 10');

    const longfilter = {
      columnName: 'AVAILABLE_ITEMS',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { operator: 'GT', value: 30 },
      tableName: 'mct_tgt_session',
      type: 'long'
    };

    expect(getFilterValue(longfilter)).toEqual(': Greater than 30');

    const datefilter = {
      columnName: 'TRANSFER_DATE',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { preset: 'LY' },
      tableName: 'mct_tgt_session',
      type: 'date'
    };

    expect(getFilterValue(datefilter)).toEqual(': LY');

    const dateSecondfilter = {
      columnName: 'TRANSFER_DATE',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { preset: 'LSM' },
      tableName: 'mct_tgt_session',
      type: 'date'
    };

    expect(getFilterValue(dateSecondfilter)).toEqual(': LSM');

    const customDateFilter = {
      columnName: 'TRANSFER_DATE',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: {
        preset: 'NA',
        gte: '2019-10-22 00:00:00',
        lte: '2019-10-22 23:59:59'
      },
      tableName: 'mct_tgt_session',
      type: 'date'
    };

    expect(getFilterValue(customDateFilter)).toEqual(
      ': From 2019-10-22 00:00:00 To 2019-10-22 23:59:59'
    );

    const customBTWDateFilter = {
      columnName: 'TRANSFER_DATE',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: {
        operator: 'BTW',
        gte: moment()
          .startOf('day')
          .format('YYYY-MM-DD'),
        lte: moment()
          .endOf('day')
          .format('YYYY-MM-DD')
      },
      tableName: 'mct_tgt_session',
      type: 'date'
    };

    const value = `: From ${moment()
      .startOf('day')
      .format('YYYY-MM-DD')} to ${moment()
      .endOf('day')
      .format('YYYY-MM-DD')}`;

    expect(getFilterValue(customBTWDateFilter)).toEqual(value);

    const stringFilter = {
      columnName: 'DAY_NAME.keyword',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { modelValues: ['a'], operator: 'CONTAINS' },
      tableName: 'mct_tgt_session',
      type: 'string'
    };

    expect(getFilterValue(stringFilter)).toEqual(': Contains a');

    const stringIsInFilter = {
      columnName: 'DAY_NAME.keyword',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { modelValues: ['a', 'f'], operator: 'ISIN' },
      tableName: 'mct_tgt_session',
      type: 'string'
    };

    expect(getFilterValue(stringIsInFilter)).toEqual(': Is in a, f');

    const NumberBTWfilter = {
      columnName: 'AVAILABLE_ITEMS',
      isGlobalFilter: false,
      isOptional: false,
      isRuntimeFilter: false,
      model: { operator: 'BTW', value: 10000000, otherValue: 10 },
      tableName: 'mct_tgt_session',
      type: 'double'
    };

    expect(getFilterValue(NumberBTWfilter)).toEqual(': 10 Between 10000000');

    const nullFilter = {
      sample: {}
    };
    expect(getFilterValue(nullFilter)).toEqual('');
  });
});
