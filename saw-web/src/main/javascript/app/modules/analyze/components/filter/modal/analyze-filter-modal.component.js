import * as groupBy from 'lodash/groupBy';
import * as isEmpty from 'lodash/isEmpty';
import * as toPairs from 'lodash/toPairs';
import * as fromPairs from 'lodash/fromPairs';
import * as forOwn from 'lodash/forOwn';
import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as flatten from 'lodash/flatten';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as map from 'lodash/map';
import * as unset from 'lodash/unset';

import * as template from './analyze-filter-modal.component.html';
import style from './analyze-filter-modal.component.scss';
import {BOOLEAN_CRITERIA} from '../../../services/filter.service';
import {OPERATORS} from '../filters/number-filter.component';
import {NUMBER_TYPES, DATE_TYPES} from '../../../consts';

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
    constructor(toastMessage, $translate, FilterService) {
      this._toastMessage = toastMessage;
      this._$translate = $translate;
      this._FilterService = FilterService;
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
      this.removeEmptyFilters(this.filters);
      if (this.areFiltersValid(this.filters)) {
        const flattenedFilters = this.unGroupFilters(this.filters);
        this.cleanFilters(flattenedFilters);
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

    cleanFilters(filters) {
      forEach(filters, filter => {
        if (NUMBER_TYPES.includes(filter.column.type) && filter.model &&
            filter.model.operator !== OPERATORS.BETWEEN.shortName) {
          unset(filter.model, 'otherValue');
        }
      });
    }

    areFiltersValid(filters) {
      let isValid = true;
      forOwn(filters, artifactFilters => {
        forEach(artifactFilters, filter => {
          if (this.isRuntime) {
            isValid = isValid && !this._FilterService.isFilterEmpty(filter);
          } else {
            isValid = isValid && (filter.isRuntimeFilter || !this._FilterService.isFilterEmpty(filter));
          }
          if (this.isDateFilterValid(filter)) {
            isValid = false;
          }
        });
      });

      return isValid;
    }

    isDateFilterValid(filter) {
      return DATE_TYPES.includes(filter.column.type) &&
        (filter.model.preset === 'CUSTOM' &&
        (!filter.model.lte || !filter.model.gte));
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
