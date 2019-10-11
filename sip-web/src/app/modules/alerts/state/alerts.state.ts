import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as clone from 'lodash/clone';
import * as lodashMap from 'lodash/map';
import * as every from 'lodash/every';
import * as isEqual from 'lodash/isEqual';
import * as cloneDeep from 'lodash/cloneDeep';
import { map } from 'rxjs/operators';
// import produce from 'immer';

import {
  ApplyAlertFilters,
  ResetAlertFilters,
  LoadAllAlertCount,
  LoadAllAlertSeverity,
  LoadSelectedAlertCount,
  LoadSelectedAlertRuleDetails,
  LoadAllAttributeValues,
  EditAlertFilter
} from './alerts.actions';
import { AlertsStateModel, AlertFilterModel } from '../alerts.interface';
import { DATE_PRESETS_OBJ, CUSTOM_DATE_PRESET_VALUE } from '../consts';
import { AlertsService } from '../services/alerts.service';

export const defaultAlertFilters: AlertFilterModel[] = [
  {
    preset: 'TW',
    fieldName: 'starttime',
    type: 'date'
  },
  {
    fieldName: 'attributeValue',
    value: '',
    operator: 'EQ',
    type: 'string'
  }
];

const defaultAlertsState: AlertsStateModel = {
  alertFilters: cloneDeep(defaultAlertFilters),
  editedAlertFilters: cloneDeep(defaultAlertFilters),
  editedAlertsValidity: lodashMap(defaultAlertFilters, () => false),
  allAlertsCountChartData: null,
  allAlertsSeverityChartData: null,
  selectedAlertCountChartData: null,
  selectedAlertRuleDetails: null,
  allAttributeValues: null
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
  static getAlertFilters(state: AlertsStateModel) {
    return state.alertFilters;
  }

  @Selector()
  static getEditedAlertFilters(state: AlertsStateModel) {
    return state.editedAlertFilters;
  }

  @Selector()
  static areEditedAlertsValid(state: AlertsStateModel) {
    const { editedAlertFilters, alertFilters } = state;
    return (
      every(editedAlertFilters) && !isEqual(editedAlertFilters, alertFilters)
    );
  }

  @Selector()
  static getAlertFilterStrings(state: AlertsStateModel) {
    const filterStrings = lodashMap(state.alertFilters, filter => {
      switch (filter.type) {
        case 'date':
          const { preset, lte, gte } = filter;
          const isCustomFilter = preset === CUSTOM_DATE_PRESET_VALUE;
          return isCustomFilter
            ? `${gte} -> ${lte}`
            : DATE_PRESETS_OBJ[preset].label;
        default:
          return null;
      }
    });
    return filterStrings;
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

  @Selector()
  static getAllAttributeValues(state: AlertsStateModel) {
    return state.allAttributeValues;
  }

  @Action(ApplyAlertFilters)
  applyAlertFilter({
    patchState,
    dispatch,
    getState
  }: StateContext<AlertsStateModel>) {
    const { editedAlertFilters } = getState();
    patchState({ alertFilters: cloneDeep(editedAlertFilters) });
    dispatch([new LoadAllAlertCount(), new LoadAllAlertSeverity()]);
  }

  @Action(EditAlertFilter)
  editAlertFilter(
    { patchState, getState }: StateContext<AlertsStateModel>,
    { alertFilter, index }: EditAlertFilter
  ) {
    const { editedAlertFilters } = getState();
    editedAlertFilters[index] = alertFilter;
    patchState({ editedAlertFilters });
  }

  @Action(ResetAlertFilters)
  resetAlertFilter({ patchState }: StateContext<AlertsStateModel>) {
    patchState({
      alertFilters: cloneDeep(defaultAlertFilters),
      editedAlertFilters: cloneDeep(defaultAlertFilters)
    });
  }

  @Action(LoadAllAlertCount)
  loadAllAlertCount({ patchState, getState }: StateContext<AlertsStateModel>) {
    const { alertFilters } = getState();
    return this._alertsService
      .getAllAlertsCount(alertFilters)
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
    const { alertFilters } = getState();
    return this._alertsService
      .getAllAlertsSeverity(alertFilters)
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
  loadSelectedAlertCount(
    { patchState, getState }: StateContext<AlertsStateModel>,
    { id }: LoadSelectedAlertCount
  ) {
    const { alertFilters } = getState();
    return this._alertsService
      .getAlertCountById(id, alertFilters)
      .pipe(mapAlertCount2ChartData)
      .toPromise()
      .then(selectedAlertCountChartData => {
        patchState({ selectedAlertCountChartData });
      });
  }

  @Action(LoadSelectedAlertRuleDetails)
  loadSelectedAlertRuleDetails(
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

  @Action(LoadAllAttributeValues)
  loadAllAttributeValues({ patchState }: StateContext<AlertsStateModel>) {
    return this._alertsService
      .getAllAttributeValues()
      .toPromise()
      .then(allAttributeValues => {
        patchState({ allAttributeValues });
      });
  }
}
