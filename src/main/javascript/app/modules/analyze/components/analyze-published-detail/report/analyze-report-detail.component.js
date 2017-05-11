import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';
import uniq from 'lodash/uniq';
import forEach from 'lodash/forEach';

import template from './analyze-report-detail.component.html';

import {ANALYZE_FILTER_SIDENAV_IDS} from '../../analyze-filter/analyze-filter-sidenav.component';

export const AnalyzeReportDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzeReportDetailController {
    constructor(FilterService) {
      'ngInject';
      this._FilterService = FilterService;
      this._isEmpty = isEmpty;
      this.filters = {};
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      this.filters.possible = map(this.analysis.report.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      forEach(this.filters.possible, filter => {
        const items = uniq(map(this.analysis.report.data, datum => datum[filter.name]));
        filter.items = items;
      });
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters.possible)(this.analysis.report.data);

      this.openFilterSidenav();
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
    }

    openFilterSidenav() {
      if (!isEmpty(this.filters.possible)) {
        this._FilterService.openFilterSidenav(this.filters.possible, ANALYZE_FILTER_SIDENAV_IDS.detailPage);
      }
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters.selected)(this.analysis.report.data);
    }

    onClearAllFilters() {
      this.filters.possible = this._FilterService.getFilterClearer()(this.filters.possible);
      this.filters.selected = [];
      this.filteredGridData = this.analysis.report.data;
    }

    onFilterRemoved(filter) {
      filter.model = null;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters.selected)(this.analysis.report.data);
    }
  }
};
