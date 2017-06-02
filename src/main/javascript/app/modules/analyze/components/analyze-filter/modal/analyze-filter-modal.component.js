import groupBy from 'lodash/groupBy';
import isEmpty from 'lodash/isEmpty';
import toPairs from 'lodash/toPairs';
import mapValues from 'lodash/mapValues';
import flatten from 'lodash/flatten';
import fpPipe from 'lodash/fp/pipe';
import fpMap from 'lodash/fp/map';

import template from './analyze-filter-modal.component.html';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../../services/filter.service';

export const AnalyzeFilterModalComponent = {
  template,
  bindings: {
    filters: '<',
    artifacts: '<'
  },
  controller: class AnalyzeFlterModalController {
    $onInit() {
      this.filters = this.groupFilters(this.filters);
      this.filters = mapValues(this.filter, artifactFilters => {
        if (isEmpty(artifactFilters)) {
          this.pushNewFilter(artifactFilters);
        }
        return artifactFilters;
      });

      // there is 1 special case when the analysis type is report
      this.analysisType = this.artifacts.length > 1 ? 'report' : '';
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
        model: null
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

    groupFilters(filters) {
      return isEmpty(filters) ?
        {} :
        groupBy(filters, 'column.table');
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
