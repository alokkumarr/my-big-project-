import * as template from './analyze-card.component.html';
import style from './analyze-card.component.scss';
import * as forEach from 'lodash/forEach';
import cronstrue from 'cronstrue';

export const AnalyzeCardComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onAction: '&',
    highlightTerm: '<',
    cronJobs: '<'
  },
  controller: class AnalyzeCardController {

    constructor($mdDialog, AnalyzeService, $log, AnalyzeActionsService, JwtService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$log = $log;
      this._JwtService = JwtService;

      this.canUserFork = false;
      this.cronReadbleMsg = 'No Schedule Set';
    }

    $onInit() {
      this.canUserFork = this._JwtService.hasPrivilege('FORK', {
        subCategoryId: this.model.categoryId
      });
      forEach(this.cronJobs, cron => {
        if (cron.jobDetails.analysisID === this.model.id) {
          this.cronReadbleMsg = cronstrue.toString(cron.jobDetails.cronExpression);
        }
      });
    }

    showExecutingFlag() {
      return this._AnalyzeService.isExecuting(this.model.id);
    }

    fork() {
      this._AnalyzeActionsService.fork(this.model);
    }

    onSuccessfulDeletion(analysis) {
      this.onAction({
        type: 'onSuccessfulDeletion',
        model: analysis
      });
    }

    onSuccessfulExecution(analysis) {
      this.onAction({
        type: 'onSuccessfulExecution',
        model: analysis
      });
    }

    onSuccessfulPublish(analysis) {
      this.onAction({
        type: 'onSuccessfulPublish',
        model: analysis
      });
    }
  }
};
