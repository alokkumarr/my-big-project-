export const DATASETS = [
  {
    '_id': 'xda-ux-sr-comp-dev::TRTEST_JEXLREF_SS',
    'system': {
      'user': 'A_user',
      'project': 'xda-ux-sr-comp-dev',
      'type': 'fs',
      'format': 'parquet',
      'name': 'TRTEST_JEXLREF_SS',
      'physicalLocation': 'data',
      'catalog': 'dout',
      'numberOfFiles': '1'
    },
    'userData': {
      'createdBy': 'S.Ryabov',
      'category': 'subcat1',
      'description': 'Transformer component test case: transformed records'
    },
    'asOfNow': {
      'status': 'SUCCESS',
      'started': '20180209-195737',
      'finished': '20180209-195822',
      'aleId': 'xda-ux-sr-comp-dev::1518206302595',
      'batchId': 'BJEXLREFSS'
    },
    'dataPods': {
      'numberOfPods': '3',
      'list': [
        'name-dp1-12342',
        'name-dp2-122342',
        'name-dp3-12344232'
      ]
    },
    'artifact': {
      'artifactName': 'MCT_DN_SESSION_SUMMARY',
      'columns': [
        {
          'name': 'TRANSFER_DATE',
          'type': 'date'
        },
        {
          'name': 'TRANSFER_DATE_ID',
          'type': 'integer'
        },
        {
          'name': 'TRANSFER_MONTH_ID',
          'type': 'integer'
        }
      ]
    },
    'asOutput': 'xda-ux-sr-comp-dev::transformer::165407713',
    'transformations': []
  },
  {
    '_id': 'xda-ux-sr-comp-dev::TRTEST_JKWNDLW',
    'system': {
      'user': 'B_user',
      'project': 'xda-ux-sr-comp-prod',
      'type': 'fs',
      'format': 'parquet',
      'name': 'SNCR_data',
      'physicalLocation': 'data',
      'catalog': 'dout',
      'numberOfFiles': '3'
    },
    'userData': {
      'createdBy': 'Admin',
      'category': 'subcat1',
      'description': 'PROD mode dataset. Just testing'
    },
    'asOfNow': {
      'status': 'SUCCESS',
      'started': '20180209-195737',
      'finished': '20180209-195822',
      'aleId': 'xda-ux-sr-comp-dev::1518206302595',
      'batchId': 'BJEXLREFSS'
    },
    'dataPods': {
      'numberOfPods': '3',
      'list': [
        'name-dp1-678',
        'name-dp2-6789',
        'name-dp3-9023'
      ]
    },
    'artifact': {
      'artifactName': 'MCT_DN_SESSION_SUMMARY',
      'columns': [
        {
          'name': 'TRANSFER_DATE',
          'type': 'date'
        },
        {
          'name': 'TRANSFER_DATE_ID',
          'type': 'integer'
        },
        {
          'name': 'TRANSFER_MONTH_ID',
          'type': 'integer'
        }
      ]
    },
    'asOutput': 'xda-ux-sr-comp-dev::transformer::34239923',
    'transformations': []
  }
]
;


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
    },
    {
      'DOJ': 'Saurav has joined SNCR on 2015-07-15',
      'Name': 'Saurav Paul',
      'ID': '213',
      'ZipCode': '20191',
      'Salary': '99.70',
      'Resignation': '01-27-2018'
    },
    {
      'DOJ': '2018-01-27',
      'Name': 'Saurav Paul',
      'ID': '213',
      'ZipCode': '20191',
      'Salary': '23.40',
      'Resignation': '01-27-2018'
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
        },
        {
          'name': 'SESSION_ID',
          'type': 'string'
        },
        {
          'name': 'SESSION_STATUS',
          'type': 'string'
        },
        {
          'name': 'SESSION_COUNT',
          'type': 'long'
        },
        {
          'name': 'QUARATINE_CODE',
          'type': 'string'
        },
        {
          'name': 'TARGET_OS',
          'type': 'string'
        },
        {
          'name': 'TARGET_OS_VERSION',
          'type': 'string'
        },
        {
          'name': 'TARGET_APP_VERSION',
          'type': 'string'
        },
        {
          'name': 'TARGET_INTERNALVERSION',
          'type': 'string'
        },
        {
          'name': 'TARGET_MANUFACTURER',
          'type': 'string'
        },
        {
          'name': 'TARGET_MODEL',
          'type': 'string'
        },
        {
          'name': 'IN_STORE_TRANSFER',
          'type': 'string'
        },
        {
          'name': 'SSID',
          'type': 'string'
        },
        {
          'name': 'MSISDN',
          'type': 'string'
        },
        {
          'name': 'IMEI',
          'type': 'string'
        },
        {
          'name': 'ICCID',
          'type': 'string'
        },
        {
          'name': 'IMSI',
          'type': 'string'
        },
        {
          'name': 'AVAILABLE_ITEMS',
          'type': 'long'
        },
        {
          'name': 'AVAILABLE_BYTES',
          'type': 'double'
        },
        {
          'name': 'AVAILABLE_MB',
          'type': 'long'
        },
        {
          'name': 'TRANSFER_ITEMS',
          'type': 'long'
        },
        {
          'name': 'TRANSFER_BYTES',
          'type': 'double'
        },
        {
          'name': 'TRANSFER_MB',
          'type': 'double'
        },
        {
          'name': 'SELECTED_BYTES',
          'type': 'long'
        },
        {
          'name': 'SESSION_SELECTED_MB ',
          'type': 'double'
        },
        {
          'name': 'SELECTED_ITEMS',
          'type': 'long'
        },
        {
          'name': 'FAILED_ITEMS',
          'type': 'long'
        },
        {
          'name': 'FAILED_BYTES',
          'type': 'long'
        },
        {
          'name': 'FAILED_MB',
          'type': 'double'
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
  },
  {
    'TARGET_MODEL': '0PCV1',
    'sum(AVAILABLE_ITEMS)': 8,
    'AVAIL_MB': 0.20462799072265625,
    'XFER_ITEMS': 7,
    'SEL_MB': 0.20444869995117188,
    'MONTH_YEAR': 'Sep-17',
    'SEL_ITEMS': 8,
    'XFER_MB': 0.13156414031982422,
    'TARGET_OS': 'android',
    'FAILED_MB': 0.07288455963134766,
    'FAILED_ITEMS': 1
  },
  {
    'TARGET_MODEL': 'SGH-T699',
    'SEL_MB': 158.44641590118408,
    'FAILED_MB': 0,
    'XFER_MB': 158.44641590118408,
    'AVAIL_MB': 158.44648838043213,
    'MONTH_YEAR': 'Sep-17',
    'XFER_ITEMS': 4252,
    'SEL_ITEMS': 4252,
    'sum(AVAILABLE_ITEMS)': 4252,
    'TARGET_OS': 'android',
    'FAILED_ITEMS': 0
  },
  {
    'SEL_MB': 206.6791353225708,
    'XFER_MB': 206.6791353225708,
    'FAILED_MB': 0,
    'MONTH_YEAR': 'Aug-17',
    'AVAIL_MB': 206.6791353225708,
    'TARGET_MODEL': 'BLU STUDIO G',
    'XFER_ITEMS': 821,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 821,
    'SEL_ITEMS': 821,
    'FAILED_ITEMS': 0
  },
  {
    'SEL_MB': 235.54494953155518,
    'FAILED_MB': 0,
    'SEL_ITEMS': 7918,
    'MONTH_YEAR': 'Sep-17',
    'sum(AVAILABLE_ITEMS)': 8070,
    'AVAIL_MB': 415.9212694168091,
    'XFER_MB': 235.54494953155518,
    'XFER_ITEMS': 7917,
    'TARGET_OS': 'android',
    'TARGET_MODEL': 'SM-S550TL',
    'FAILED_ITEMS': 1
  },
  {
    'SEL_ITEMS': 18381,
    'SEL_MB': 15052.330384254456,
    'XFER_ITEMS': 18379,
    'MONTH_YEAR': 'Oct-17',
    'FAILED_ITEMS': 6,
    'XFER_MB': 15052.330384254456,
    'FAILED_MB': 0.42267799377441406,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 60843,
    'TARGET_MODEL': 'SAMSUNG-SM-G890A',
    'AVAIL_MB': 24623.086645126343
  },
  {
    'XFER_ITEMS': 17409,
    'MONTH_YEAR': 'Oct-17',
    'FAILED_MB': 3153.8832387924194,
    'sum(AVAILABLE_ITEMS)': 18303,
    'TARGET_MODEL': 'SAMSUNG-SM-J727AZ',
    'AVAIL_MB': 10865.783199310303,
    'SEL_MB': 5872.0199546813965,
    'SEL_ITEMS': 18156,
    'TARGET_OS': 'android',
    'XFER_MB': 2718.136715888977,
    'FAILED_ITEMS': 747
  },
  {
    'SEL_ITEMS': 3619,
    'XFER_MB': 418.99408054351807,
    'AVAIL_MB': 13875.930421829224,
    'FAILED_MB': 6521.453552246094,
    'SEL_MB': 6940.447632789612,
    'MONTH_YEAR': 'Nov-17',
    'sum(AVAILABLE_ITEMS)': 4650,
    'FAILED_ITEMS': 736,
    'TARGET_MODEL': 'G8142',
    'TARGET_OS': 'android',
    'XFER_ITEMS': 2883
  },
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
  },
  {
    'TARGET_MODEL': '0PCV1',
    'sum(AVAILABLE_ITEMS)': 8,
    'AVAIL_MB': 0.20462799072265625,
    'XFER_ITEMS': 7,
    'SEL_MB': 0.20444869995117188,
    'MONTH_YEAR': 'Sep-17',
    'SEL_ITEMS': 8,
    'XFER_MB': 0.13156414031982422,
    'TARGET_OS': 'android',
    'FAILED_MB': 0.07288455963134766,
    'FAILED_ITEMS': 1
  },
  {
    'TARGET_MODEL': 'SGH-T699',
    'SEL_MB': 158.44641590118408,
    'FAILED_MB': 0,
    'XFER_MB': 158.44641590118408,
    'AVAIL_MB': 158.44648838043213,
    'MONTH_YEAR': 'Sep-17',
    'XFER_ITEMS': 4252,
    'SEL_ITEMS': 4252,
    'sum(AVAILABLE_ITEMS)': 4252,
    'TARGET_OS': 'android',
    'FAILED_ITEMS': 0
  },
  {
    'SEL_MB': 206.6791353225708,
    'XFER_MB': 206.6791353225708,
    'FAILED_MB': 0,
    'MONTH_YEAR': 'Aug-17',
    'AVAIL_MB': 206.6791353225708,
    'TARGET_MODEL': 'BLU STUDIO G',
    'XFER_ITEMS': 821,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 821,
    'SEL_ITEMS': 821,
    'FAILED_ITEMS': 0
  },
  {
    'SEL_MB': 235.54494953155518,
    'FAILED_MB': 0,
    'SEL_ITEMS': 7918,
    'MONTH_YEAR': 'Sep-17',
    'sum(AVAILABLE_ITEMS)': 8070,
    'AVAIL_MB': 415.9212694168091,
    'XFER_MB': 235.54494953155518,
    'XFER_ITEMS': 7917,
    'TARGET_OS': 'android',
    'TARGET_MODEL': 'SM-S550TL',
    'FAILED_ITEMS': 1
  },
  {
    'SEL_ITEMS': 18381,
    'SEL_MB': 15052.330384254456,
    'XFER_ITEMS': 18379,
    'MONTH_YEAR': 'Oct-17',
    'FAILED_ITEMS': 6,
    'XFER_MB': 15052.330384254456,
    'FAILED_MB': 0.42267799377441406,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 60843,
    'TARGET_MODEL': 'SAMSUNG-SM-G890A',
    'AVAIL_MB': 24623.086645126343
  },
  {
    'XFER_ITEMS': 17409,
    'MONTH_YEAR': 'Oct-17',
    'FAILED_MB': 3153.8832387924194,
    'sum(AVAILABLE_ITEMS)': 18303,
    'TARGET_MODEL': 'SAMSUNG-SM-J727AZ',
    'AVAIL_MB': 10865.783199310303,
    'SEL_MB': 5872.0199546813965,
    'SEL_ITEMS': 18156,
    'TARGET_OS': 'android',
    'XFER_MB': 2718.136715888977,
    'FAILED_ITEMS': 747
  },
  {
    'SEL_ITEMS': 3619,
    'XFER_MB': 418.99408054351807,
    'AVAIL_MB': 13875.930421829224,
    'FAILED_MB': 6521.453552246094,
    'SEL_MB': 6940.447632789612,
    'MONTH_YEAR': 'Nov-17',
    'sum(AVAILABLE_ITEMS)': 4650,
    'FAILED_ITEMS': 736,
    'TARGET_MODEL': 'G8142',
    'TARGET_OS': 'android',
    'XFER_ITEMS': 2883
  },
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
  },
  {
    'TARGET_MODEL': '0PCV1',
    'sum(AVAILABLE_ITEMS)': 8,
    'AVAIL_MB': 0.20462799072265625,
    'XFER_ITEMS': 7,
    'SEL_MB': 0.20444869995117188,
    'MONTH_YEAR': 'Sep-17',
    'SEL_ITEMS': 8,
    'XFER_MB': 0.13156414031982422,
    'TARGET_OS': 'android',
    'FAILED_MB': 0.07288455963134766,
    'FAILED_ITEMS': 1
  },
  {
    'TARGET_MODEL': 'SGH-T699',
    'SEL_MB': 158.44641590118408,
    'FAILED_MB': 0,
    'XFER_MB': 158.44641590118408,
    'AVAIL_MB': 158.44648838043213,
    'MONTH_YEAR': 'Sep-17',
    'XFER_ITEMS': 4252,
    'SEL_ITEMS': 4252,
    'sum(AVAILABLE_ITEMS)': 4252,
    'TARGET_OS': 'android',
    'FAILED_ITEMS': 0
  },
  {
    'SEL_MB': 206.6791353225708,
    'XFER_MB': 206.6791353225708,
    'FAILED_MB': 0,
    'MONTH_YEAR': 'Aug-17',
    'AVAIL_MB': 206.6791353225708,
    'TARGET_MODEL': 'BLU STUDIO G',
    'XFER_ITEMS': 821,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 821,
    'SEL_ITEMS': 821,
    'FAILED_ITEMS': 0
  },
  {
    'SEL_MB': 235.54494953155518,
    'FAILED_MB': 0,
    'SEL_ITEMS': 7918,
    'MONTH_YEAR': 'Sep-17',
    'sum(AVAILABLE_ITEMS)': 8070,
    'AVAIL_MB': 415.9212694168091,
    'XFER_MB': 235.54494953155518,
    'XFER_ITEMS': 7917,
    'TARGET_OS': 'android',
    'TARGET_MODEL': 'SM-S550TL',
    'FAILED_ITEMS': 1
  },
  {
    'SEL_ITEMS': 18381,
    'SEL_MB': 15052.330384254456,
    'XFER_ITEMS': 18379,
    'MONTH_YEAR': 'Oct-17',
    'FAILED_ITEMS': 6,
    'XFER_MB': 15052.330384254456,
    'FAILED_MB': 0.42267799377441406,
    'TARGET_OS': 'android',
    'sum(AVAILABLE_ITEMS)': 60843,
    'TARGET_MODEL': 'SAMSUNG-SM-G890A',
    'AVAIL_MB': 24623.086645126343
  },
  {
    'XFER_ITEMS': 17409,
    'MONTH_YEAR': 'Oct-17',
    'FAILED_MB': 3153.8832387924194,
    'sum(AVAILABLE_ITEMS)': 18303,
    'TARGET_MODEL': 'SAMSUNG-SM-J727AZ',
    'AVAIL_MB': 10865.783199310303,
    'SEL_MB': 5872.0199546813965,
    'SEL_ITEMS': 18156,
    'TARGET_OS': 'android',
    'XFER_MB': 2718.136715888977,
    'FAILED_ITEMS': 747
  },
  {
    'SEL_ITEMS': 3619,
    'XFER_MB': 418.99408054351807,
    'AVAIL_MB': 13875.930421829224,
    'FAILED_MB': 6521.453552246094,
    'SEL_MB': 6940.447632789612,
    'MONTH_YEAR': 'Nov-17',
    'sum(AVAILABLE_ITEMS)': 4650,
    'FAILED_ITEMS': 736,
    'TARGET_MODEL': 'G8142',
    'TARGET_OS': 'android',
    'XFER_ITEMS': 2883
  }
];

export const SQL_AQCTIONS = [
  { statement: 'SELECT * FROM table_name;'},
  {statement: 'SELECT column1, column2 FROM table_name;'},
  { statement: 'SELECT CustomerName, City FROM Customers;' },
  { statement: 'SELECT Count(*) AS DistinctCountrie FROM (SELECT DISTINCT Country FROM Customers);' }
]
