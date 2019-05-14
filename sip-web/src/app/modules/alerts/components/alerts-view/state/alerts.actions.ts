import { AlertFiltersModel } from './alerts.model';

export class ApplyAlertFilters {
  static readonly type = '[Alerts] Apply Filters';
  constructor(public alertFilters: AlertFiltersModel) {}
}
