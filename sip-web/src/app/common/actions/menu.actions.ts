import { Menu } from '../state/common.state.model';

export class CommonStateUpdateMenu {
  static readonly type = '[Common State] Update Menu';
  constructor(public moduleName: string, public items: Menu) {}
}

export class CommonLoadAllMetrics {
  static readonly type = '[Common State] Load All Metrics';
  constructor() {}
}

export class CommonLoadMetricById {
  static readonly type = '[Common State] Load Metric By Id';
  constructor(public metricId: string) {}
}

export class AdminExportLoadMenu {
  static readonly type = '[Admin Export Page] Load Menu ';

  constructor(public moduleName: string) {}
}

export class CommonStateScheuleJobs {
  /* Use for only new DSL analyses. This is for fetching jobs for that category,  */
  static readonly type =
    '[Common State] Update schedule Jobs for a particular category';
  constructor(public cronJobs) {}
}

export class CommonResetStateOnLogout {
  static readonly type = '[Common State] Reset common state on logout';
  constructor() {}
}