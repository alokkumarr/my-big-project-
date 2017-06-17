import map from 'lodash/fp/map';
import reduce from 'lodash/fp/reduce';
import filter from 'lodash/fp/filter';
import isEmpty from 'lodash/isEmpty';
import isNumber from 'lodash/isNumber';
import values from 'lodash/values';
import find from 'lodash/find';

import {OPERATORS} from '../components/analyze-filter/filters/number-filter.component';

export const BOOLEAN_CRITERIA = [{
  label: 'ALL',
  value: 'AND'
}, {
  label: 'ANY',
  value: 'OR'
}];

export const NUMBER_TYPES = ['int', 'integer', 'double', 'long', 'timestamp'];

export const DEFAULT_BOOLEAN_CRITERIA = BOOLEAN_CRITERIA[0];

export function FilterService() {
  'ngInject';

  return {
    getFilterEvaluator,
    getEvaluatedFilterReducer,
    isFilterModelNonEmpty,
    frontend2BackendFilter,
    backend2FrontendFilter
  };

  function frontend2BackendFilter() {
    return frontendFilter => {
      const column = frontendFilter.column;

      return {
        type: column.type,
        model: frontendFilter.model,
        tableName: column.table,
        columnName: column.columnName,
        isRuntimeFilter: frontendFilter.isRuntimeFilter
      };
    };
  }

  function backend2FrontendFilter(artifacts) {
    return backendFilter => {
      const artifact = find(artifacts,
        ({artifactName}) => artifactName === backendFilter.tableName);

      const column = find(artifact.columns,
        ({columnName}) => columnName === backendFilter.columnName);

      return {
        column,
        model: backendFilter.model,
        isRuntimeFilter: backendFilter.isRuntimeFilter
      };
    };
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
    if (booleanCriteria === BOOLEAN_CRITERIA[0]) {
      return true;
    }

    if (booleanCriteria === BOOLEAN_CRITERIA[1]) {
      return false;
    }
  }

  function evaluateBoolean(a, booleanCriteria, b) {
    if (booleanCriteria === BOOLEAN_CRITERIA[0]) {
      return a && b;
    }

    if (booleanCriteria === BOOLEAN_CRITERIA[1]) {
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
