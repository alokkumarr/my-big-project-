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

export const CHANNEL_TYPES: any[] = [
  {
    name: 'SFTP',
    uid: 'sftp',
    imgsrc: 'assets/img/sftp.png',
    supported: true
  },
  {
    name: 'Amazon S3',
    uid: 's3',
    imgsrc: 'assets/img/s3.png',
    supported: false
  },
  {
    name: 'MAPR',
    uid: 'mapr',
    imgsrc: 'assets/svg/mapr.svg',
    supported: false
  },
  {
    name: 'Elastic Search',
    uid: 'es',
    imgsrc: 'assets/svg/elastic.svg',
    supported: false
  },
  {
    name: 'MariaDB',
    uid: 'maridb',
    imgsrc: 'assets/img/mariadb.jpg',
    supported: false
  },
  {
    name: 'HDFS',
    uid: 'hdfs',
    imgsrc: 'assets/img/hadoop.jpg',
    supported: false
  },
  {
    name: 'MySQL',
    uid: 'mysql',
    imgsrc: 'assets/svg/mysql.svg',
    supported: false
  },
  {
    name: 'SQL Server',
    uid: 'sqlserver',
    imgsrc: 'assets/img/sqlserver.png',
    supported: false
  },
  {
    name: 'MongoDB',
    uid: 'mongodb',
    imgsrc: 'assets/img/mongodb.png',
    supported: false
  },
  {
    name: 'JDBC',
    uid: 'jdbc',
    imgsrc: 'assets/img/jdbc.png',
    supported: false
  }
];
