import { ChannelType } from './models/workbench.interface';

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

export const STAGING_TREE = {
  name: 'Staging',
  size: Infinity,
  isDirectory: true,
  path: 'root'
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

export interface ChannelType {
  name: string;
  uid: string;
  imgsrc: string;
  supported: boolean;
}

export enum CHANNEL_UID {
  SFTP = 'sftp',
  API = 'apipull',
  AMAZON_S3 = 's3',
  MAPR = 'mapr',
  ELASTIC_SEARCH = 'es',
  MARIA_DB = 'mariadb',
  HDFS = 'hdfs',
  MY_SQL = 'mysql',
  SQL_SERVER = 'sqlserver',
  MONGO_DB = 'mongodb',
  JDBC = 'jdbc',
  NONE = ''
}
export const CHANNEL_TYPES: ChannelType[] = [
  {
    name: 'SFTP',
    uid: CHANNEL_UID.SFTP,
    imgsrc: 'assets/img/sftp.png',
    supported: true
  },
  {
    name: 'API',
    uid: CHANNEL_UID.API,
    imgsrc: 'assets/img/api-source.png',
    supported: true
  },
  {
    name: 'Amazon S3',
    uid: CHANNEL_UID.AMAZON_S3,
    imgsrc: 'assets/img/s3.png',
    supported: false
  },
  {
    name: 'MAPR',
    uid: CHANNEL_UID.MAPR,
    imgsrc: 'assets/svg/mapr.svg',
    supported: false
  },
  {
    name: 'Elastic Search',
    uid: CHANNEL_UID.ELASTIC_SEARCH,
    imgsrc: 'assets/svg/elastic.svg',
    supported: false
  },
  {
    name: 'MariaDB',
    uid: CHANNEL_UID.MARIA_DB,
    imgsrc: 'assets/img/mariadb.jpg',
    supported: false
  },
  {
    name: 'HDFS',
    uid: CHANNEL_UID.HDFS,
    imgsrc: 'assets/img/hadoop.jpg',
    supported: false
  },
  {
    name: 'MySQL',
    uid: CHANNEL_UID.MY_SQL,
    imgsrc: 'assets/svg/mysql.svg',
    supported: false
  },
  {
    name: 'SQL Server',
    uid: CHANNEL_UID.SQL_SERVER,
    imgsrc: 'assets/img/sqlserver.png',
    supported: false
  },
  {
    name: 'MongoDB',
    uid: CHANNEL_UID.MONGO_DB,
    imgsrc: 'assets/img/mongodb.png',
    supported: false
  },
  {
    name: 'JDBC',
    uid: CHANNEL_UID.JDBC,
    imgsrc: 'assets/img/jdbc.png',
    supported: false
  }
];

export const DEFAULT_CHANNEL_TYPE = CHANNEL_TYPES[0];
