import template from './report-rename-dialog.component.html';

export const ReportRenameDialogComponent = {
  template,
  controller: class ReportRenameDialogController {
    constructor($mdDialog) {
      'ngInject';
      this._$mdDialog = $mdDialog;

      this.dataHolder = {
        newName: ''
      };
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    rename(newName) {
      this._$mdDialog.hide(newName);
    }
  }
};
