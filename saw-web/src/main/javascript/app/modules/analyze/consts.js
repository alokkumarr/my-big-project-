import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as map from 'lodash/map';
import * as fpMapValues from 'lodash/fp/mapValues';

import {NUMBER_TYPES, DATE_TYPES, CHART_COLORS} from '../../common/consts.js';

export const Events = {
  AnalysesRefresh: 'Analyses:Refresh'
};

export const AnalyseTypes = {
  Report: 'report',
  Chart: 'chart',
  Pivot: 'pivot'
};

export const ENTRY_MODES = {
  EDIT: 'edit',
  FORK: 'fork',
  NEW: 'new'
};

export const LAST_ANALYSES_CATEGORY_ID = 'lastAnalysesListId';

export {NUMBER_TYPES, DATE_TYPES, CHART_COLORS};

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
  icon: 'icon-vert-bar-chart'
}];

export const COMBO_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(COMBO_TYPES);

export const ANALYSIS_METHODS = [
  {
    label: 'TABLES',
    category: 'table',
    children: [
      {
        label: 'Report',
        icon: {font: 'icon-report'},
        type: 'table:report'
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
  value: undefined
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
