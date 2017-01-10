import template from './analyze-report-preview.component.html';
import style from './analyze-report-preview.component.scss';

export const AnalyzeReportPreviewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeReportPreviewController {
    constructor($mdDialog) {
      this._$mdDialog = $mdDialog;
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
