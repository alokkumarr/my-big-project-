export const ALERT_AGGREGATIONS = [
  { name: 'Sum', value: 'SUM' },
  { name: 'Average', value: 'AVG' },
  { name: 'Minimum', value: 'MIN' },
  { name: 'Maximum', value: 'MAX' },
  { name: 'Count', value: 'COUNT' },
  { name: 'Percentage', value: 'PERCENTAGE' }
];

export const ALERT_SEVERITY = [
  { name: 'CRITICAL', value: 'CRITICAL' },
  { name: 'MEDIUM', value: 'MEDIUM' },
  { name: 'LOW', value: 'LOW' }
];

export const ALERT_STATUS = [
  { name: 'Active', value: true },
  { name: 'Inactive', value: false }
];

export const ALERT_OPERATORS = [
  {
    value: 'GT',
    name: 'Greater than'
  },
  {
    value: 'LT',
    name: 'Less than'
  },
  {
    value: 'GTE',
    name: 'Greater than or equal to'
  },
  {
    value: 'LTE',
    name: 'Less than or equal to'
  },
  {
    value: 'EQ',
    name: 'Equal to'
  },
  {
    value: 'NEQ',
    name: 'Not equal to'
  }
];
