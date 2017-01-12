import template from './analyze-report-description.component.html';

export const AnalyzeReportDescriptionComponent = {
  template,
  bindings: {
    model: '<'
  },
  controller: class AnalyzeDescriptionController {
    constructor($mdDialog) {
      this._$mdDialog = $mdDialog;
    }

    $onInit() {
      this.tempModel = {
        description: this.model.description || ''
      }
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    save(newDescription) {
      this.model.description = newDescription;
      this._$mdDialog.hide();
    }
  }
};
