import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';
import uniq from 'lodash/uniq';
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
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      this.filters = map(this.analysis.report.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
      forEach(this.filters, filter => {
        const items = uniq(map(this.analysis.report.data, datum => datum[filter.name]));
        filter.items = items;
      });
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters)(this.analysis.report.data);
    }

    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters, ANALYZE_FILTER_SIDENAV_IDS.detailPage);
    }

    onApplyFilters(filters) {
      this.filters = filters;
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters)(this.analysis.report.data);
    }

    onClearAllFilters() {
      this.filters = this._FilterService.getFilterClearer()(this.filters);
      this.filteredGridData = this.analysis.report.data;
    }
  }
};
