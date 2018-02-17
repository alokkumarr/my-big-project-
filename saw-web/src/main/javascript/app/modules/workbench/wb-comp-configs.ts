export const CSV_CONFIG = {
  csvInspector: {
    file: '',
    lineSeparator: '\n',
    delimiter: '',
    quoteChar: '\"',
    quoteEscapeChar: '\\',
    hederSize: 1,
    fieldNamesLine: 1,
    dateFormats: ['yyyy-MM-dd HH:mm:ss', 'MM/dd/yyyy HH:mm:ss', 'yyyy-MM-dd'],
    rowsToInspect: 3333,
    sampleSize: 33,
    delimiterType: 'delimited',
    header: 'yes'
  }
};

export const PARSER_CONFIG = {
  parser: {
    fields: [],
    file: '',
    lineSeparator: '\\n',
    delimiter: ',',
    quoteChar: '\"',
    quoteEscape: '\\',
    headerSize: 1,
    fieldNameLine: 0
  },
  outputs: [{
    dataSet: '',
    mode: 'replace', // append
    description: ''
  }],
  parameters: [{
    name: 'spark.master',
    value: 'yarn'
  }, {
    name: 'spark.executor.instances',
    value: '6'
  }]
};

export const STAGING_TREE = [{ 'name': 'Staging', 'size': 0, 'isDirectory': true, 'path': 'root' }];