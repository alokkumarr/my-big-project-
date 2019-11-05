import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as clone from 'lodash/clone';
import * as lodashMap from 'lodash/map';
import * as find from 'lodash/find';
import * as every from 'lodash/every';
import * as isEqual from 'lodash/isEqual';
import * as cloneDeep from 'lodash/cloneDeep';
import * as sortBy from 'lodash/sortBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpSplit from 'lodash/fp/split';
import * as fpJoin from 'lodash/fp/join';
import * as fpReverse from 'lodash/fp/reverse';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpFromPairs from 'lodash/fp/fromPairs';
import * as fpMap from 'lodash/fp/map';
import * as fpKeys from 'lodash/fp/keys';
import * as moment from 'moment';
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
  EditAlertFilter,
  ResetAlertChartData
} from './alerts.actions';
import { AlertsStateModel, AlertFilterModel, AlertChartData } from '../alerts.interface';
import {
  DATE_PRESETS_OBJ,
  CUSTOM_DATE_PRESET_VALUE,
  DATE_FORMAT
} from '../consts';
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

const defaultAlertChartData: AlertChartData = {
  x: [],
  y: [],
};

const defaultAlertsState: AlertsStateModel = {
  alertFilters: cloneDeep(defaultAlertFilters),
  editedAlertFilters: cloneDeep(defaultAlertFilters),
  editedAlertsValidity: lodashMap(defaultAlertFilters, () => false),
  allAlertsCountChartData: null,
  allAlertsSeverityChartData: null,
  selectedAlertCountChartData: cloneDeep(defaultAlertChartData),
  selectedAlertRuleDetails: null,
  allAttributeValues: null
};

/**
 * reverse "DD-MM-YYYY" date format to "YYYY-MM-DD"
 */
const reverseDateFormat = date => {
  return fpPipe(fpSplit('-'), fpReverse, fpJoin('-'))(date);
};

const orderAlertsCount = alertCountList =>
  sortBy(alertCountList, ({ date }) => reverseDateFormat(date));

const mapAlertCount2ChartData = map(countList => ({
  x: lodashMap(orderAlertsCount(countList), ({ date }) => date),
  y: lodashMap(orderAlertsCount(countList), ({ count }) => count)
}));



const severityColors = {
  WARNING: '#999',
  LOW: '#ffbe00',
  MEDIUM: '#ff9000',
  CRITICAL: '#d93e00'
};

/** Severity order map, gives the order of the severity in the chart */
const severityOrderMap = fpPipe(
  fpKeys,
  fpToPairs,
  fpMap(([key, value]) => [value, key]),
  fpFromPairs
)(severityColors);

const orderAlertsSeverity = alertSeverityList =>
  sortBy(
    alertSeverityList,
    ({ alertSeverity }) => severityOrderMap[alertSeverity]
  );

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
  static areFiltersApplied(state: AlertsStateModel) {
    const { alertFilters } = state;
    return !isEqual(alertFilters, defaultAlertFilters);
  }

  @Selector()
  static getAlertDateFilterString(state: AlertsStateModel) {
    const dateFilter = find(state.alertFilters, ({ type }) => type === 'date');
    const { preset, lte, gte } = dateFilter;
    const isCustomFilter = preset === CUSTOM_DATE_PRESET_VALUE;
    if (isCustomFilter) {
      const startDate = moment.utc(gte).format(DATE_FORMAT.YYYY_MM_DD);
      const endDate = moment.utc(lte).format(DATE_FORMAT.YYYY_MM_DD);
      return `${startDate} -> ${endDate}`;
    }
    return DATE_PRESETS_OBJ[preset].label;
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
  resetAlertFilter({ patchState, dispatch }: StateContext<AlertsStateModel>) {
    patchState({
      alertFilters: cloneDeep(defaultAlertFilters),
      editedAlertFilters: cloneDeep(defaultAlertFilters)
    });
    dispatch([new LoadAllAlertCount(), new LoadAllAlertSeverity()]);
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
          x: lodashMap(orderAlertsSeverity(severityList), 'alertSeverity'),
          y: lodashMap(
            orderAlertsSeverity(severityList),
            ({ alertSeverity, count }) => ({
              color: severityColors[alertSeverity],
              y: count
            })
          )
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

  @Action(ResetAlertChartData)
  resetAlertChartData({ patchState }: StateContext<AlertsStateModel>) {
    patchState({ selectedAlertCountChartData: cloneDeep(defaultAlertChartData) });
  }
}
