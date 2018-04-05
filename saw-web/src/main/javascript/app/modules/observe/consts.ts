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
    value: 'YESTERDAY',
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
