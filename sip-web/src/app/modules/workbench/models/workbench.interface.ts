export interface ChannelObject {
  createdBy: string;
  productCode: string;
  projectCode: string;
  customerCode: string;
  channelType: string;
  channelMetadata: string;
  status?: number;
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
