import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';
import flatMap from 'lodash/flatMap';
import filter from 'lodash/filter';

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
      this.filters = map(this.analysis.sqlBuilder.filters, this._FilterService.backend2FrontendFilter());
      this.columns = flatMap(this.analysis.artifacts, table => {
        return filter(table.columns, column => column.checked);
      });
      this.dataSubscription = this.requester.subscribe(options => this.onData(options));
    }

    $onDestroy() {
      this.dataSubscription.unsubscribe();
    }

    onData({data}) {
      if (!data) {
        return;
      }

      this.gridData = data;
    }
    // TODO runtime filters in SAW-634

  }
};
