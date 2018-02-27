import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as map from 'lodash/map';
import * as reduce from 'lodash/reduce';
import * as fpMapValues from 'lodash/fp/mapValues';

import {
  NUMBER_TYPES,
  FLOAT_TYPES,
  DATE_TYPES,
  CHART_COLORS,
  BACKEND_TIMEZONE
} from '../../common/consts.js';

export {
  NUMBER_TYPES,
  FLOAT_TYPES,
  DATE_TYPES,
  CHART_COLORS,
  BACKEND_TIMEZONE
};

export const Events = {
  AnalysesRefresh: 'Analyses:Refresh'
};

export const AnalyseTypes = {
  Report: 'report',
  ESReport: 'esReport',
  Chart: 'chart',
  Pivot: 'pivot'
};

export const ENTRY_MODES = {
  EDIT: 'edit',
  FORK: 'fork',
  NEW: 'new'
};

export const LAST_ANALYSES_CATEGORY_ID = 'lastAnalysesListId';

export const TYPE_MAP = reduce([
  ...map(NUMBER_TYPES, type => ({type, generalType: 'number'})),
  ...map(DATE_TYPES, type => ({type, generalType: 'date'})),
  {type: 'string', generalType: 'string'}
], (typeMap, {type, generalType}) => {
  typeMap[type] = generalType;
  return typeMap;
}, {});

export const TYPE_ICONS = [{
  icon: 'icon-number-type',
  label: 'Number',
  value: 'number'
}, {
  icon: 'icon-string-type',
  label: 'String',
  value: 'string'
}, {
  icon: 'icon-calendar',
  label: 'Date',
  value: 'date'
}];

export const TYPE_ICONS_OBJ = fpPipe(
  fpGroupBy('type'),
  fpMapValues(v => v[0])
)([
  ...map(NUMBER_TYPES, type => ({type, icon: 'icon-number-type'})),
  ...map(DATE_TYPES, type => ({type, icon: 'icon-calendar'})),
  {type: 'string', icon: 'icon-string-type'}
]);

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

export const AGGREGATE_TYPES = [{
  label: 'Total',
  value: 'sum',
  icon: 'icon-Sum'
}, {
  label: 'Average',
  value: 'avg',
  icon: 'icon-AVG'
}, {
  label: 'Mininum',
  value: 'min',
  icon: 'icon-MIN'
}, {
  label: 'Maximum',
  value: 'max',
  icon: 'icon-MAX'
}, {
  label: 'Count',
  value: 'count',
  icon: 'icon-Count'
}];

export const DEFAULT_AGGREGATE_TYPE = AGGREGATE_TYPES[0];

export const AGGREGATE_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AGGREGATE_TYPES);

export const COMBO_TYPES = [{
  label: 'line',
  value: 'line',
  icon: 'icon-line-chart'
}, {
  label: 'column',
  value: 'column',
  icon: 'icon-vert-bar-chart'
}, {
  label: 'area',
  value: 'area',
  icon: 'icon-area-chart'
}];

export const COMBO_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(COMBO_TYPES);

export const TSCOMBO_TYPES = [{
  label: 'line',
  value: 'tsspline',
  icon: 'icon-line-chart'
}, {
  label: 'column',
  value: 'tscolumn',
  icon: 'icon-vert-bar-chart'
}, {
  label: 'area',
  value: 'tsareaspline',
  icon: 'icon-area-chart'
}, {
  label: 'line',
  value: 'tsPane',
  icon: 'icon-line-chart'
}];

export const TSCOMBO_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(TSCOMBO_TYPES);

export const ANALYSIS_METHODS = [
  {
    label: 'TABLES',
    category: 'table',
    children: [
      {
        label: 'Report',
        icon: {font: 'icon-report'},
        type: 'table:report',
        supportedTypes: ['table:report', 'table:esReport']
      },
      {
        label: 'Pivot',
        icon: {font: 'icon-pivot'},
        type: 'table:pivot'
      }
    ]
  },
  {
    label: 'CHARTS',
    category: 'charts',
    children: [
      {
        label: 'Column Chart',
        icon: {font: 'icon-vert-bar-chart'},
        type: 'chart:column'
      },
      {
        label: 'Bar Chart',
        icon: {font: 'icon-hor-bar-chart'},
        type: 'chart:bar'
      },
      {
        label: 'Stacked Chart',
        icon: {font: 'icon-vert-bar-chart'},
        type: 'chart:stack'
      },
      {
        label: 'Line Chart',
        icon: {font: 'icon-line-chart'},
        type: 'chart:line'
      },
      {
        label: 'Area Chart',
        icon: {font: 'icon-area-chart'},
        type: 'chart:area'
      },
      {
        label: 'Combo Chart',
        icon: {font: 'icon-combo-chart'},
        type: 'chart:combo'
      },
      {
        label: 'Scatter Plot',
        icon: {font: 'icon-scatter-chart'},
        type: 'chart:scatter'
      },
      {
        label: 'Bubble Chart',
        icon: {font: 'icon-bubble-chart'},
        type: 'chart:bubble'
      },
      {
        label: 'Pie Chart',
        icon: {font: 'icon-pie-chart'},
        type: 'chart:pie'
      },
      {
        label: 'Time series chart',
        icon: {font: 'icon-timeseries-chart'},
        type: 'chart:tsspline'
      },
      {
        label: 'Time series multi pane',
        icon: {font: 'icon-Candlestick-icon'},
        type: 'chart:tsPane'
      }
    ]
  }
];

export const CHART_TYPES_OBJ = fpPipe(
  fpGroupBy('type'),
  fpMapValues(v => v[0])
)(ANALYSIS_METHODS[1].children);

export const AREA_TYPES = [{
  label: 'Row',
  value: 'row',
  icon: 'icon-row'
}, {
  label: 'Column',
  value: 'column',
  icon: 'icon-column'
}, {
  label: 'Data',
  value: 'data',
  icon: 'icon-data'
}];

export const DEFAULT_AREA_TYPE = AREA_TYPES[0];
export const AREA_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AREA_TYPES);

export const DATE_INTERVALS = [{
  label: 'All',
  value: 'all'
}, {
  label: 'Year',
  value: 'year'
}, {
  label: 'Quarter',
  value: 'quarter',
  format: 'YYYY-Q'
}, {
  label: 'Month',
  value: 'month',
  format: 'YYYY-MM'
}, {
  label: 'Date',
  value: 'day',
  format: 'YYYY-MM-DD'
}];

export const DEFAULT_DATE_INTERVAL = DATE_INTERVALS[4];

export const DATE_INTERVALS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(DATE_INTERVALS);

export const CUSTOM_DATE_PRESET_VALUE = 'NA';
export const DATE_PRESETS = [{
  value: 'TW',
  keyword: 'THIS_WEEK',
  label: 'This Week'
}, {
  value: 'MTD',
  keyword: 'MONTH_TO_DATE',
  label: 'MTD (Month to Date)'
}, {
  value: 'YTD',
  keyword: 'YEAR_TO_DATE',
  label: 'YTD (Year to Date)'
}, {
  value: 'LW',
  keyword: 'LAST_WEEK',
  label: 'Last Week'
}, {
  value: 'LTW',
  keyword: 'LAST_2_WEEKS',
  label: 'Last 2 Weeks'
}, {
  value: 'LM',
  keyword: 'LAST_MONTH',
  label: 'Last Month'
}, {
  value: 'LQ',
  keyword: 'LAST_QUARTER',
  label: 'Last Quarter'
}, {
  value: 'LTM',
  keyword: 'LAST_3_MONTHS',
  label: 'Last 3 Months'
}, {
  value: 'LSM',
  keyword: 'LAST_6_MONTHS',
  label: 'Last 6 Months'
}, {
  value: CUSTOM_DATE_PRESET_VALUE,
  keyword: 'CUSTOM',
  label: 'Custom'
}];
