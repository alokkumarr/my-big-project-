import map from 'lodash/fp/map';
import cloneDeep from 'lodash/cloneDeep';
import get from 'lodash/get';
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

const FILTER_TYPES = {
  STRING: 'string',
  NUMBER: 'number',
  DATE: 'date',
  UNKNOWN: 'unknown'
};

export const NUMBER_TYPES = ['int', 'integer', 'double', 'long', 'float'];

export const DEFAULT_BOOLEAN_CRITERIA = BOOLEAN_CRITERIA[0];

export function FilterService($q, $mdDialog) {
  'ngInject';

  return {
    getFilterEvaluator,
    getEvaluatedFilterReducer,
    getRuntimeFilterValues,
    isFilterEmpty,
    isFilterModelNonEmpty,
    frontend2BackendFilter,
    backend2FrontendFilter
  };

  function getType(inputType) {
    if (inputType === FILTER_TYPES.STRING) {
      return FILTER_TYPES.STRING;
    } else if (NUMBER_TYPES.indexOf(inputType) >= 0) {
      return FILTER_TYPES.NUMBER;
    } else if (inputType === FILTER_TYPES.DATE) {
      return FILTER_TYPES.DATE;
    }

    return FILTER_TYPES.UNKNOWN;
  }

  function isFilterEmpty(filter) {
    const filterType = getType(filter.type || filter.column.type);

    switch (filterType) {

      case FILTER_TYPES.STRING:
        return isEmpty(get(filter, 'model.modelValues', []));

      case FILTER_TYPES.NUMBER:
        return isEmpty(filter.model);

      case FILTER_TYPES.DATE:
        return isEmpty(filter.model);

      default:
        return true;
    }
  }

  function frontend2BackendFilter() {
    return frontendFilter => {
      const column = frontendFilter.column;

      const result = {
        type: column.type,
        tableName: column.table,
        columnName: column.columnName,
        isRuntimeFilter: frontendFilter.isRuntimeFilter
      };

      if (!frontendFilter.isRuntimeFilter || frontendFilter.model) {
        result.model = frontendFilter.model;
      }

      return result;
    };
  }

  function backend2FrontendFilter(artifacts) {
    return backendFilter => {
      // for some reason in th edit screen the artofactName is not present in the artifact object
      // and the target artifact cannot be found
      // this is a temporary solution for pivot and chart types
      // TODO undo this modification after consulting with backend
      const artifact = artifacts.length > 1 ?
        find(artifacts,
          ({artifactName}) => artifactName === backendFilter.tableName) :
        artifacts[0];

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

  function getRuntimeFiltersFrom(filters = []) {
    return filter(f => f.isRuntimeFilter, filters);
  }

  function openRuntimeModal(analysis, filters = []) {
    const tpl = '<analyze-filter-modal filters="filters" artifacts="artifacts" filter-boolean-criteria="booleanCriteria" runtime="true"></analyze-filter-modal>';
    return $mdDialog.show({
      template: tpl,
      controller: scope => {
        scope.filters = map(backend2FrontendFilter(analysis.artifacts), filters);
        scope.artifacts = analysis.artifacts;
        scope.booleanCriteria = analysis.sqlBuilder.booleanCriteria;
      },
      fullscreen: true,
      autoWrap: false,
      multiple: true
    }).then(onApplyFilters.bind(this)(analysis));
  }

  function onApplyFilters(analysis) {
    return result => {
      if (!result) {
        return $q.reject(new Error('Cancelled'));
      }

      const filterPayload = map(frontend2BackendFilter(), result.filters);
      analysis.sqlBuilder.filters = filterPayload.concat(
        filter(f => !f.isRuntimeFilter, analysis.sqlBuilder.filters)
      );

      return analysis;
    };
  }

  function getRuntimeFilterValues(analysis) {
    const clone = cloneDeep(analysis);
    const runtimeFilters = getRuntimeFiltersFrom(
      get(clone, 'sqlBuilder.filters', [])
    );

    if (!runtimeFilters.length) {
      return $q.resolve(clone);
    }
    return openRuntimeModal(clone, runtimeFilters);
  }
}
