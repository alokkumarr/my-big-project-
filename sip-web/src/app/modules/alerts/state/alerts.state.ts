import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as clone from 'lodash/clone';
import * as lodashMap from 'lodash/map';
import * as find from 'lodash/find';
import * as every from 'lodash/every';
import * as isEqual from 'lodash/isEqual';
import * as cloneDeep from 'lodash/cloneDeep';
import * as toNumber from 'lodash/toNumber';
import * as forEach from 'lodash/forEach';
import * as split from 'lodash/split';
import * as mapKeys from 'lodash/mapKeys';
import * as omitBy from 'lodash/omitBy';
import * as isUndefined from 'lodash/isUndefined';
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
  EditAlertFilter
} from './alerts.actions';
import { AlertsStateModel, AlertFilterModel } from '../alerts.interface';
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
  x: lodashMap(orderAlertsCount(countList), ({ date }) => date),
  y: lodashMap(orderAlertsCount(countList), ({ count }) => count)
}));

const orderAlertsCount = alertData => {
  let sortedArray = [];
  /**
   * Here dates received is in 'DD-MM-YYYY' format. When trying to convert it into moment object
   * any date(DD in 'DD-MM-YYYY') more than 12 is resulting to 'Invalid date'.
   * So making sure that string is converted to moment object correctly.
   */
  forEach(alertData, obj => {
    sortedArray.push(
      toNumber(
        moment
          .utc(
            split(obj.date, '-', 3)
              .reverse()
              .join('-')
          )
          .format('x')
      )
    );
  });

  sortedArray = sortedArray.sort();

  forEach(sortedArray, (obj, index) => {
    sortedArray[index] = moment.utc(obj).format('DD-MM-YYYY');
    sortedArray[index] = find(alertData, {
      date: sortedArray[index]
    });
  });
  return sortedArray;
};

const orderAlertsSeverity = severity => {
  const orderedList = [];
  mapKeys(severityColors, (val, key) => {
    orderedList.push(find(severity, { alertSeverity: key }));
  });
  return omitBy(orderedList, isUndefined);
};

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
}
