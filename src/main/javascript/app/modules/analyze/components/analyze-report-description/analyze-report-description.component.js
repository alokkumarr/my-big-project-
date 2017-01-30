import template from './analyze-report-description.component.html';

export const AnalyzeReportDescriptionComponent = {
  template,
  bindings: {
    model: '<',
    onSave: '&'
  },
  controller: class AnalyzeDescriptionController {
    constructor($mdDialog) {
      this._$mdDialog = $mdDialog;

      this.dataHolder = {
        description: ''
      };
    }

    $onInit() {
      this.dataHolder.description = this.model.description || '';
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    save() {
      this.onSave({
        $data: {
          description: this.dataHolder.description
        }
      });

      this.cancel();
    }
  }
};
