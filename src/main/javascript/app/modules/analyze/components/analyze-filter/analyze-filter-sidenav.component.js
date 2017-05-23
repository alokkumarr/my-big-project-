import cloneDeep from 'lodash/cloneDeep';
import map from 'lodash/map';
import omit from 'lodash/fp/omit';
import set from 'lodash/fp/set';
import pipe from 'lodash/fp/pipe';

import template from './analyze-filter-sidenav.component.html';

export const ANALYZE_FILTER_SIDENAV_IDS = {
  designer: 'analyze-observe-filter-sidenav-designer',
  detailPage: 'analyze-observe-filter-sidenav-detail-page'
};

export const AnalyzeFilterSidenavComponent = {
  template,
  bindings: {
    placement: '@'
  },
  controller: class AnalyzeFilterSidenavController {
    constructor(FilterService, $element, $document) {
      'ngInject';
      this._$element = $element;
      this._$document = $document;
      this._FilterService = FilterService;
      this.ANALYZE_FILTER_SIDENAV_IDS = ANALYZE_FILTER_SIDENAV_IDS;
    }

    $onInit() {
      if (this.placement === ANALYZE_FILTER_SIDENAV_IDS.detailPage) {
        const elem = this._$element.detach();
        this._$document[0].querySelector('div.root-container').append(elem[0]);
      }

      this.id = this.placement === ANALYZE_FILTER_SIDENAV_IDS.detailPage ?
          ANALYZE_FILTER_SIDENAV_IDS.detailPage : ANALYZE_FILTER_SIDENAV_IDS.designer;

      this._FilterService.onOpenFilterSidenav(filters => this.onSidenavOpen(filters), this.id);
    }

    $onDestroy() {
      this._FilterService.offOpenFilterSidenav();
      if (this.placement === ANALYZE_FILTER_SIDENAV_IDS.detailPage) {
        this._$element.remove();
      }
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
