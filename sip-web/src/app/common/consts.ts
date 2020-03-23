import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as map from 'lodash/map';

export const USER_ANALYSIS_CATEGORY_NAME = 'My Analysis';
export const USER_ANALYSIS_SUBCATEGORY_NAME = 'DRAFTS';

export const TABLE_CUSTCODE_COLUMNNAME = 'customerCode';

export const INT_TYPES = ['int', 'integer', 'long'];
export const FLOAT_TYPES = ['double', 'float'];
export const DATE_TYPES = ['timestamp', 'date'];
export const STRING_TYPES = ['string'];
export const NUMBER_TYPES = [...INT_TYPES, ...FLOAT_TYPES];
export const ALL_TYPES = [...NUMBER_TYPES, ...DATE_TYPES, ...STRING_TYPES];
export const DEFAULT_PRECISION = 2;
export const EMAIL_REGEX = /^[_a-zA-Z0-9]+(\.[_a-zA-Z0-9]+)*@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*(\.[a-zA-Z]{2,4})$/;
const GEO_TYPES_WITH_IDENTIFIER = {
  state: ['name', 'postal-code'],
  country: ['name', 'fips'],
  lngLat: [''],
  zip: []
};
export const GEO_TYPES = fpPipe(
  fpToPairs,
  fpFlatMap(([geoType, identifiers]) =>
    map(identifiers, identifier => {
      if (!identifier) {
        return geoType;
      }
      return `${geoType}:${identifier}`;
    })
  )
)(GEO_TYPES_WITH_IDENTIFIER);

export const BACKEND_TIMEZONE = 'America/New_York';

export const SYSTEM_CATEGORY_OPERATIONS = ['Delete', 'Publish'];

export const DATE_INTERVALS = [
  {
    label: 'All',
    value: 'all',
    formatForBackEnd: 'yyyy-MM-dd'
  },
  {
    label: 'Year',
    value: 'year',
    format: 'yyyy',
    momentFormat: 'yyyy',
    formatForBackEnd: 'yyyy'
  },
  {
    label: 'Quarter',
    value: 'quarter',
    format: 'yyyy-QQQ',
    momentFormat: 'YYYY-[Q]Q',
    formatForBackEnd: 'yyyy-MM' // the backend can't handle quarters,
    // so we'll send month foramt, and convert to quarter on front end
  },
  {
    label: 'Month',
    value: 'month',
    format: 'yyyy-MM',
    momentFormat: 'YYYY-MM',
    formatForBackEnd: 'yyyy-MM'
  },
  {
    label: 'Date',
    value: 'day',
    format: 'yyyy-MM-dd',
    momentFormat: 'YYYY-MM-DD',
    formatForBackEnd: 'yyyy-MM-dd'
  }
];

export const DEFAULT_DATE_INTERVAL = DATE_INTERVALS[4];
export const DATE_INTERVALS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(DATE_INTERVALS);

export const COMPARISON_CHART_DATE_INTERVALS = [
  DATE_INTERVALS[2],
  DATE_INTERVALS[3]
];

export const COMPARISON_CHART_DATE_INTERVALS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(COMPARISON_CHART_DATE_INTERVALS);

export const CHART_COLORS = [
  '#00c9e8',
  '#0096d5',
  '#206bce',
  '#1d3ab2',
  '#6fb320',
  '#ffbe00',
  '#ff9000',
  '#d93e00',
  '#ac145a',
  '#914191',
  '#0375bf',
  '#4c9fd2',
  '#bfdcef',
  '#490094',
  '#9A72C4',
  '#C8B2DF',
  '#006ADE',
  '#6AB4FF',
  '#B5DAFF',
  '#014752',
  '#009293',
  '#73C3C4',
  '#4CEA7C',
  '#9DF4B7',
  '#C9F9D8',
  '#DD5400',
  '#EDA173',
  '#F5CDB4',
  '#940000',
  '#C47373',
  '#DFB2B2'
];

export const DATE_FORMATS = [
  {
    value: 'YYYY',
    momentValue: 'YYYY',
    groupInterval: 'year',
    label: '2017'
  },
  {
    value: 'MMM YYYY',
    momentValue: 'MMM YYYY',
    groupInterval: 'month',
    label: 'September 2017'
  },
  {
    value: 'MM YYYY',
    momentValue: 'MM YYYY',
    groupInterval: 'month',
    label: '09 2017'
  },
  {
    label: '2017-11',
    value: 'yyyy-MM',
    momentValue: 'YYYY-MM',
    groupInterval: 'month'
  },
  {
    label: 'September 1',
    value: 'MMMM d',
    momentValue: 'MMMM D',
    groupInterval: 'month'
  },
  {
    label: '09-2017',
    value: 'MM-yyyy',
    groupInterval: 'month',
    momentValue: 'MM-YYYY'
  },
  {
    label: 'Sept-2017',
    value: 'MMM-yyyy',
    groupInterval: 'month',
    momentValue: 'MMM-YYYY'
  },
  {
    label: 'Sept-17 (Month-year)',
    value: 'MMM-yy',
    groupInterval: 'month',
    momentValue: 'MMM-YY'
  },
  {
    value: 'MMM d YYYY',
    momentValue: 'MMM Do YYYY',
    groupInterval: 'day',
    label: ' Sep 1st 2017'
  },
  {
    label: '09/15/17',
    value: 'MM/dd/yy',
    groupInterval: 'day',
    momentValue: 'MM/DD/YY'
  },
  {
    label: '15/09/17',
    value: 'dd/MM/yy',
    groupInterval: 'day',
    momentValue: 'DD/MM/YY'
  },
  {
    label: '2017-11-23',
    value: 'yyyy-MM-dd',
    momentValue: 'YYYY-MM-DD',
    groupInterval: 'day'
  },
  {
    label: 'September-15',
    value: 'MMMM-dd',
    groupInterval: 'day',
    momentValue: 'MMMM-DD'
  },
  {
    label: 'Sept-15 (Month-day)',
    value: 'MMM-dd',
    groupInterval: 'day',
    momentValue: 'MMM-DD'
  },
  {
    label: 'September 1, 2017',
    value: 'MMMM d, yyyy',
    momentValue: 'MMMM D, YYYY',
    groupInterval: 'day'
  },
  {
    label: '09/01/2017 (MM/DD/YYYY)',
    value: 'MM/dd/yyyy',
    momentValue: 'MM/DD/YYYY',
    groupInterval: 'day'
  },
  {
    label: '01/09/2017 (DD/MM/YYYY)',
    value: 'dd/MM/yyyy',
    momentValue: 'DD/MM/YYYY',
    groupInterval: 'day'
  },
  {
    value: 'MMMM d YYYY, h:mm:ss a',
    momentValue: 'MMMM Do YYYY, h:mm:ss a',
    groupInterval: 'hour',
    label: 'September 1st 2017, 1:28:31 pm'
  },
  {
    label: '09/15/17 11:20',
    value: 'MM/dd/yyyy hh:mm',
    groupInterval: 'minute',
    momentValue: 'MM/DD/YYYY HH:mm'
  },
  {
    label: '09/15 11:20',
    value: 'MM/dd HH:mm',
    groupInterval: 'minute',
    momentValue: 'MM/DD HH:mm'
  },
  {
    label: '09/01/2017 11:20:36',
    value: 'MM/dd/yyyy HH:mm:ss',
    momentValue: 'MM/DD/YYYY HH:mm:ss',
    groupInterval: 'second'
  },
  {
    label: '09/15/17 11:20:36',
    value: 'MM/dd/yyyy hh:mm:ss',
    groupInterval: 'second',
    momentValue: 'MM/DD/YYYY HH:mm:ss'
  }
];

export const DATE_FORMATS_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(DATE_FORMATS);

export const DEFAULT_DATE_FORMAT = DATE_FORMATS[11];

export const CUSTOM_HEADERS = {
  SKIP_TOAST: 'SIP-Skip-Error-Toast'
};

export const AGGREGATE_TYPES = [
  {
    label: 'Sum',
    designerLabel: 'SUM',
    value: 'sum',
    icon: 'icon-Sum',
    valid: ['chart', 'pivot', 'report', 'esReport', 'map'],
    validDataType: [...NUMBER_TYPES]
  },
  {
    label: 'Average',
    designerLabel: 'AVG',
    value: 'avg',
    icon: 'icon-ic-average',
    valid: ['chart', 'pivot', 'report', 'esReport', 'map'],
    validDataType: [...NUMBER_TYPES]
  },
  {
    label: 'Mininum',
    designerLabel: 'MIN',
    value: 'min',
    icon: 'icon-ic-min',
    valid: ['chart', 'pivot', 'report', 'esReport', 'map'],
    validDataType: [...NUMBER_TYPES]
  },
  {
    label: 'Maximum',
    designerLabel: 'MAX',
    value: 'max',
    icon: 'icon-ic-max',
    valid: ['chart', 'pivot', 'report', 'esReport', 'map'],
    validDataType: [...NUMBER_TYPES]
  },
  {
    label: 'Count',
    designerLabel: 'CNT',
    value: 'count',
    icon: 'icon-Count',
    type: 'long',
    valid: ['chart', 'pivot', 'report', 'esReport', 'map'],
    validDataType: [...ALL_TYPES]
  },
  {
    label: 'Distinct Count',
    designerLabel: 'CNTD',
    value: 'distinctcount',
    icon: 'icon-Count',
    type: 'long',
    valid: ['chart', 'pivot', 'report', 'esReport', 'map'],
    validDataType: [...ALL_TYPES]
  },
  {
    label: 'Percentage',
    designerLabel: 'PCTC',
    value: 'percentage',
    icon: 'icon-Percentage',
    type: 'float',
    valid: ['pivot', 'map', 'report', 'esReport'],
    validDataType: [...NUMBER_TYPES]
  },
  {
    label: 'Percentage by Column',
    designerLabel: 'PCTC',
    value: 'percentage',
    icon: 'icon-Percentage',
    type: 'float',
    valid: ['chart'],
    validDataType: [...NUMBER_TYPES]
  },
  {
    label: 'Percentage By Row',
    designerLabel: 'PCTR',
    value: 'percentagebyrow',
    icon: 'icon-Percentage',
    type: 'float',
    valid: ['chart'],
    validDataType: [...NUMBER_TYPES]
  }
  // {
  //   label: 'Median',
  //   designerLabel: 'MEDIAN',
  //   value: 'median',
  //   icon: 'icon-Percentage',
  //   type: 'float',
  //   valid: ['chart']
  // },
  // {
  //   label: 'Std.Deviation',
  //   designerLabel: 'STDEV',
  //   value: 'stdev',
  //   icon: 'icon-Percentage',
  //   type: 'float',
  //   valid: ['chart']
  // },
  // {
  //   label: 'Varience',
  //   designerLabel: 'VARI',
  //   value: 'varience',
  //   icon: 'icon-Percentage',
  //   type: 'float',
  //   valid: ['chart']
  // }
];

export const filterAggregatesByAnalysisType = (
  analysisType: string,
  aggregates = AGGREGATE_TYPES
) => aggregates.filter(aggregate => aggregate.valid.includes(analysisType));

export const filterAggregatesByDataType = (
  dataType: string,
  aggregates = AGGREGATE_TYPES
) => aggregates.filter(aggregate => aggregate.validDataType.includes(dataType));

export const DEFAULT_AGGREGATE_TYPE = AGGREGATE_TYPES[0];

export const AGGREGATE_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AGGREGATE_TYPES);

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

export const SCHEDULE_TYPES = [
  {
    label: 'Immediate',
    value: 'immediate'
  },
  {
    label: 'Hourly',
    value: 'hourly'
  },
  {
    label: 'Daily',
    value: 'daily'
  },
  {
    label: 'Weekly',
    value: 'weeklybasis'
  },
  {
    label: 'Monthly',
    value: 'monthly'
  },
  {
    label: 'Yearly',
    value: 'yearly'
  }
];

export const DATAPOD_CATEGORIES = [
  {
    name: 'Default',
    icon: 'category-default'
  },
  {
    name: 'Errors',
    icon: 'category-errors'
  },
  {
    name: 'Orders',
    icon: 'category-orders'
  },
  {
    name: 'Sessions',
    icon: 'category-sessions'
  },
  {
    name: 'Subscribers',
    icon: 'category-subscribers'
  },
  {
    name: 'Usage',
    icon: 'category-usage'
  },
  {
    name: 'Events',
    icon: 'calendar-events'
  },
  {
    name: 'Retention',
    icon: 'calendar-retention'
  },
  {
    name: 'Funnel',
    icon: 'calendar-funnel'
  }
];

export const DATAPOD_CATEGORIES_OBJ = fpPipe(
  fpGroupBy('name'),
  fpMapValues(v => v[0])
)(DATAPOD_CATEGORIES);

export const PRODUCT_MODULE_MOCK_MENU = {
  prodCode: 'SAWD0000012131',
  productModName: 'INSIGHTS',
  productModDesc: 'Insights Module',
  productModCode: 'INSIGH00001',
  productModID: '1324244',
  moduleURL: 'http://localhost:4200/assets/insights.umd.js',
  defaultMod: '1',
  privilegeCode: 128,
  prodModFeature: [
    {
      prodModFeatureName: 'SubModules',
      prodModCode: 'INSIGH00001',
      productModuleSubFeatures: [
        {
          prodModFeatureName: 'IOT',
          prodModFeatureDesc: 'Iot',
          defaultURL: 'iot',
          prodModFeatureID: 'iot',
          prodModFeatrCode: 'iot',
          prodModCode: 'INSIGH00001',
          roleId: 1
        },
        {
          prodModFeatureName: 'REVIEW',
          prodModFeatureDesc: 'Review',
          defaultURL: 'review',
          prodModFeatureID: 'review',
          prodModFeatrCode: 'review',
          roleId: 1
        }
      ]
    }
  ]
};

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

export const DEFAULT_BRANDING_COLOR = '#0077be';
export const DATASET_CATEGORIES_TYPE = [
  {
    value: 'base',
    displayName: 'Base'
  },
  {
    value: 'partition',
    displayName: 'Partition'
  },
  {
    value: 'Raw Data Set',
    displayName: 'Raw Data Set'
  },
  {
    value: 'Enriched Data Set',
    displayName: 'Enriched Data Set'
  },
  {
    value: 'Aggregated Data Set',
    displayName: 'Aggregated Data Set'
  }
];

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

export const LESS_THAN_A_WEEK_DATE_PRESETS = ['Yesterday', 'Today', 'TW', 'LW'];
