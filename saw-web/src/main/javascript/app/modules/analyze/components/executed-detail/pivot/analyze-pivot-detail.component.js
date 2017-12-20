import 'devextreme/ui/pivot_grid';
import * as isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import * as template from './analyze-pivot-detail.component.html';

export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor(FilterService, PivotService) {
      'ngInject';
      this._isEmpty = isEmpty;
      this._PivotService = PivotService;
      this._FilterService = FilterService;
      this.pivotGridUpdater = new BehaviorSubject({});
      this.dataSource = {};
    }

    $onInit() {

      this.fields = this._PivotService.artifactColumns2PivotFields()(this.analysis.artifacts[0].columns);
      this.requester.subscribe(requests => this.request(requests));
    }

    setDataSource(store, fields) {
      const parsedFields = this._PivotService.trimSuffixFromPivotFields(fields);
      const {formattedFields, formattedData} = this._PivotService.formatDates(store, parsedFields);
      this.dataSource = new PivotGridDataSource({store: formattedData, fields: formattedFields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource
      });
    }

    updatePivot() {
      this.deNormalizedData = this._PivotService.parseData(this.normalizedData, this.analysis.sqlBuilder);
      this.dataSource.store = this.deNormalizedData;
      const parsedFields = this._PivotService.trimSuffixFromPivotFields(this.fields);
      const {formattedFields, formattedData} = this._PivotService.formatDates(this.deNormalizedData, parsedFields);
      this.dataSource = new PivotGridDataSource({store: formattedData, fields: formattedFields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource,
        sorts: this.sorts
      });
    }

    request({data, exportAnalysis}) {
      /* eslint-disable no-unused-expressions */
      exportAnalysis && this.onExport();

      if (!data) {
        return;
      }

      this.normalizedData = data;
      this.fields = this._PivotService.artifactColumns2PivotFields()(this.analysis.artifacts[0].columns);
      this.updatePivot();
      /* eslint-disable no-unused-expressions */
    }

    onExport() {
      this.pivotGridUpdater.next({
        export: true
      });
    }
  }
};
