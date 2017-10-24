import PivotGridDataSource from 'devextreme/ui/pivot_grid/data_source';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import * as template from './analyze-pivot-preview.component.html';
import style from './analyze-pivot-preview.component.scss';

export const AnalyzePivotPreviewComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzePivotPreviewController {
    constructor($mdDialog, $timeout, AnalyzeService, PivotService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._PivotService = PivotService;
      this.pivotGridUpdater = new BehaviorSubject({});
    }

    $onInit() {
      this.loadData();
    }

    loadData(options = {}) {
      this._AnalyzeService.previewExecution(this.model.pivot, options).then(({data}) => {
        const fields = this._PivotService.artifactColumns2PivotFields()(this.model.pivot.artifacts[0].columns);
        const trimmedFields = this._PivotService.trimSuffixFromPivotFields(fields);
        const parsedData = this._PivotService.parseData(data, this.model.pivot.sqlBuilder);
        this.dataSource = new PivotGridDataSource({store: parsedData, fields: trimmedFields});
        this.pivotGridUpdater.next({
          dataSource: this.dataSource
        });
      });
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
