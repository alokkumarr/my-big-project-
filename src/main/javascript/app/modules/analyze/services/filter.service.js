import map from 'lodash/fp/map';
import get from 'lodash/fp/get';
import uniq from 'lodash/uniq';
import pipe from 'lodash/fp/pipe';
import reduce from 'lodash/fp/reduce';
import filter from 'lodash/fp/filter';
import isEmpty from 'lodash/isEmpty';
import set from 'lodash/fp/set';
import values from 'lodash/values';
import compact from 'lodash/compact';

import {ANALYZE_FILTER_SIDENAV_ID} from '../components/analyze-filter-sidenav/analyze-filter-sidenav.component';
import {OPERATORS} from '../components/analyze-filter-sidenav/filters/number-filter.component';

export const DEFAULT_FILTER_OPERATOR = 'AND';

export const FILTER_OPERATORS = {
  AND: 'AND',
  OR: 'OR'
};

const EVENTS = {
  OPEN_SIDENAV: 'OPEN_SIDENAV',
  APPLY_FILTERS: 'APPLY_FILTERS',
  CLEAR_ALL_FILTERS: 'CLEAR_ALL_FILTERS'
};

export function FilterService($mdSidenav, $eventHandler) {
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
    getCanvasFieldsToFiltersMapper,
    getGridDataFilter,
    getFilterClearer
  };

  function onOpenFilterSidenav(callback) {
    unRegisterFuncs[EVENTS.OPEN_SIDENAV] = $eventHandler.on(EVENTS.OPEN_SIDENAV, callback);
  }

  function onApplyFilters(callback) {
    unRegisterFuncs[EVENTS.APPLY_FILTERS] = $eventHandler.on(EVENTS.APPLY_FILTERS, callback);
  }

  function onClearAllFilters(callback) {
    unRegisterFuncs[EVENTS.CLEAR_ALL_FILTERS] = $eventHandler.on(EVENTS.CLEAR_ALL_FILTERS, callback);
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

  function openFilterSidenav(payload) {
    $eventHandler.emit(EVENTS.OPEN_SIDENAV, payload);
    $mdSidenav(ANALYZE_FILTER_SIDENAV_ID).open();
  }

  function applyFilters(payload) {
    $eventHandler.emit(EVENTS.APPLY_FILTERS, payload);
    $mdSidenav(ANALYZE_FILTER_SIDENAV_ID).close();
  }

  function clearAllFilters() {
    $eventHandler.emit(EVENTS.CLEAR_ALL_FILTERS);
    $mdSidenav(ANALYZE_FILTER_SIDENAV_ID).close();
  }

  function getCanvasFieldsToFiltersMapper(gridData) {
    return pipe(
      filter(get('isFilterEligible')),
      map(field => {
        return {
          label: field.alias || field.displayName,
          name: field.name,
          type: field.type,
          operator: DEFAULT_FILTER_OPERATOR,
          items: field.type === 'string' ? uniq(map(get(field.name), gridData)) : null
        };
      }));
  }

  function getFilterClearer() {
    return map(pipe(
      set('model', null),
      set('operator', DEFAULT_FILTER_OPERATOR)
    ));
  }

  function getSelectedFilterMapper() {
    return filter(filter => {
      if (!filter.model) {
        return false;
      }

      // can be an empty array if the filter was a string filter
      // and the checkboxes were unchecked
      if (isEmpty(filter.model)) {
        return false;
      }

      // can be an object with null values
      if (isEmpty(compact(values(filter.model)))) {
        return false;
      }
      return true;
    });
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
          isValid = Boolean(isNumberValid(row[filter.name], filter.model));
          break;
        default:
          isValid = false;
          break;
      }

      return {
        operator: filter.operator,
        value: isValid
      };
    });
  }

/**
 * reduce the array of evaluated filters and their operators( AND | OR )
 */
  function getEvaluatedFilterReducer() {
    return (evaluatedFilters => {
      // we need to know the first elements operator to tget the identity element
      // so that we don't influence the result
      const accumulator = isEmpty(evaluatedFilters) ? true : getIdentityElement(evaluatedFilters[0].operator);

      return reduce((accum, evaluatedFilter) => {

        return evaluateBoolean(accum, evaluatedFilter.operator, evaluatedFilter.value);

      }, accumulator)(evaluatedFilters);
    });
  }

  function getIdentityElement(operator) {
    if (operator === FILTER_OPERATORS.AND) {
      return true;
    }

    if (operator === FILTER_OPERATORS.OR) {
      return false;
    }
  }

  function evaluateBoolean(a, operator, b) {
    if (operator === FILTER_OPERATORS.AND) {
      return a && b;
    }

    if (operator === FILTER_OPERATORS.OR) {
      return a || b;
    }
  }

  function isNumberValid(number, numberFilterModel) {
    const a = number;
    const b = numberFilterModel.value;
    const c = numberFilterModel.otherValue;

    switch (numberFilterModel.operator) {
      case OPERATORS.GREATER:
        return a > b;
      case OPERATORS.LESS:
        return a < b;
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
