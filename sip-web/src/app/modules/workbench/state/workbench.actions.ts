export class LoadJobs {
  static readonly type = '[Workbench] Load jobs';
  constructor() {}
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
