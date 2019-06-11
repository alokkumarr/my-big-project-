import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as map from 'lodash/map';
import * as reduce from 'lodash/reduce';
import * as fpMapValues from 'lodash/fp/mapValues';

import {
  NUMBER_TYPES,
  FLOAT_TYPES,
  INT_TYPES,
  DATE_TYPES,
  GEO_TYPES,
  CHART_COLORS,
  BACKEND_TIMEZONE,
  DATE_FORMATS,
  DATE_FORMATS_OBJ,
  PIVOT_DATE_FORMATS,
  PIVOT_DATE_FORMATS_OBJ,
  DEFAULT_PIVOT_DATE_FORMAT,
  DEFAULT_DATE_FORMAT,
  CHART_DATE_FORMATS,
  CHART_DATE_FORMATS_OBJ,
  CHART_DEFAULT_DATE_FORMAT,
  AGGREGATE_TYPES,
  DEFAULT_AGGREGATE_TYPE,
  AGGREGATE_TYPES_OBJ,
  DEFAULT_PRECISION
} from '../../common/consts';

export { DATAPOD_CATEGORIES_OBJ } from '../../common/consts';

export {
  NUMBER_TYPES,
  FLOAT_TYPES,
  INT_TYPES,
  DATE_TYPES,
  GEO_TYPES,
  CHART_COLORS,
  BACKEND_TIMEZONE,
  DATE_FORMATS,
  DATE_FORMATS_OBJ,
  PIVOT_DATE_FORMATS,
  PIVOT_DATE_FORMATS_OBJ,
  DEFAULT_PIVOT_DATE_FORMAT,
  DEFAULT_DATE_FORMAT,
  CHART_DATE_FORMATS,
  CHART_DATE_FORMATS_OBJ,
  CHART_DEFAULT_DATE_FORMAT,
  AGGREGATE_TYPES,
  DEFAULT_AGGREGATE_TYPE,
  AGGREGATE_TYPES_OBJ,
  DEFAULT_PRECISION
};

export const DSL_ANALYSIS_TYPES = ['chart', 'map', 'pivot'];

export const Events = {
  AnalysesRefresh: 'Analyses:Refresh'
};

export const AnalyseTypes = {
  Report: 'report',
  ESReport: 'esReport',
  Chart: 'chart',
  Map: 'map',
  Pivot: 'pivot'
};

export const ENTRY_MODES = {
  EDIT: 'edit',
  FORK: 'fork',
  NEW: 'new'
};

export const TYPE_MAP = reduce(
  [
    ...map(NUMBER_TYPES, type => ({ type, generalType: 'number' })),
    ...map(DATE_TYPES, type => ({ type, generalType: 'date' })),
    { type: 'string', generalType: 'string' }
  ],
  (typeMap, { type, generalType }) => {
    typeMap[type] = generalType;
    return typeMap;
  },
  {}
);

export const TYPE_ICONS = [
  {
    icon: 'icon-number-type',
    label: 'Number',
    value: 'number'
  },
  {
    icon: 'icon-string-type',
    label: 'String',
    value: 'string'
  },
  {
    icon: 'icon-geo-chart',
    label: 'Geo',
    value: 'geo'
  },
  {
    icon: 'icon-geo-chart',
    label: 'Coordinate',
    value: 'coordinate'
  },
  {
    icon: 'icon-calendar',
    label: 'Date',
    value: 'date'
  }
];

export const TYPE_ICONS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(TYPE_ICONS);

export const MAX_POSSIBLE_FIELDS_OF_SAME_AREA = 5;

export const PRIVILEGES = {
  ACCESS: 'ACCESS',
  CREATE: 'CREATE',
  EXECUTE: 'EXECUTE',
  PUBLISH: 'PUBLISH',
  FORK: 'FORK',
  EDIT: 'EDIT',
  EXPORT: 'EXPORT',
  DELETE: 'DELETE',
  ALL: 'ALL'
};

export const COMBO_TYPES = [
  {
    label: 'line',
    value: 'line',
    icon: 'icon-line-chart'
  },
  {
    label: 'column',
    value: 'column',
    icon: 'icon-vert-bar-chart'
  },
  {
    label: 'area',
    value: 'area',
    icon: 'icon-area-chart'
  }
];

export const COMBO_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(COMBO_TYPES);

export const TSCOMBO_TYPES = [
  {
    label: 'line',
    value: 'tsspline',
    icon: 'icon-line-chart'
  },
  {
    label: 'column',
    value: 'tscolumn',
    icon: 'icon-vert-bar-chart'
  },
  {
    label: 'area',
    value: 'tsareaspline',
    icon: 'icon-area-chart'
  },
  {
    label: 'line',
    value: 'tsPane',
    icon: 'icon-line-chart'
  }
];

export const TSCOMBO_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(TSCOMBO_TYPES);

export const ANALYSIS_METHODS = [
  {
    label: 'Select Analysis Type',
    category: 'analysis',
    children: [
      {
        label: 'Chart',
        icon: { font: 'icon-vert-bar-chart' },
        type: '',
        children: [
          {
            label: 'Column Chart',
            icon: {
              font: 'icon-vert-bar-chart',
              svg: 'column-chart-col'
            },
            type: 'chart:column'
          },
          {
            label: 'Bar Chart',
            icon: {
              font: 'icon-hor-bar-chart',
              svg: 'bar-chart-col'
            },
            type: 'chart:bar'
          },
          {
            label: 'Stacked Chart',
            icon: {
              font: 'icon-vert-bar-chart',
              svg: 'stack-chart-col'
            },
            type: 'chart:stack'
          },
          {
            label: 'Line Chart',
            icon: {
              font: 'icon-line-chart',
              svg: 'line-chart-col'
            },
            type: 'chart:line'
          },
          {
            label: 'Area Chart',
            icon: {
              font: 'icon-area-chart',
              svg: 'area-chart-col'
            },
            type: 'chart:area'
          },
          {
            label: 'Combo Chart',
            icon: {
              font: 'icon-combo-chart',
              svg: 'combo-chart-col'
            },
            type: 'chart:combo'
          },
          {
            label: 'Scatter Plot',
            icon: {
              font: 'icon-scatter-chart',
              svg: 'scatter-chart-col'
            },
            type: 'chart:scatter'
          },
          {
            label: 'Bubble Chart',
            icon: {
              font: 'icon-bubble-chart',
              svg: 'bubble-chart-col'
            },
            type: 'chart:bubble'
          },
          {
            label: 'Pie Chart',
            icon: {
              font: 'icon-pie-chart',
              svg: 'pie-chart-col'
            },
            type: 'chart:pie'
          },
          {
            label: 'Time series chart',
            icon: {
              font: 'icon-timeseries-chart',
              svg: 'time-series-chart-col'
            },
            type: 'chart:tsspline',
            typeOnBackEnd: 'chart:tsline'
          },
          {
            label: 'Time series multi pane',
            icon: {
              font: 'icon-Candlestick-icon',
              svg: 'time-series-multi-chart-col'
            },
            type: 'chart:tsPane',
            typeOnBackEnd: 'chart:tsareaspline'
          }
        ]
      },
      {
        label: 'Pivot',
        icon: { font: 'icon-pivot' },
        type: 'table:pivot'
      },
      {
        label: 'Report',
        icon: { font: 'icon-report' },
        type: 'table:report',
        supportedTypes: ['table:report', 'table:esReport']
      },
      {
        label: 'Geolocation',
        icon: { font: 'icon-geo-chart' },
        type: '',
        children: [
          {
            label: 'Geolocation',
            icon: { font: 'icon-geo-chart', svg: 'map-chart-col' },
            type: 'map:chart_scale',
            typeOnBackEnd: 'map:chart_scale'
          },
          {
            label: 'Map',
            icon: { font: 'icon-geo-chart', svg: 'map-col' },
            type: 'map:map',
            typeOnBackEnd: 'map-map'
          }
        ]
      }
    ]
  }
];

export const CHART_TYPES_OBJ = fpPipe(
  fpGroupBy('type'),
  fpMapValues(v => v[0])
)(ANALYSIS_METHODS[0].children[0].children);

export const AREA_TYPES = [
  {
    label: 'Row',
    value: 'row',
    icon: 'icon-row'
  },
  {
    label: 'Column',
    value: 'column',
    icon: 'icon-column'
  },
  {
    label: 'Data',
    value: 'data',
    icon: 'icon-data'
  }
];

export const DEFAULT_AREA_TYPE = AREA_TYPES[0];
export const AREA_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AREA_TYPES);

export const DATE_INTERVALS = [
  {
    label: 'All',
    value: 'all'
  },
  {
    label: 'Year',
    value: 'year',
    format: 'yyyy',
    momentFormat: 'yyyy'
  },
  {
    label: 'Quarter',
    value: 'quarter',
    format: 'yyyy-QQQ',
    momentFormat: 'YYYY-[Q]Q'
  },
  {
    label: 'Month',
    value: 'month',
    format: 'yyyy-MM',
    momentFormat: 'YYYY-MM'
  },
  {
    label: 'Date',
    value: 'day',
    format: 'yyyy-MM-dd',
    momentFormat: 'YYYY-MM-DD'
  }
];

export const DEFAULT_DATE_INTERVAL = DATE_INTERVALS[4];

export const DATE_INTERVALS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(DATE_INTERVALS);

export const CUSTOM_DATE_PRESET_VALUE = 'NA';
export const DATE_PRESETS = [
  {
    value: 'Yesterday',
    keyword: 'YESTERDAY',
    label: 'Yesterday'
  },
  {
    value: 'Today',
    keyword: 'TODAY',
    label: 'Today'
  },
  {
    value: 'TW',
    keyword: 'THIS_WEEK',
    label: 'This Week'
  },
  {
    value: 'MTD',
    keyword: 'MONTH_TO_DATE',
    label: 'MTD (Month to Date)'
  },
  {
    value: 'YTD',
    keyword: 'YEAR_TO_DATE',
    label: 'YTD (Year to Date)'
  },
  {
    value: 'LW',
    keyword: 'LAST_WEEK',
    label: 'Last Week'
  },
  {
    value: 'LTW',
    keyword: 'LAST_2_WEEKS',
    label: 'Last 2 Weeks'
  },
  {
    value: 'LM',
    keyword: 'LAST_MONTH',
    label: 'Last Month'
  },
  {
    value: 'LQ',
    keyword: 'LAST_QUARTER',
    label: 'Last Quarter'
  },
  {
    value: 'LTM',
    keyword: 'LAST_3_MONTHS',
    label: 'Last 3 Months'
  },
  {
    value: 'LSM',
    keyword: 'LAST_6_MONTHS',
    label: 'Last 6 Months'
  },
  {
    value: 'LY',
    keyword: 'LAST_YEAR',
    label: 'Last Year'
  },
  {
    value: CUSTOM_DATE_PRESET_VALUE,
    keyword: 'CUSTOM',
    label: 'Custom'
  }
];

export const BETWEEN_NUMBER_FILTER_OPERATOR = {
  value: 'BTW',
  label: 'Between'
};

export const NUMBER_FILTER_OPERATORS = [
  {
    value: 'GT',
    label: 'Greater than'
  },
  {
    value: 'LT',
    label: 'Less than'
  },
  {
    value: 'GTE',
    label: 'Greater than or equal to'
  },
  {
    value: 'LTE',
    label: 'Less than or equal to'
  },
  {
    value: 'EQ',
    label: 'Equal to'
  },
  {
    value: 'NEQ',
    label: 'Not equal to'
  },
  BETWEEN_NUMBER_FILTER_OPERATOR
];

export const NUMBER_FILTER_OPERATORS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(NUMBER_FILTER_OPERATORS);

export const PLURAL_STRING_FILTERS = [
  {
    value: 'ISIN',
    label: 'Is in'
  },
  {
    value: 'ISNOTIN',
    label: 'Is not in'
  }
];

export const SINGULAR_STRING_FILTERS = [
  {
    value: 'EQ',
    label: 'Equals'
  },
  {
    value: 'NEQ',
    label: 'Not equal'
  },
  {
    value: 'CONTAINS',
    label: 'Contains'
  },
  {
    value: 'SW',
    label: 'Starts with'
  },
  {
    value: 'EW',
    label: 'Ends with'
  }
];

export const STRING_FILTER_OPERATORS = [
  ...SINGULAR_STRING_FILTERS,
  ...PLURAL_STRING_FILTERS
];

export const STRING_FILTER_OPERATORS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(STRING_FILTER_OPERATORS);