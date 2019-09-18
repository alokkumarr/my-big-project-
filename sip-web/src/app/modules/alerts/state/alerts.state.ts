import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as clone from 'lodash/clone';
import * as lodashMap from 'lodash/map';
import { map } from 'rxjs/operators';
// import produce from 'immer';

import {
  ApplyAlertFilters,
  ResetAlertFilters,
  LoadAllAlertCount,
  LoadAllAlertSeverity,
  LoadSelectedAlertCount,
  LoadSelectedAlertRuleDetails
} from './alerts.actions';
import { AlertsStateModel, AlertFilterModel } from '../alerts.interface';
import { DATE_PRESETS_OBJ, CUSTOM_DATE_PRESET_VALUE } from '../consts';
import { AlertsService } from '../services/alerts.service';

const defaultAlertFilter: AlertFilterModel = {
  preset: 'TW',
  groupBy: 'StartTime'
};

const defaultAlertsState: AlertsStateModel = {
  alertFilter: defaultAlertFilter,
  allAlertsCountChartData: null,
  allAlertsSeverityChartData: null,
  selectedAlertCountChartData: null,
  selectedAlertRuleDetails: null
};

const mapAlertCount2ChartData = map(countList => ({
  x: lodashMap(countList, ({ date }) => date),
  y: lodashMap(countList, ({ count }) => count)
}));

const severityColors = {
  WARNING: '#a5b7ce',
  LOW: '#24b18c',
  MEDIUM: '#ffbe00',
  CRITICAL: '#e4524c'
};

@State<AlertsStateModel>({
  name: 'alertsState',
  defaults: <AlertsStateModel>clone(defaultAlertsState)
})
export class AlertsState {
  constructor(private _alertsService: AlertsService) {}

  @Selector()
  static getAlertFilter(state: AlertsStateModel) {
    return state.alertFilter;
  }

  @Selector()
  static getAlertFilterString(state: AlertsStateModel) {
    const { preset, startTime, endTime } = state.alertFilter;
    const isCustomFilter = preset === CUSTOM_DATE_PRESET_VALUE;
    return isCustomFilter
      ? `${startTime} -> ${endTime}`
      : DATE_PRESETS_OBJ[preset].label;
  }

  @Selector()
  static getAllAlertsCountChartData(state: AlertsStateModel) {
    return state.allAlertsCountChartData;
  }

  @Selector()
  static getAllAlertsSeverityChartData(state: AlertsStateModel) {
    return state.allAlertsSeverityChartData;
  }

  @Selector()
  static getSelectedAlertCountChartData(state: AlertsStateModel) {
    return state.selectedAlertCountChartData;
  }

  @Selector()
  static getSelectedAlertRuleDetails(state: AlertsStateModel) {
    return state.selectedAlertRuleDetails;
  }

  @Action(ApplyAlertFilters)
  applyAlertFilter(
    { patchState }: StateContext<AlertsStateModel>,
    { alertFilter }: ApplyAlertFilters
  ) {
    patchState({ alertFilter });
  }

  @Action(ResetAlertFilters)
  resetAlertFilter({ patchState }: StateContext<AlertsStateModel>) {
    patchState({ alertFilter: defaultAlertFilter });
  }

  @Action(LoadAllAlertCount)
  loadAllAlertCount({ patchState, getState }: StateContext<AlertsStateModel>) {
    const { alertFilter } = getState();
    return this._alertsService
      .getAllAlertsCount(alertFilter)
      .pipe(mapAlertCount2ChartData)
      .toPromise()
      .then(allAlertsCountChartData => {
        patchState({ allAlertsCountChartData });
      });
  }

  @Action(LoadAllAlertSeverity)
  loadAllAlertSeverity({
    patchState,
    getState
  }: StateContext<AlertsStateModel>) {
    const { alertFilter } = getState();
    return this._alertsService
      .getAllAlertsSeverity(alertFilter)
      .pipe(
        map(severityList => ({
          x: lodashMap(severityList, 'alertSeverity'),
          y: lodashMap(severityList, ({ alertSeverity, count }) => ({
            color: severityColors[alertSeverity],
            y: count
          }))
        }))
      )
      .toPromise()
      .then(allAlertsSeverityChartData => {
        patchState({ allAlertsSeverityChartData });
      });
  }

  @Action(LoadSelectedAlertCount)
  LoadSelectedAlertCount(
    { patchState, getState }: StateContext<AlertsStateModel>,
    { id }: LoadSelectedAlertCount
  ) {
    const { alertFilter } = getState();
    return this._alertsService
      .getAlertCountById(id, alertFilter)
      .pipe(mapAlertCount2ChartData)
      .toPromise()
      .then(selectedAlertCountChartData => {
        patchState({ selectedAlertCountChartData });
      });
  }

  @Action(LoadSelectedAlertRuleDetails)
  LoadSelectedAlertRuleDetails(
    { patchState }: StateContext<AlertsStateModel>,
    { id }: LoadSelectedAlertRuleDetails
  ) {
    return this._alertsService
      .getAlertRuleDetails(id)
      .toPromise()
      .then(selectedAlertRuleDetails => {
        patchState({ selectedAlertRuleDetails });
      });
  }
}
