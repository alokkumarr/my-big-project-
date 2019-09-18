import { AlertFilterModel } from '../alerts.interface';

export class ApplyAlertFilters {
  static readonly type = '[Alerts] Apply Filters';
  constructor(public alertFilter: AlertFilterModel) {}
}

export class ResetAlertFilters {
  static readonly type = '[Alerts] Reset Filters';
  constructor() {}
}

export class LoadAllAlertCount {
  static readonly type = '[Alerts] Load all alert count list';
  constructor() {}
}

export class LoadAllAlertSeverity {
  static readonly type = '[Alerts] Load all alert severity list';
  constructor() {}
}

export class LoadSelectedAlertCount {
  static readonly type = '[Alerts] Load a single alert count list';
  constructor(public id: number) {}
}

export class LoadSelectedAlertRuleDetails {
  static readonly type = '[Alerts] Load selected alert rule details';
  constructor(public id: number) {}
}
