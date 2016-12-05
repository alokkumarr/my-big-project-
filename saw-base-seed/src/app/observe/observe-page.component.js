import map from 'lodash/fp/map';
import filter from 'lodash/fp/filter';
import pipe from 'lodash/fp/pipe';
import get from 'lodash/fp/get';
import indexOf from 'lodash/fp/indexOf';
import lte from 'lodash/lte';
import omit from 'lodash/fp/omit';

import template from './observe-page.component.html';
import {OBSERVE_FILTER_SIDENAV_ID} from './filter-sidenav.component';

export const ObservePageComponent = {
  template,
  controller: class ObserverPageController {
    constructor($log) {
      'ngInject';
      this.$log = $log;
      this.filterSidenavId = OBSERVE_FILTER_SIDENAV_ID;

      this.filters = [];

      this.menu = [{
        name: 'Dashboard 1'
      }, {
        name: 'Dashboard 2'
      }, {
        name: 'Dashboard 3'
      }, {
        name: 'Dashboard 4'
      }];

      this.appliedFilters = [];
    }

    onFiltersApplied(filters) {
      this.appliedFilters = this.appliedFiltersMapper()(filters);
      this.$log.info('filtersApplied: filters: ', this.appliedFilters);
    }

    onFilterRemoved() {
      this.filters = this.removedFilterMapper()(this.filters);
    }

    appliedFiltersMapper() {
      return pipe(
        filter(filter => Boolean(filter.model)),
        map(get('label'))
      );
    }

    removedFilterMapper() {
      return map(filter => {
        const isFilterApplied = lte(0, indexOf(filter.label, this.appliedFilters));
        return isFilterApplied ? filter : omit('model', filter);
      });
    }
  }
};
