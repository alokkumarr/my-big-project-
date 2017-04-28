import map from 'lodash/fp/map';
import filter from 'lodash/fp/filter';
import pipe from 'lodash/fp/pipe';
import get from 'lodash/fp/get';
import indexOf from 'lodash/fp/indexOf';
import lte from 'lodash/lte';
import omit from 'lodash/fp/omit';

import template from './observe-page.component.html';
import style from './observe-page.component.scss';
import {OBSERVE_FILTER_SIDENAV_ID} from '../filter-sidenav/filter-sidenav.component';

export const ObservePageComponent = {
  template,
  styles: [style],
  controller: class ObserverPageController {
    constructor($componentHandler, MenuService) {
      'ngInject';

      this.$componentHandler = $componentHandler;
      this.MenuService = MenuService;

      this.filterSidenavId = OBSERVE_FILTER_SIDENAV_ID;

      // models of fitlers
      this.filters = [];
      // array of strings used fo the chips
      this.appliedFilters = [];
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.MenuService.getMenu('OBSERVE')
        .then(data => {
          leftSideNav.update(data, 'OBSERVE');
        });
    }

    onFiltersApplied(filters) {
      this.appliedFilters = this.appliedFiltersMapper()(filters);
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
