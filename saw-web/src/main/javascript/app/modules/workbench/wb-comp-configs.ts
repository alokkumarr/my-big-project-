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

export const sourceTypes: any[] = [
  {
    name: 'SFTP',
    uid: 'sftp',
    imgsrc: 'assets/img/sftp.png'
  },
  {
    name: 'Amazon S3',
    uid: 's3',
    imgsrc: 'assets/img/s3.png'
  },
  {
    name: 'MAPR',
    uid: 'mapr',
    imgsrc: 'assets/svg/mapr.svg'
  },
  {
    name: 'Elastic Search',
    uid: 'es',
    imgsrc: 'assets/svg/elastic.svg'
  },
  {
    name: 'MariaDB',
    uid: 'maridb',
    imgsrc: 'assets/img/mariadb.jpg'
  },
  {
    name: 'HDFS',
    uid: 'hdfs',
    imgsrc: 'assets/img/hadoop.jpg'
  },
  {
    name: 'MySQL',
    uid: 'mysql',
    imgsrc: 'assets/svg/mysql.svg'
  },
  {
    name: 'SQL Server',
    uid: 'sqlserver',
    imgsrc: 'assets/img/sqlserver.png'
  },
  {
    name: 'MongoDB',
    uid: 'mongodb',
    imgsrc: 'assets/img/mongodb.png'
  },
  {
    name: 'JDBC',
    uid: 'jdbc',
    imgsrc: 'assets/img/jdbc.png'
  }
];
