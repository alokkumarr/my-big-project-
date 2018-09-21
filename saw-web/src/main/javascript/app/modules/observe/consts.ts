import * as fpPipe from 'lodash/fp/pipe';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpMapValues from 'lodash/fp/mapValues';

export const CUSTOM_DATE_PRESET_VALUE = 'NA';

export const DATE_PRESETS = [
  {
    value: 'TW',
    keyword: 'THIS_WEEK',
    label: 'This Week'
  },
  {
    value: 'Yesterday',
    keyword: 'YESTERDAY',
    label: 'Yesterday'
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
    value: 'LY',
    keyword: 'LAST_YEAR',
    label: 'Last Year'
  },
  {
    value: 'LW',
    keyword: 'LAST_WEEK',
    label: 'Last Week'
  },
  {
    value: 'LSW',
    keyword: 'LAST_SIX_WEEKS',
    label: 'Last 6 Weeks'
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
    value: CUSTOM_DATE_PRESET_VALUE,
    keyword: 'CUSTOM',
    label: 'Custom'
  }
];

export const DATE_PRESETS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(DATE_PRESETS);

export const DATE_FORMAT = {
  YYYY_MM_DD: 'YYYY-MM-DD',
  YYYY_MM_DD_HH_mm_ss: 'YYYY-MM-DD HH:mm:ss'
};

export const KPI_AGGREGATIONS = [
  { name: 'Sum', value: 'sum' },
  { name: 'Average', value: 'avg' },
  { name: 'Minimum', value: 'min' },
  { name: 'Maximum', value: 'max' },
  { name: 'Count', value: 'count' }
];

export const BULLET_CHART_OPTIONS = {
  chart: {
    type: 'bullet'
  }
};

export const BULLET_CHART_COLORS = [
  { value: 'rog', b1: '#C0463F', b2: '#EE754E', b3: '#99BA33' },
  { value: 'greys', b1: '#666', b2: '#999', b3: '#BBB' },
  { value: 'blues', b1: '#568ED5', b2: '#8DB4E3', b3: '#DDE6F3' }
];

export const KPI_BG_COLORS = [
  { label: 'green', value: '#67B829' },
  { label: 'purple', value: '#7C38BC' },
  { label: 'blue', value: '#0f61c8' },
  { label: 'orange', value: '#f7aa46' },
  { label: 'pink', value: '#fb6263' },
  { label: 'mint', value: '#00B19D' },
  { label: 'skyblue', value: '#40bcea' },
  { label: 'yellow', value: '#fcd036' },
  { label: 'red', value: '#d6080e' },
  { label: 'black', value: '#2c2e2f' },
  { label: 'maroon', value: '#cc3f44' }
];
