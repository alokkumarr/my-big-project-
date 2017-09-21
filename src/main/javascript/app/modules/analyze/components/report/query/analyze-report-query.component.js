import * as template from './analyze-report-query.component.html';
import style from './analyze-report-query.component.scss';

const WARN_DIALOG = {
  title: 'Are you sure you want to proceed?',
  content: 'If you save changes to sql query, you will not be able to go back to designer view for this analysis.'
};

export const AnalyzeReportQueryComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onSave: '&'
  },
  controller: class AnalyzeReportQueryController {
    constructor(AnalyzeService, $mdDialog) {
      'ngInject';

      this._AnalyzeService = AnalyzeService;
      this._$mdDialog = $mdDialog;
    }

    warnUser() {
      const confirm = this._$mdDialog.confirm()
        .title(WARN_DIALOG.title)
        .textContent(WARN_DIALOG.content)
        .multiple(true)
        .ok('Save')
        .cancel('Cancel');

      return this._$mdDialog.show(confirm);
    }

    doSubmit() {
      this.model.edit = true;
      this.model.queryManual = this.model.query;
      this.onSave({
        analysis: this.model
      });
    }

    submitQuery() {
      if (!this.model.edit) {
        this.warnUser().then(() => {
          this.doSubmit();
        }, () => {
          // do nothing if user hits cancel
        });
      } else {
        this.doSubmit();
      }
    }
  }
};
