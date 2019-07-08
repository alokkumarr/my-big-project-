import { Job, JobLog } from '../models/workbench.interface';

export class LoadJobs {
  static readonly type = '[Workbench] Load jobs';
  constructor() {}
}

export class LoadJobByJobId {
  static readonly type = '[Workbench] Load job';
  constructor(public jobId: string) {}
}

export class SetLastJobsPath {
  static readonly type = '[Workbench] Set last jobs request path';
  constructor(public lastJobsPath: string) {}
}
export class SetJobs {
  static readonly type = '[Workbench] Set jobs';
  constructor(public jobs: Job[]) {}
}

export class SetJobLogs {
  static readonly type = '[Workbench] Set job logs';
  constructor(public jobLogs: JobLog[]) {}
}

export class LoadJobLogs {
  static readonly type = '[Workbench] Load job logs';
  constructor(public jobId: number) {}
}

export class LoadChannelList {
  static readonly type = '[Workbench] Load channel list';
  constructor() {}
}

export class LoadRouteList {
  static readonly type = '[Workbench] Load route list';
  constructor(public channelId: number) {}
}

export class SelectChannelTypeId {
  static readonly type = '[Workbench] Select channel type id';
  constructor(public channelType: string) {}
}

export class SelectChannelId {
  static readonly type = '[Workbench] Select channel id';
  constructor(public channelId: number) {}
}

export class SelectRouteId {
  static readonly type = '[Workbench] Select route id';
  constructor(public routeId: number) {}
}
