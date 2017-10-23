import * as template from './report-format-dialog.component.html';
import style from './report-format-dialog.component.scss';

export const ReportFormatDialogComponent = {
  bindings: {
    modelData: '<'
  },
  template,
  style: [style],
  controller: class ReportFormatDialogController {
    constructor($mdDialog) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this.format = ({
        column: this.modelData.dataField;//caption
      });

    }

    cancel() {
      this._$mdDialog.cancel();
    }

    apply() {
      this._$mdDialog.hide(this.format);
    }
  }
};
