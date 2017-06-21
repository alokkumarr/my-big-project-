import groupBy from 'lodash/groupBy';
import isEmpty from 'lodash/isEmpty';
import toPairs from 'lodash/toPairs';
import fromPairs from 'lodash/fromPairs';
import forOwn from 'lodash/forOwn';
import forEach from 'lodash/forEach';
import remove from 'lodash/remove';
import flatten from 'lodash/flatten';
import fpPipe from 'lodash/fp/pipe';
import fpMap from 'lodash/fp/map';
import map from 'lodash/map';

import template from './analyze-filter-modal.component.html';
import style from './analyze-filter-modal.component.scss';
import {BOOLEAN_CRITERIA} from '../../../services/filter.service';

export const AnalyzeFilterModalComponent = {
  template,
  styles: [style],
  bindings: {
    filters: '<',
    artifacts: '<',
    isRuntime: '<?runtime',
    filterBooleanCriteria: '<'
  },
  controller: class AnalyzeFlterModalController {
    constructor(toastMessage, $translate) {
      this._toastMessage = toastMessage;
      this._$translate = $translate;
      this.BOOLEAN_CRITERIA = BOOLEAN_CRITERIA;
    }

    $onInit() {
      this.translateBooleanCriteria();
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

    translateBooleanCriteria() {
      this._$translate(map(this.BOOLEAN_CRITERIA, 'label')).then(translations => {
        forEach(this.BOOLEAN_CRITERIA, criteria => {
          criteria.label = translations[criteria.label];
        });
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
      filtersArray.push(newFilter);
    }

    onFilterChange(filter, artifactName, index) {
      this.filters[artifactName][index] = filter;
    }

    onApplyFilters() {
      if (this.areFiltersValid(this.filters)) {
        this.removeEmptyFilters(this.filters);
        const flattenedFilters = this.unGroupFilters(this.filters);
        this.$dialog.hide({
          filterBooleanCriteria: this.filterBooleanCriteria,
          filters: flattenedFilters
        });
      } else {
        this._$translate('ERROR_FILL_IN_REQUIRED_FILTER_MODELS').then(message => {
          this._toastMessage.error(message);
        });
      }
    }

    areFiltersValid(filters) {
      let isValid = true;
      forOwn(filters, artifactFilters => {
        forEach(artifactFilters, filter => {
          if (filter.column && !filter.model && !filter.isRuntimeFilter) {
            isValid = false;
          }
        });
      });

      return isValid;
    }

    removeEmptyFilters(filters) {
      forOwn(filters, artifactFilters => {
        remove(artifactFilters, filter => {
          return !filter.column;
        });
      });
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
