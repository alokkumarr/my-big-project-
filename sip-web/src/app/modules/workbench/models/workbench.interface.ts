export interface DetailForm {
  valid: boolean;
  value: any;
  testConnectivityValue: any;
}

export enum CHANNEL_OPERATION {
  CREATE = 'create',
  UPDATE = 'update'
}

export enum ROUTE_OPERATION {
  CREATE = 'create',
  UPDATE = 'update'
}

export enum CHANNEL_ACCESS {
  READ = 'R',
  READ_WRITE = 'RW'
}

export enum HTTP_METHODS {
  POST = 'POST',
  GET = 'GET',
  PUT = 'PUT',
  DELETE = 'DELETE',
  PATCH = 'PATCH'
}

export interface ChannelObject {
  createdBy: string;
  productCode: string;
  projectCode: string;
  customerCode: string;
  channelType: string;
  channelMetadata: string;
  status?: number;
}

export interface SFTPChannelMetadata {
  channelName: string;
  hostName: string;
  portNo: string;
  userName: string;
  password: string;
  description: string;
  accessType: string;
}

export interface SFTPRouteMetadata {
  routeName: string;
  filePattern: string;
  sourceLocation: string;
  destinationLocation: string;
  batchSize: number;
  description?: string;
  disableDuplicate?: boolean;
  disableConcurrency?: boolean;
  fileExclusions?: string;
  lastModifiedLimitHours?: string;
}

export interface APIChannelMetadata {
  channelName: string;
  hostAddress: string;
  port: number;
  description?: string;
  apiEndPoint?: string;
  httpMethod: string;
  bodyParameters?: {
    content: string;
  };
  headerParameters: Array<{ key: string; value: string }>;
  queryParameters: Array<{ key: string; value: string }>;
  urlParameters: Array<{ key: string; value: string }>;
}

export interface APIRouteMetadata {
  routeName: string;
  description?: string;
  destinationLocation: string;
  lastModifiedLimitHours?: string;
  apiEndPoint: string;
  httpMethod: string;
  bodyParameters?: {
    content: string;
  };
  headerParameters: Array<{ key: string; value: string }>;
  queryParameters: Array<{ key: string; value: string }>;
  urlParameters: Array<{ key: string; value: string }>;
}

export interface Job {
  jobId: number;
  jobName: string;
  startTime: string;
  endTime: string;
  jobStatus: string;
  totalCount: number;
  successCount: number;
  filePattern: string;
  jobType: string;
  createdDate: string;
  createdBy: string;
  updatedDate: string;
  updatedBy: string;
}

export interface JobLog {
  pid: string /* don't show */;
  routeSysId: number /* don't show */;
  filePattern: string;
  source: string;
  fileName: string;
  actualFileRecDate: string;
  recdFileName: string;
  recdFileSize: number;
  mflFileStatus: string;
  bisProcessState: string;
  modifiedDate: string;
  createdDate: string;
  transferStartTime: string;
  transferEndTime: string;
  transferDuration: string;
  checkpointDate: string;
  reason: string;
  bisChannelSysId: number;
  bisChannelType: string;
  job: any;
}

export interface ChannelForJobs {
  id: number;
  name: string;
}

export interface RouteForJobs {
  id: number;
  name: string;
}

export interface ChannelType {
  name: string;
  uid: string;
  imgsrc: string;
  supported: boolean;
}

export interface WorkbenchStateModel {
  channelTypeList: ChannelType[];
  selectedChannelTypeId: string;
  channelList: ChannelForJobs[];
  selectedChannelId: number;
  routeList: RouteForJobs[];
  selectedRouteId: number;
  lastJobsPath: string;
  jobs: Job[];
  jobLogs: JobLog[];
}
