import {
  ChannelForJobs,
  RouteForJobs,
  ChannelType
} from '../models/workbench.interface';

export class LoadJobs {
  static readonly type = '[Workbench] Load jobs';
  constructor() {}
}

export class LoadChannelList {
  static readonly type = '[Workbench] Load channel list';
  constructor() {}
}

export class LoadRouteList {
  static readonly type = '[Workbench] Load route list';
  constructor(public channelId: string) {}
}

export class SelectChannelType {
  static readonly type = '[Workbench] Select channel type';
  constructor(public channelType: ChannelType) {}
}

export class SelectChannel {
  static readonly type = '[Workbench] Select channel';
  constructor(public channel: ChannelForJobs) {}
}

export class SelectRoute {
  static readonly type = '[Workbench] Select route';
  constructor(public route: RouteForJobs) {}
}
