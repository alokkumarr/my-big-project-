
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMapValues from 'lodash/fp/mapValues';

export const INT_TYPES = ['int', 'integer', 'long'];
export const FLOAT_TYPES = ['double', 'float'];
export const DEFAULT_PRECISION = 2;
export const NUMBER_TYPES = [...INT_TYPES, ...FLOAT_TYPES];
export const DATE_TYPES = ['timestamp', 'date'];

export const BACKEND_TIMEZONE = 'America/New_York';

export const CHART_COLORS = ['#00c9e8', '#0096d5', '#206bce', '#1d3ab2', '#6fb320', '#ffbe00', '#ff9000', '#d93e00', '#ac145a', '#914191',
  '#0375bf', '#4c9fd2', '#bfdcef', '#490094', '#9A72C4', '#C8B2DF', '#006ADE', '#6AB4FF',
  '#B5DAFF', '#014752', '#009293', '#73C3C4', '#4CEA7C', '#9DF4B7', '#C9F9D8',
  '#DD5400', '#EDA173', '#F5CDB4', '#940000', '#C47373', '#DFB2B2'];

export const DATE_FORMATS = [{
  label: 'Default',
  value: 'yyyy-MM-dd',
  momentValue: 'YYYY-MM-DD'
}, {
  label: 'September 1, 2017',
  value: 'longDate',
  momentValue: 'MMMM D, YYYY'
}, {
  label: '09/01/2017 (MM/DD/YYYY)',
  value: 'shortDate',
  momentValue: 'MM/DD/YYYY'
}, {
  label: '01/09/2017 (DD/MM/YYYY)',
  value: 'dd/MM/yyyy',
  momentValue: 'DD/MM/YYYY'
}, {
  label: 'September 2017',
  value: 'monthAndYear',
  momentValue: 'MMMM YYYY'
}, {
  label: 'September 1',
  value: 'monthAndDay',
  momentValue: 'MMMM D'
}];

export const DEFAULT_DATE_FORMAT = DATE_FORMATS[0];

export const DATE_FORMATS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(DATE_FORMATS);

export const CHART_DATE_FORMATS = [{
  value: 'MMMM d YYYY, h:mm:ss a',
  label: 'September 1st 2017, 1:28:31 pm'
}, {
  value: 'MMM d YYYY',
  label: ' Sep 1st 2017'
}, {
  value: 'MMM YYYY',
  label: 'September 2017'
}, {
  value: 'MM YYYY',
  label: '09 2017'
}, {
  value: 'YYYY',
  label: '2017'
}];

export const CHART_DEFAULT_DATE_FORMAT = CHART_DATE_FORMATS[1];

export const CHART_DATE_FORMATS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(CHART_DATE_FORMATS);

export const AGGREGATE_TYPES = [{
  label: 'Total',
  value: 'sum',
  icon: 'icon-Sum',
  valid: ['chart', 'pivot', 'report']
}, {
  label: 'Average',
  value: 'avg',
  icon: 'icon-AVG',
  valid: ['chart', 'pivot', 'report']
}, {
  label: 'Mininum',
  value: 'min',
  icon: 'icon-MIN',
  valid: ['chart', 'pivot', 'report']
}, {
  label: 'Maximum',
  value: 'max',
  icon: 'icon-MAX',
  valid: ['chart', 'pivot', 'report']
}, {
  label: 'Count',
  value: 'count',
  icon: 'icon-Count',
  type: 'long',
  valid: ['chart', 'pivot', 'report']
}, {
  label: 'Percentage',
  value: 'percentage',
  icon: 'icon-Percentage',
  type: 'float',
  valid: ['pivot', 'report']
}, {
  label: 'Percentage By Row',
  value: 'percentageByRow',
  icon: 'icon-Percentage',
  type: 'float',
  valid: ['chart']
}, {
  label: 'Percentage by Column',
  value: 'percentageByColumn',
  icon: 'icon-Percentage',
  type: 'float',
  valid: ['chart']
}];

export const AGGREGATE_STRING_TYPES = [{
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

export const SCHEDULE_TYPES = [{
  label: 'Immediate',
  value: 'immediate'
}, {
  label: 'Hourly',
  value: 'hourly'
}, {
  label: 'Daily',
  value: 'daily'
}, {
  label: 'Weekly',
  value: 'weeklybasis'
}, {
  label: 'Monthly',
  value: 'monthly'
}, {
  label: 'Yearly',
  value: 'yearly'
}];
