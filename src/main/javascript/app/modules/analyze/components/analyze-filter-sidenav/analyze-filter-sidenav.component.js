import cloneDeep from 'lodash/cloneDeep';
import map from 'lodash/map';
import omit from 'lodash/fp/omit';
import set from 'lodash/fp/set';
import pipe from 'lodash/fp/pipe';

import template from './analyze-filter-sidenav.component.html';

export const ANALYZE_FILTER_SIDENAV_ID = 'analyze-observe-filter-sidenav';
export const AnalyzeFilterSidenavComponent = {
  template,
  controller: class AnalyzeFilterSidenavController {
    constructor(FilterService) {
      'ngInject';
      this._FilterService = FilterService;
      this.id = ANALYZE_FILTER_SIDENAV_ID;
    }

    $onInit() {
      this._FilterService.onOpenFilterSidenav(filters => this.onSidenavOpen(filters));
    }

    $onDestroy() {
      this._FilterService.offOpenFilterSidenav();
    }

    onSidenavOpen(filters) {
      this.filters = map(filters, filter => {
        return pipe(
          omit('model'),
          set('model', cloneDeep(filter.model))
        )(filter);
      });
    }

    onFiltersApplied() {
      this._FilterService.applyFilters(this.filters);
    }

    clearAllFilters() {
      this._FilterService.clearAllFilters();
    }
  }
};
