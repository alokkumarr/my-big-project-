import template from './analyze-publish-dialog.component.html';
import style from './analyze-publish-dialog.component.scss';

export const AnalyzePublishDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onPublish: '&'
  },
  controller: class AnalyzePublishDialogController {
    constructor($mdDialog, AnalyzeService) {
      'ngInject';

      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this.subjects = ['Anyone at AT&T', 'Only Analysts', 'Only I'];
      this.subject = this.subjects[0];
      this.predicates = ['VIEW', 'FORK_AND_EDIT'];
      this.predicate = this.predicates[0];
      this.scheduleOptions = ['PUBLISH_ONCE', 'SCHEDULE'];
      this.schedule = this.scheduleOptions[0];
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    publish() {
      this._$mdDialog.hide();
    }
  }
};
