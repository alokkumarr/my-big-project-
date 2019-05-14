import { State, Action, StateContext, Selector } from '@ngxs/store';
import { ApplyAlertFilters } from './alerts.actions';
import { AlertFiltersModel } from './alerts.model';
import * as clone from 'lodash/clone';

const defaultAlertFilters: AlertFiltersModel = {
  preset: 'TW',
  groupBy: 'StartTime'
};

@State<AlertFiltersModel>({
  name: 'alertsFilters',
  defaults: <AlertFiltersModel>clone(defaultAlertFilters)
})
export class AlertsFilterState {
  @Selector()
  static getAlertFilters(state: AlertFiltersModel) {
    return state;
  }

  @Action(ApplyAlertFilters)
  add(ctx: StateContext<AlertFiltersModel>, action: ApplyAlertFilters) {
    ctx.patchState(action.alertFilters);
  }
}
