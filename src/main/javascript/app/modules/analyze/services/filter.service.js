import map from 'lodash/fp/map';
import get from 'lodash/fp/get';
import uniq from 'lodash/uniq';
import transfrom from 'lodash/transform';
import toPairs from 'lodash/fp/toPairs';
import pipe from 'lodash/fp/pipe';
import reduce from 'lodash/fp/reduce';
import filter from 'lodash/fp/filter';
import isEmpty from 'lodash/isEmpty';
import isNumber from 'lodash/isNumber';
import set from 'lodash/fp/set';
import values from 'lodash/values';
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

const NUMBER_TYPES = ['int', 'integer', 'double', 'long', 'timestamp'];

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
        columnName: frontEndFilter.name,
        label: frontEndFilter.label,
        tableName: frontEndFilter.tableName,
        booleanCriteria: frontEndFilter.booleanCriteria,
        filterType: frontEndFilter.type
      };

      if (NUMBER_TYPES.indexOf(frontEndFilter.type) >= 0) {
        backEndFilter.filterType = 'number';
        backEndFilter.operator = frontEndFilter.operator;
        backEndFilter.searchConditions =
          frontEndFilter.operator === OPERATORS.BETWEEN.value ?
          [frontEndFilter.model.otherValue, frontEndFilter.model.value] :
          [frontEndFilter.model.value];

      } else if (frontEndFilter.type === 'string') {
        backEndFilter.operator = null;
        backEndFilter.searchConditions = pipe(
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
        name: backEndFilter.columnName,
        label: backEndFilter.label,
        tableName: backEndFilter.tableName,
        booleanCriteria: backEndFilter.booleanCriteria,
        type: backEndFilter.filterType
      };

      if (NUMBER_TYPES.indexOf(backEndFilter.type) >= 0) {
        frontEndFilter.operator = backEndFilter.operator;
        frontEndFilter.model = {
          otherValue: backEndFilter.operator === OPERATORS.BETWEEN.value ?
            backEndFilter.searchConditions[0] : null,

          value: backEndFilter.operator === OPERATORS.BETWEEN.value ?
            backEndFilter.searchConditions[1] :
            backEndFilter.searchConditions[0]
        };
      } else if (backEndFilter.filterType === 'string') {
        // transform a string of arrays to an object with the strings as keys
        frontEndFilter.model = transfrom(backEndFilter.searchConditions,
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
      filter(get('filterEligible')),
      map(field => {
        return {
          tableName: (field.table ? field.table.name : field.tableName),
          label: field.alias || field.displayName || field.displayName,
          name: field.name || field.columnName,
          type: field.type,
          model: null,
          booleanCriteria: DEFAULT_BOOLEAN_CRITERIA,
          items: field.type === 'string' ? uniq(map(get(field.name || field.columnName), gridData)) : null
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
    if (isEmpty(filter(x => isNumber(x) || Boolean(x), values(model)))) {
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
        case 'integer':
        case 'timestamp':
        case 'double':
        case 'long':
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
      case OPERATORS.GREATER.value:
        return a > b;
      case OPERATORS.LESS.value:
        return a < b;
      case OPERATORS.GREATER_OR_EQUAL.value:
        return a >= b;
      case OPERATORS.LESS_OR_EQUAL.value:
        return a <= b;
      case OPERATORS.NOT_EQUALS.value:
        return a !== b;
      case OPERATORS.EQUALS.value:
        return a === b;
      case OPERATORS.BETWEEN.value:
        return c <= a && a <= b;
      default:
        return false;
    }
  }
}
