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
        column: this.modelData.dataField
      });

      if (this.modelData.format.type === 'fixedpoint') {
        this.format.CommaSeparator = true;
      } else {
        this.format.CommaSeparator = false;
      }
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    apply() {
      this._$mdDialog.hide(this.format);
    }
  }
};
