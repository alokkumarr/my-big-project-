import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';

import template from './analyze-report-detail.component.html';

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
      this.filters = [];
    }

    $onInit() {
      this.filters = map(this.analysis.report.filters, this._FilterService.backend2FrontendFilter());
    }
    // TODO runtime filters in SAW-634

  }
};
