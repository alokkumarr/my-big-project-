import map from 'lodash/fp/map';
import get from 'lodash/fp/get';
import uniq from 'lodash/uniq';
import transfrom from 'lodash/transform';
import toPairs from 'lodash/fp/toPairs';
import pipe from 'lodash/fp/pipe';
import reduce from 'lodash/fp/reduce';
import filter from 'lodash/fp/filter';
import isEmpty from 'lodash/isEmpty';
import set from 'lodash/fp/set';
import values from 'lodash/values';
import compact from 'lodash/compact';
import forEach from 'lodash/forEach';
import find from 'lodash/find';

import {OPERATORS} from '../components/analyze-filter-sidenav/filters/number-filter.component';

export const BOOLEAN_CRITERIA = {
  AND: 'AND',
  OR: 'OR'
};

export const DEFAULT_BOOLEAN_CRITERIA = BOOLEAN_CRITERIA.AND;

const EVENTS = {
  OPEN_SIDENAV: 'OPEN_SIDENAV',
  APPLY_FILTERS: 'APPLY_FILTERS',
  CLEAR_ALL_FILTERS: 'CLEAR_ALL_FILTERS'
};

export function FilterService($mdSidenav, $eventEmitter, $log) {
  'ngInject';

  const unRegisterFuncs = [];

  return {
    onOpenFilterSidenav,
    onApplyFilters,
    onClearAllFilters,
    offOpenFilterSidenav,
    offApplyFilters,
    offClearAllFilters,
    openFilterSidenav,
    applyFilters,
    clearAllFilters,
    getFilterEvaluator,
    getEvaluatedFilterReducer,
    getSelectedFilterMapper,
    isFilterModelNonEmpty,
    getCanvasFieldsToFiltersMapper,
    getChartSetttingsToFiltersMapper,
    getGridDataFilter,
    getFilterClearer,
    getFrontEnd2BackEndFilterMapper,
    getBackEnd2FrontEndFilterMapper,
    mergeCanvasFiltersWithPossibleFilters
  };

  function onOpenFilterSidenav(callback) {
    unRegisterFuncs[EVENTS.OPEN_SIDENAV] = $eventEmitter.on(EVENTS.OPEN_SIDENAV, callback);
  }

  function onApplyFilters(callback) {
    unRegisterFuncs[EVENTS.APPLY_FILTERS] = $eventEmitter.on(EVENTS.APPLY_FILTERS, callback);
  }

  function onClearAllFilters(callback) {
    unRegisterFuncs[EVENTS.CLEAR_ALL_FILTERS] = $eventEmitter.on(EVENTS.CLEAR_ALL_FILTERS, callback);
  }

  function offOpenFilterSidenav() {
    unRegisterFuncs[EVENTS.OPEN_SIDENAV]();
  }

  function offApplyFilters() {
    unRegisterFuncs[EVENTS.APPLY_FILTERS]();
  }

  function offClearAllFilters() {
    unRegisterFuncs[EVENTS.CLEAR_ALL_FILTERS]();
  }

  function openFilterSidenav(payload, sidenavId) {
    $eventEmitter.emit(EVENTS.OPEN_SIDENAV, payload);
    $mdSidenav(sidenavId).open();
  }

  function applyFilters(payload, sidenavId) {
    $eventEmitter.emit(EVENTS.APPLY_FILTERS, payload);
    $mdSidenav(sidenavId).close();
  }

  function clearAllFilters(sidenavId) {
    $eventEmitter.emit(EVENTS.CLEAR_ALL_FILTERS);
    $mdSidenav(sidenavId).close();
  }

  /* eslint-disable camelcase */
  function getFrontEnd2BackEndFilterMapper() {
    return frontEndFilter => {
      const backEndFilter = {
        column_name: frontEndFilter.name,
        label: frontEndFilter.label,
        table_name: frontEndFilter.tableName,
        boolean_criteria: frontEndFilter.booleanCriteria,
        filter_type: frontEndFilter.type
      };

      if (frontEndFilter.type === 'int' || frontEndFilter.type === 'double') {
        backEndFilter.operator = frontEndFilter.operator;
        backEndFilter.search_conditions =
          frontEndFilter.operator === OPERATORS.BETWEEN ?
          [frontEndFilter.model.otherValue, frontEndFilter.model.value] :
          [frontEndFilter.model.value];

      } else if (frontEndFilter.type === 'string') {
        backEndFilter.operator = null;
        backEndFilter.search_conditions = pipe(
          // transform the model object to an array of strings
          toPairs,
          // filter only the ones that are truthy
          // in case someone checked and unchecked the checkbox
          filter(get('1')),
          // take only the string value
          map(get('0'))
        )(frontEndFilter.model);
      }

      return backEndFilter;
    };
  }
  /* eslint-enable camelcase */

  /* eslint-disable camelcase */
  function getBackEnd2FrontEndFilterMapper() {
    return backEndFilter => {
      const frontEndFilter = {
        name: backEndFilter.column_name,
        label: backEndFilter.label,
        tableName: backEndFilter.table_name,
        booleanCriteria: backEndFilter.boolean_criteria,
        type: backEndFilter.filter_type
      };

      if (backEndFilter.filter_type === 'int' || backEndFilter.filter_type === 'double') {
        frontEndFilter.operator = backEndFilter.operator;
        frontEndFilter.model = {
          otherValue: backEndFilter.operator === OPERATORS.BETWEEN ?
            backEndFilter.search_conditions[0] : null,

          value: backEndFilter.operator === OPERATORS.BETWEEN ?
            backEndFilter.search_conditions[1] :
            backEndFilter.search_conditions[0]
        };
      } else if (backEndFilter.filter_type === 'string') {
        // transform a string of arrays to an object with the strings as keys
        frontEndFilter.model = transfrom(backEndFilter.search_conditions,
          (model, value) => {
            model[value] = true;
          },
        {});
      }

      return frontEndFilter;
    };
  }

  function getChartSetttingsToFiltersMapper(gridData) {
    return pipe(
      filter(get('filter_eligible')),
      map(field => {
        return {
          tableName: (field.table ? field.table.name : field.tableName),
          label: field.alias || field.displayName || field.display_name,
          name: field.name || field.column_name,
          type: field.type,
          model: null,
          booleanCriteria: DEFAULT_BOOLEAN_CRITERIA,
          items: field.type === 'string' ? uniq(map(get(field.name || field.column_name), gridData)) : null
        };
      }));
  }
  /* eslint-enable camelcase */

  function getCanvasFieldsToFiltersMapper(gridData) {
    return pipe(
      filter(get('isFilterEligible')),
      map(field => {
        return {
          tableName: (field.table ? field.table.name : field.tableName),
          label: field.alias || field.displayName,
          name: field.name,
          type: field.type,
          model: null,
          booleanCriteria: DEFAULT_BOOLEAN_CRITERIA,
          items: field.type === 'string' ? uniq(map(get(field.name), gridData)) : null
        };
      }));
  }

  function getFilterClearer() {
    return map(pipe(
      set('model', null),
      set('operator', null),
      set('booleanCriteria', DEFAULT_BOOLEAN_CRITERIA)
    ));
  }

  function getSelectedFilterMapper() {
    return filter(filter => isFilterModelNonEmpty(filter.model));
  }

  function isFilterModelNonEmpty(model) {
    if (!model) {
      return false;
    }

    // can be an empty array if the filter was a string filter
    // and the checkboxes were unchecked
    if (isEmpty(model)) {
      return false;
    }

    // can be an object with null values
    if (isEmpty(compact(values(model)))) {
      return false;
    }
    return true;
  }

  function getGridDataFilter(filters) {
    return filter(row => {
      return pipe(
        getFilterEvaluator(row),
        getEvaluatedFilterReducer()
      )(filters);
    });
  }

  /**
   * Get the lazy filter evaluator for ever row
   */
  function getFilterEvaluator(row) {
    return map(filter => {
      let isValid;

      switch (filter.type) {
        case 'string':
          isValid = Boolean(filter.model[row[filter.name]]);
          break;
        case 'int':
        case 'double':
          isValid = Boolean(isNumberValid(row[filter.name], filter.model, filter.operator));
          break;
        default:
          isValid = false;
          break;
      }

      return {
        booleanCriteria: filter.booleanCriteria,
        value: isValid
      };
    });
  }

  function mergeCanvasFiltersWithPossibleFilters(canvasFilters, possibleFilters) {
    forEach(possibleFilters, possibleFilter => {
      try {
        const targetCanvasFilter = find(canvasFilters, canvasFilter => {
          const tableName = canvasFilter.table ? canvasFilter.table.name : canvasFilter.tableName;
          return possibleFilter.name === canvasFilter.name &&
            possibleFilter.tableName === (tableName);
        }) || {};

        Object.assign(possibleFilter, targetCanvasFilter);
      } catch (err) {
        $log.error(err);
      }
    });
  }

/**
 * reduce the array of evaluated filters and their booleanCriteria( AND | OR )
 */
  function getEvaluatedFilterReducer() {
    return (evaluatedFilters => {
      // we need to know the first elements booleanCriteria to get the identity element
      // so that we don't influence the result
      const accumulator = isEmpty(evaluatedFilters) ? true : getIdentityElement(evaluatedFilters[0].booleanCriteria);

      return reduce((accum, evaluatedFilter) => {

        return evaluateBoolean(accum, evaluatedFilter.booleanCriteria, evaluatedFilter.value);

      }, accumulator)(evaluatedFilters);
    });
  }

  function getIdentityElement(booleanCriteria) {
    if (booleanCriteria === BOOLEAN_CRITERIA.AND) {
      return true;
    }

    if (booleanCriteria === BOOLEAN_CRITERIA.OR) {
      return false;
    }
  }

  function evaluateBoolean(a, booleanCriteria, b) {
    if (booleanCriteria === BOOLEAN_CRITERIA.AND) {
      return a && b;
    }

    if (booleanCriteria === BOOLEAN_CRITERIA.OR) {
      return a || b;
    }
  }

  function isNumberValid(number, numberFilterModel, operator) {
    const a = number;
    const b = numberFilterModel.value;
    const c = numberFilterModel.otherValue;

    switch (operator) {
      case OPERATORS.GREATER:
        return a > b;
      case OPERATORS.LESS:
        return a < b;
      case OPERATORS.GREATER_OR_EQUAL:
        return a >= b;
      case OPERATORS.LESS_OR_EQUAL:
        return a <= b;
      case OPERATORS.NOT_EQUALS:
        return a !== b;
      case OPERATORS.EQUALS:
        return a === b;
      case OPERATORS.BETWEEN:
        return c <= a && a <= b;
      default:
        return false;
    }
  }
}
