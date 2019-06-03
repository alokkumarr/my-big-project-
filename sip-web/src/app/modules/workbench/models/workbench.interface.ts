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
  selectedChannelType: ChannelType;
  channelList: ChannelForJobs[];
  selectedChannel: ChannelForJobs;
  routeList: RouteForJobs[];
  selectedRoute: RouteForJobs;
  jobs: Job[];
}
