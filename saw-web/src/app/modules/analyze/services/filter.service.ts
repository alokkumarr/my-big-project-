import { Injectable } from '@angular/core';
import * as map from 'lodash/fp/map';
import * as cloneDeep from 'lodash/cloneDeep';
import * as get from 'lodash/get';
import * as reduce from 'lodash/fp/reduce';
import * as filter from 'lodash/fp/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as isEmpty from 'lodash/isEmpty';
import * as isNumber from 'lodash/isNumber';
import * as values from 'lodash/values';
import * as find from 'lodash/find';

import { AnalyzeDialogService } from './analyze-dialog.service';

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
  TIMESTAMP: 'timestamp',
  UNKNOWN: 'unknown'
};

export const NUMBER_TYPES = ['int', 'integer', 'double', 'long', 'float'];

export const DEFAULT_BOOLEAN_CRITERIA = BOOLEAN_CRITERIA[0];

@Injectable()
export class FilterService {
  constructor(public _dialog: AnalyzeDialogService) {}

  getType(inputType) {
    if (inputType === FILTER_TYPES.STRING) {
      return FILTER_TYPES.STRING;
    } else if (NUMBER_TYPES.indexOf(inputType) >= 0) {
      return FILTER_TYPES.NUMBER;
    } else if (inputType === FILTER_TYPES.DATE) {
      return FILTER_TYPES.DATE;
    } else if (inputType === FILTER_TYPES.TIMESTAMP) {
      return FILTER_TYPES.TIMESTAMP;
    }

    return FILTER_TYPES.UNKNOWN;
  }

  isFilterEmpty(filter) {
    if (!filter) {
      return true;
    }

    const filterType = this.getType(filter.type || get(filter, 'column.type', FILTER_TYPES.UNKNOWN));

    switch (filterType) {

    case FILTER_TYPES.STRING:
      return isEmpty(get(filter, 'model.modelValues', []));

    case FILTER_TYPES.NUMBER:
      return isEmpty(filter.model);

    case FILTER_TYPES.DATE:
      return isEmpty(filter.model);

    case FILTER_TYPES.TIMESTAMP:
      return isEmpty(filter.model);

    default:
      return true;
    }
  }

  frontend2BackendFilter() {
    return frontendFilter => {
      const column = frontendFilter.column;

      const result = {
        type: column.type,
        tableName: column.table,
        columnName: column.columnName,
        isRuntimeFilter: frontendFilter.isRuntimeFilter,
        isGlobalFilter: frontendFilter.isGlobalFilter,
        model: undefined
      };
      if (!(frontendFilter.isRuntimeFilter || frontendFilter.isGlobalFilter) || frontendFilter.model) {
        result.model = frontendFilter.model;
      }

      return result;
    };
  }

  backend2FrontendFilter(artifacts) {
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
        isRuntimeFilter: backendFilter.isRuntimeFilter,
        isGlobalFilter: backendFilter.isGlobalFilter
      };
    };
  }

  isFilterModelNonEmpty(model) {
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
   * reduce the array of evaluated filters and their booleanCriteria( AND | OR )
  */
  getEvaluatedFilterReducer() {
    return (evaluatedFilters => {
      // we need to know the first elements booleanCriteria to get the identity element
      // so that we don't influence the result
      const accumulator = isEmpty(evaluatedFilters) ? true : this.getIdentityElement(evaluatedFilters[0].booleanCriteria);

      return reduce((accum, evaluatedFilter) => {

        return this.evaluateBoolean(accum, evaluatedFilter.booleanCriteria, evaluatedFilter.value);

      }, accumulator)(evaluatedFilters);
    });
  }

  getIdentityElement(booleanCriteria) {
    if (booleanCriteria === BOOLEAN_CRITERIA[0]) {
      return true;
    }

    if (booleanCriteria === BOOLEAN_CRITERIA[1]) {
      return false;
    }
  }

  evaluateBoolean(a, booleanCriteria, b) {
    if (booleanCriteria === BOOLEAN_CRITERIA[0]) {
      return a && b;
    }

    if (booleanCriteria === BOOLEAN_CRITERIA[1]) {
      return a || b;
    }
  }

  getRuntimeFiltersFrom(filters = []) {
    return filter(f => f.isRuntimeFilter, filters);
  }

  openRuntimeModal(analysis, filters = []) {
    return new Promise(resolve => {
      this._dialog.openFilterPromptDialog(filters, analysis).afterClosed().subscribe((result) => {
        if (!result) {
          return resolve();
        }
        const nonRuntimeFilters = filter(f => !(f.isRuntimeFilter || f.isGlobalFilter), analysis.sqlBuilder.filters);
        analysis.sqlBuilder.filters = fpPipe(
          // block optional runtime filters that have no model
          filter(({isRuntimeFilter, isOptional, model}) => !(isRuntimeFilter && isOptional && !model)),
          runtimeFilters => [
            ...runtimeFilters,
            ...nonRuntimeFilters
          ]
        )(result.filters);
        // analysis.sqlBuilder.filters = result.filters.concat(
        //   filter(f => !(f.isRuntimeFilter || f.isGlobalFilter), analysis.sqlBuilder.filters)
        // );

        resolve(analysis);
      });
    });
  }

  onApplyFilters(analysis) {
    return result => {
      if (!result) {
        return Promise.reject(new Error('Cancelled'));
      }

      const filterPayload = map(this.frontend2BackendFilter.bind(this)(), result.filters);
      analysis.sqlBuilder.filters = filterPayload.concat(
        filter(f => !(f.isRuntimeFilter || f.isGlobalFilter), analysis.sqlBuilder.filters)
      );

      return analysis;
    };
  }

  getRuntimeFilterValues(analysis) {
    const clone = cloneDeep(analysis);
    const runtimeFilters = this.getRuntimeFiltersFrom(
      get(clone, 'sqlBuilder.filters', [])
    );

    if (!runtimeFilters.length) {
      return Promise.resolve(clone);
    }
    return this.openRuntimeModal(clone, runtimeFilters);
  }
}
