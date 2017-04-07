import cloneDeep from 'lodash/cloneDeep';
import map from 'lodash/map';
import omit from 'lodash/fp/omit';
import set from 'lodash/fp/set';
import pipe from 'lodash/fp/pipe';

import template from './analyze-filter-sidenav.component.html';
import style from './analyze-filter-sidenav.component.scss';

export const ANALYZE_FILTER_SIDENAV_ID = 'analyze-observe-filter-sidenav';
export const ANALYZE_FILTER_SIDENAV_IDS = {
  designer: 'analyze-observe-filter-sidenav-designer',
  detailPage: 'analyze-observe-filter-sidenav-detail-page'
};

export const AnalyzeFilterSidenavComponent = {
  template,
  styles: [style],
  bindings: {
    placement: '@'
  },
  controller: class AnalyzeFilterSidenavController {
    constructor(FilterService) {
      'ngInject';
      this._FilterService = FilterService;
      this.ANALYZE_FILTER_SIDENAV_IDS = ANALYZE_FILTER_SIDENAV_IDS;
    }

    $onInit() {
      this.id = this.placement === ANALYZE_FILTER_SIDENAV_IDS.detailPage ?
          ANALYZE_FILTER_SIDENAV_IDS.detailPage : ANALYZE_FILTER_SIDENAV_IDS.designer;

      this._FilterService.onOpenFilterSidenav(filters => this.onSidenavOpen(filters), this.id);
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
      this._FilterService.applyFilters(this.filters, this.id);
    }

    clearAllFilters() {
      this._FilterService.clearAllFilters(this.id);
    }
  }
};
