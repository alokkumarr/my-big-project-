import * as template from './analyze-description-dialog.component.html';
import style from './analyze-description-dialog.component.scss';

export const AnalyzeDescriptionDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onSave: '&'
  },
  controller: class AnalyzeDescriptionDialogController {
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
