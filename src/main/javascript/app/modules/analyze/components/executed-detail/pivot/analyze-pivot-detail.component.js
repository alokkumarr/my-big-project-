import 'devextreme/ui/pivot_grid';
import isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';

import template from './analyze-pivot-detail.component.html';

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
      this.dataSource = new PivotGridDataSource({store, fields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource
      });
    }

    updatePivot() {
      this.deNormalizedData = this._PivotService.denormalizeData(this.normalizedData, this.fields);
      this.deNormalizedData = this._PivotService.takeOutKeywordFromData(this.deNormalizedData);
      this.dataSource.store = this.deNormalizedData;
      this.dataSource = new PivotGridDataSource({store: this.dataSource.store, fields: this.fields});
      this.pivotGridUpdater.next({
        dataSource: this.dataSource,
        sorts: this.sorts
      });
    }

    request({data, exports}) {
      /* eslint-disable no-unused-expressions */
      exports && this.onExport();

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
