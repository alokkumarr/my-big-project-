import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import template from './analyze-report-preview.component.html';
import style from './analyze-report-preview.component.scss';

export const AnalyzeReportPreviewComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzeReportPreviewController {
    constructor($componentHandler, $mdDialog, $timeout, AnalyzeService) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;

      this.MORE_ROWS_COUNT = 500;
      this.requester = new BehaviorSubject({});
    }

    $onInit() {
      // this._$timeout(() => this.reloadPreviewGrid());
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    loadGridData(options = {}) {
      return this._AnalyzeService.previewExecution(this.model.report, options).then(({data, count}) => {
        return {data, count};
      });
    }
  }
};
