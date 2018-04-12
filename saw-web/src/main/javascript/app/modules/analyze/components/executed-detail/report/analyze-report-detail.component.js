import * as isEmpty from 'lodash/isEmpty';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as flatMap from 'lodash/flatMap';

import * as template from './analyze-report-detail.component.html';

export const AnalyzeReportDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    source: '&'
  },
  controller: class AnalyzeReportDetailController {
    constructor(FilterService) {
      'ngInject';
      this._FilterService = FilterService;
      this.filters = [];
    }

    $onInit() {
      this.initAnalysis();
    }

    initAnalysis() {
      this.filters = map(this.analysis.sqlBuilder.filters, this._FilterService.backend2FrontendFilter(this.analysis.artifacts));
      // no culomns if the in query mode
      this.columns = this.analysis.edit ? null : this._getColumns(this.analysis);
      this.showChecked = !(get(this.analysis, 'queryManual'));
    }

    _getColumns(analysis) {
      return flatMap(analysis.artifacts, table => {
        return table.columns;
      });
    }

    loadData(options) {
      return this.source({options});
    }

    $onChanges(data) {
      if (isEmpty(get(data, 'analysis.previousValue'))) {
        return;
      }

      this.initAnalysis();
    }
    // TODO runtime filters in SAW-634

  }
};
