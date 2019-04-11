export const ALERT_AGGREGATIONS = [
  { name: 'Sum', value: 'sum' },
  { name: 'Average', value: 'avg' },
  { name: 'Minimum', value: 'min' },
  { name: 'Maximum', value: 'max' },
  { name: 'Count', value: 'count' },
  { name: 'Distinct Count', value: 'distinctCount' }
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
