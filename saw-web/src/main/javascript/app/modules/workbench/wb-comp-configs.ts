export const CSV_CONFIG = {
  file: '',
  lineSeparator: '\n',
  delimiter: ',',
  quoteChar: '\"',
  quoteEscapeChar: '\\',
  headerSize: 1,
  fieldNamesLine: 1,
  dateFormats: [],
  rowsToInspect: 3333,
  delimiterType: 'delimited',
  header: 'yes'
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