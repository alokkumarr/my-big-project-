import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';
import flatMap from 'lodash/flatMap';
import filter from 'lodash/filter';
import keys from 'lodash/keys';

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
      this.filters = map(this.analysis.sqlBuilder.filters, this._FilterService.backend2FrontendFilter(this.analysis.artifacts));
      this.columns = this._getColumns(this.analysis);
      this.dataSubscription = this.requester.subscribe(options => this.onData(options));
    }

    _getColumns(analysis, data = []) {
      /* If report was using designer mode, find checked columns */
      if (!analysis.edit) {
        return flatMap(analysis.artifacts, table => {
          return filter(table.columns, column => column.checked);
        });
      }

      /* If report was using sql mode, we don't really have any info
         about columns. Keys from individual data nodes are used as
         column names */
      if (data.length > 0) {
        return map(keys(data[0]), col => ({
          label: col,
          columnName: col,
          type: 'string'
        }));
      }

      return this.columns;
    }

    $onDestroy() {
      this.dataSubscription.unsubscribe();
    }

    onData({data}) {
      if (!data) {
        return;
      }

      this.gridData = data;
      this.columns = this._getColumns(this.analysis, data);
    }
    // TODO runtime filters in SAW-634

  }
};
