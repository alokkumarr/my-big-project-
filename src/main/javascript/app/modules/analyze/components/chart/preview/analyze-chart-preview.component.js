import * as template from './analyze-chart-preview.component.html';
// import style from './analyze-report-preview.component.scss';

export const AnalyzeChartPreviewComponent = {
  template,
  bindings: {
    model: '<'
  },
  controller: class AnalyzeChartPreviewController {
    constructor($componentHandler, $mdDialog, $timeout, AnalyzeService) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;

      this.MORE_ROWS_COUNT = 500;
      this.data = [];
    }

    $onInit() {
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
