export const CSV_CONFIG = {
  file: '',
  lineSeparator: '\n',
  delimiter: ',',
  quoteChar: '',
  quoteEscapeChar: '\\',
  headerSize: 1,
  fieldNamesLine: '',
  dateFormats: [],
  rowsToInspect: 100,
  delimiterType: 'delimited',
  header: 'yes'
};

export const PARSER_CONFIG = {
  parser: {
    fields: [],
    file: '',
    lineSeparator: '\\n',
    delimiter: ',',
    quoteChar: '',
    quoteEscape: '\\',
    headerSize: 1,
    fieldNameLine: ''
  },
  outputs: [
    {
      dataSet: 'test'
    }
  ],
  parameters: [
    {
      name: 'spark.master',
      value: 'spark://foobar'
    }
  ]
  // outputs: [{
  //   dataSet: '',
  //   mode: 'replace', // append
  //   description: ''
  // }],
  // parameters: [{
  //   name: 'spark.master',
  //   value: 'yarn'
  // }, {
  //   name: 'spark.executor.instances',
  //   value: '6'
  // }]
};

export const STAGING_TREE = [
  { name: 'Staging', size: 0, isDirectory: true, path: 'root' }
];

export const SEMANTIC_TEMPLATE = {
  action: {
    'base-relation': [
      {
        id: 'replace_baseID',
        'node-category': 'DataObject'
      }
    ],
    content: {
      artifacts: [
        {
          artifactName: '',
          columns: []
        }
      ],
      checked: false,
      customerCode: 'SYNCHRONOSS',
      dataSecurityKey: '',
      disabled: false,
      esRepository: {
        indexName: '',
        storageType: 'ES',
        type: ''
      },
      id: '',
      metric: '',
      metricName: '',
      module: 'ANALYZE',
      supports: [
        {
          category: 'table',
          children: [],
          label: 'tables'
        },
        {
          category: 'charts',
          children: [],
          label: 'charts'
        }
      ],
      type: 'semantic'
    },
    verb: 'create'
  },
  'node-category': 'SemanticNode'
};
export const DATA_NODE_TEMPLATE = {
  action: {
    'dl-locations': [],
    content: {
      description: '',
      displayName: '',
      name: '',
      partitionType: '',
      product: '',
      storageType: 'ES',
      type: 'ES'
    },
    schema: {
      fields: [],
      type: 'struct'
    },
    verb: 'create'
  },
  'node-category': 'DataObject'
};

export const TYPE_CONVERSION = {
  text: 'string',
  string: 'string',
  double: 'double',
  integer: 'integer',
  date: 'date',
  float: 'float',
  keyword: 'string',
  long: 'long',
  decimal: 'double',
  timestamp: 'date'
};