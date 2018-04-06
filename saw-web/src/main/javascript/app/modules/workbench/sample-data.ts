
export const TREE_VIEW_Data = [
  { id: 1, parentId: 0 },
  { id: 11, parentId: 1 },
  { id: 12, parentId: 1 },
  { id: 13, parentId: 1 },
  { id: 131, parentId: 13 },
  { id: 132, parentId: 13 },
  { id: 133, parentId: 13 },
  { id: 2, parentId: 0 }
];

export const RAW_SAMPLE = {
  'projectId': 'workbench',
  'path': 'test.csv',
  'data': [
    'DOJ,Name,ID,ZipCode,Salary,Resignation\r',
    '2018-01-27,Saurav Paul,213,20191,23.40,01-27-2018\r',
    '2018-01-27,Saurav Paul,213,20191,54.56,01-27-2018\r',
    '2018-01-27,Alexey Sorokin,215,20191,84.70,2018-Jan-31 15:08:00\r',
    ' 2015-07-15,Saurav Paul,213,20191,99.70,01-27-2018\r'
  ]
};

export const parser_preview = {
  'lineSeparator': '\n',
  'delimiter': ',',
  'quoteChar': '\'',
  'quoteEscapeChar': '\\',
  'headerSize': 1,
  'fieldNamesLine': 1,
  'rowsToInspect': 10000,
  'delimiterType': 'delimited',
  'description': 'It\'s delimited file inspecting to verify & understand the content of file',
  'fields': [
    {
      'name': 'DOJ',
      'type': 'date',
      'format': [
        'YYYY-MM-DD'
      ]
    },
    {
      'name': 'Name',
      'type': 'string'
    },
    {
      'name': 'ID',
      'type': 'long'
    },
    {
      'name': 'ZipCode',
      'type': 'long'
    },
    {
      'name': 'Salary',
      'type': 'double'
    },
    {
      'name': 'Resignation',
      'type': 'string',
      'format': [
        'YYYY-MMM-DD HH:mm:ss',
        'MM-DD-YYYY'
      ]
    }
  ],
  'info': {
    'totalLines': 5,
    'dataRows': 4,
    'maxFields': 6,
    'minFields': 6,
    'file': '/Users/spau0004/Desktop/test.csv'
  },
  'samplesParsed': [
    {
      'DOJ': '2018-01-27',
      'Name': 'Saurav Paul',
      'ID': '213',
      'ZipCode': '20191',
      'Salary': '54.56',
      'Resignation': '01-27-2018'
    },
    {
      'DOJ': '2018-01-27',
      'Name': 'Alexey Sorokin',
      'ID': '215',
      'ZipCode': '20191',
      'Salary': '84.70',
      'Resignation': 'Alexey has resigned & his last day was on 2018-Jan-31 15:08:00'
    }
  ]
};

export const ARTIFACT_SAMPLE = {
  'artifacts': [
    {
      'artifactName': 'MCT_DN_SESSION_SUMMARY',
      'columns': [
        {
          'name': 'TRANSFER_DATE ',
          'type': 'date'
        },
        {
          'name': 'TRANSFER_DATE_ID',
          'type': 'integer'
        },
        {
          'name': 'TRANSFER_MONTH_ID',
          'type': 'integer'
        },
        {
          'name': 'OPCO',
          'type': 'string'
        }
      ]
    }
  ]
};

export const SQLEXEC_SAMPLE = [
  {
    'SEL_MB': 433670.01326084137,
    'MONTH_YEAR': 'Aug-17',
    'FAILED_ITEMS': 60904,
    'FAILED_MB': 145021.8522052765,
    'AVAIL_MB': 621064.1080217361,
    'TARGET_MODEL': 'LGMP260',
    'XFER_ITEMS': 621830,
    'XFER_MB': 288465.82830142975,
    'SEL_ITEMS': 685102,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 934445
  },
  {
    'AVAIL_MB': 251823.43658542633,
    'XFER_MB': 67241.89413356781,
    'SEL_ITEMS': 153029,
    'MONTH_YEAR': 'Aug-17',
    'FAILED_ITEMS': 10851,
    'FAILED_MB': 30701.61478805542,
    'TARGET_MODEL': 'SM-J727T1',
    'SEL_MB': 152983.2884979248,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 217587,
    'XFER_ITEMS': 133955
  },
  {
    'XFER_ITEMS': 754,
    'AVAIL_MB': 6519.984823226929,
    'FAILED_MB': 0,
    'MONTH_YEAR': 'Oct-17',
    'SEL_MB': 1808.7185382843018,
    'TARGET_MODEL': '5049S',
    'XFER_MB': 1808.7185382843018,
    'SEL_ITEMS': 754,
    'sum(AVAILABLE_ITEMS)': 14447,
    'TARGET_OS': 'android',
    'FAILED_ITEMS': 0
  }
];

export const SQL_AQCTIONS = [
  { statement: 'SELECT * FROM table_name;'},
  {statement: 'SELECT column1, column2 FROM table_name;'},
  { statement: 'SELECT CustomerName, City FROM Customers;' },
  { statement: 'SELECT Count(*) AS DistinctCountrie FROM (SELECT DISTINCT Country FROM Customers);' }
]
