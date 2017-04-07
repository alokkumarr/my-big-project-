import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';
import forEach from 'lodash/forEach';

import template from './analyze-report-detail.component.html';

import {ANALYZE_FILTER_SIDENAV_IDS} from '../../analyze-filter-sidenav/analyze-filter-sidenav.component';

export const AnalyzeReportDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzeReportDetailController {
    constructor(FilterService) {
      this._FilterService = FilterService;
      this._isEmpty = isEmpty;
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this.filters = map(this.analysis.report.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
      forEach(this.filters, filter => {
        const items = map(this.analysis.report.data, datum => datum[filter.name]);
        filter.items = items;
      });

    }

    openFilterSidenav() {
      console.log(this.filters);
      this._FilterService.openFilterSidenav(this.filters, ANALYZE_FILTER_SIDENAV_IDS.detailPage);
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(filters);

      this.filterGridData();
    }
  }
};
