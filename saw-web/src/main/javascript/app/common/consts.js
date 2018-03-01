import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMapValues from 'lodash/fp/mapValues';

export const INT_TYPES = ['int', 'integer'];
export const FLOAT_TYPES = ['double', 'long', 'float'];
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
  momentValue: 'MMMM-D-YYYY'
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
