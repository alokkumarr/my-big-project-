import * as isEmpty from 'lodash/isEmpty';
import * as map from 'lodash/map';
import * as flatMap from 'lodash/flatMap';
import * as filter from 'lodash/filter';

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
      this._isEmpty = isEmpty;
      this.filters = [];
    }

    $onInit() {
      this.filters = map(this.analysis.sqlBuilder.filters, this._FilterService.backend2FrontendFilter(this.analysis.artifacts));
      this.columns = this._getColumns(this.analysis);
    }

    _getColumns(analysis) {
      return flatMap(analysis.artifacts, table => {
        return filter(table.columns, column => column.checked);
      });
    }

    loadData(options) {
      return this.source({options});
    }
    // TODO runtime filters in SAW-634

  }
};
