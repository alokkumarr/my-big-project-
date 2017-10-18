import * as isEmpty from 'lodash/isEmpty';
import * as map from 'lodash/map';
import * as flatMap from 'lodash/flatMap';
import * as filter from 'lodash/filter';
import * as keys from 'lodash/keys';

import * as template from './analyze-report-detail.component.html';

export const AnalyzeReportDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    source: '&',
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

    loadData(options) {
      return this.source({options});
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
