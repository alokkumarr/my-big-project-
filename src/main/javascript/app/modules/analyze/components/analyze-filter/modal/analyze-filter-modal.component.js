import groupBy from 'lodash/groupBy';
import isEmpty from 'lodash/isEmpty';
import toPairs from 'lodash/toPairs';
import fromPairs from 'lodash/fromPairs';
import forOwn from 'lodash/forOwn';
import flatten from 'lodash/flatten';
import fpPipe from 'lodash/fp/pipe';
import fpMap from 'lodash/fp/map';

import template from './analyze-filter-modal.component.html';
import {DEFAULT_BOOLEAN_CRITERIA, BOOLEAN_CRITERIA} from '../../../services/filter.service';

export const AnalyzeFilterModalComponent = {
  template,
  bindings: {
    filters: '<',
    artifacts: '<'
  },
  controller: class AnalyzeFlterModalController {
    constructor() {
      this.BOOLEAN_CRITERIA = BOOLEAN_CRITERIA;
    }

    $onInit() {
      // there is 1 special case when the analysis type is report
      // and the boolean criteria should be shown
      this.analysisType = this.artifacts.length > 1 ? 'report' : '';

      this.filters = this.groupFilters(this.filters);
      forOwn(this.filters, artifactFilters => {
        if (isEmpty(artifactFilters)) {
          this.pushNewFilter(artifactFilters);
        }
        return artifactFilters;
      });
    }

    addFilter(artifactName) {
      if (!this.filters[artifactName]) {
        this.filters[artifactName] = [];
      }
      this.pushNewFilter(this.filters[artifactName]);
    }

    pushNewFilter(filtersArray) {
      const newFilter = {
        column: null,
        model: null,
        isRuntimeFilter: false
      };
      if (this.analysisType === 'report') {
        newFilter.booleanCriteria = DEFAULT_BOOLEAN_CRITERIA;
      }
      filtersArray.push(newFilter);
    }

    onFilterChange(filter, artifactName, index) {
      this.filters[artifactName][index] = filter;
    }

    onApplyFilters() {
      const flattenedFilters = this.unGroupFilters(this.filters);

      this.$dialog.hide(flattenedFilters);
    }

    onBooleanCriteriaSelected(filter, value) {
      filter.booleanCriteria = value;
    }

    onRuntimeToggle(filter) {
      if (filter.isRuntimeFilter) {
        filter.model = null;
      }
    }

    onRemoveFilter(index, artifactName) {
      this.filters[artifactName].splice(index, 1);
    }

    groupFilters(filters) {
      return isEmpty(filters) ?
        this.getInitialFilters() :
        groupBy(filters, 'column.table');
    }

    getInitialFilters() {
      return fpPipe(
        fpMap(({artifactName}) => {
          return [artifactName, []];
        }),
        fromPairs
      )(this.artifacts);
    }

    unGroupFilters(filters) {
      return fpPipe(
        toPairs,
        fpMap(pair => pair[1]),
        flatten
      )(filters);
    }
  }
};
