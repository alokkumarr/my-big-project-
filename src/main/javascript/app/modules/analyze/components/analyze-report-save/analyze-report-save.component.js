import template from './analyze-report-save.component.html';
import style from './analyze-report-save.component.scss';

export const AnalyzeReportSaveComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onSave: '&'
  },
  controller: class AnalyzeReportSaveController {
    constructor($mdDialog, $timeout, AnalyzeService) {
      'ngInject';

      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;

      this.dataHolder = {
        payload: null,
        category: null,
        title: '',
        description: '',
        categories: []
      };
    }

    $onInit() {
      this._AnalyzeService.getMenu()
        .then(response => {
          this.dataHolder.categories = response[0].children;
        });

      this.dataHolder.artifacts = this.model.artifacts;
      this.dataHolder.category = this.model.category;
      this.dataHolder.title = this.model.title;
      this.dataHolder.description = this.model.description;
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    save() {
      this.$dialog.showLoader();

      this._$timeout(() => {
        const payload = {
          title: this.dataHolder.title,
          description: this.dataHolder.description,
          category: this.dataHolder.category,
          artifacts: this.dataHolder.artifacts
        };

        this._AnalyzeService.saveReport(payload)
          .then(response => {
            payload.id = response.id;

            this.onSave({
              $data: payload
            });

            this.cancel();
          })
          .finally(() => {
            this.$dialog.hideLoader();
          });
      }, 1000);
    }
  }
};
